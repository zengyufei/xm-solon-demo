package com.xunmo.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import com.xunmo.rabbitmq.MqChannelPool;
import com.xunmo.rabbitmq.MqChannelPoolConfig;
import com.xunmo.rabbitmq.entity.DeadConfig;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ConsumeAction;
import com.xunmo.rabbitmq.enums.ExchangeType;
import com.xunmo.rabbitmq.enums.SendAction;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonProps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class MqHelper {

	private final static int tryCountMax = 6;

	private final static AtomicBoolean isInit = new AtomicBoolean(false);

	private final static Object initLock = new Object();

	private static final Map<MqConfig, MqChannelPool> poolMap = new ConcurrentHashMap<>();

	private static MqChannelPoolConfig mqChannelPoolConfig;

	static {
	}

	// 构造方法私有化 防止直接通过类创建实例
	private MqHelper() {
	}

	public static synchronized void initFromSolon() {
		if (isInit.get()) {
			return;
		}
		synchronized (initLock) {
			if (isInit.get()) {
				return;
			}
			final SolonApp app = Solon.app();
			if (app != null) {
				final SolonProps cfg = Solon.cfg();

				// 设置账号信息，用户名、密码、vhost
				mqChannelPoolConfig = new MqChannelPoolConfig();
				mqChannelPoolConfig.setHost(cfg.get("xm.mq.host"));
				mqChannelPoolConfig.setUsername(cfg.get("xm.mq.username"));
				mqChannelPoolConfig.setPassword(cfg.get("xm.mq.password"));
				mqChannelPoolConfig.setPort(cfg.getInt("xm.mq.port", 5432));
				mqChannelPoolConfig.setMaxIdle(10);
				mqChannelPoolConfig.setMaxTotal(20);
				mqChannelPoolConfig.setMinIdle(1);
				isInit.compareAndSet(false, true);
			}
		}
	}

	public static synchronized void initArgs(String host, String username, String password, int port) {
		if (isInit.get()) {
			return;
		}
		synchronized (initLock) {
			if (isInit.get()) {
				return;
			}

			// 设置账号信息，用户名、密码、vhost
			mqChannelPoolConfig = new MqChannelPoolConfig();
			mqChannelPoolConfig.setHost(host);
			mqChannelPoolConfig.setUsername(username);
			mqChannelPoolConfig.setPassword(password);
			mqChannelPoolConfig.setPort(port);
			mqChannelPoolConfig.setMaxIdle(10);
			mqChannelPoolConfig.setMaxTotal(20);
			mqChannelPoolConfig.setMinIdle(1);
			isInit.compareAndSet(false, true);
		}
	}

	/**
	 * 发送消息
	 */
	public static void sendMsg(MqConfig mqConfig, String msg,
			java.util.function.BiConsumer<Channel, SendAction> sendFunc) throws IOException, TimeoutException {
		sendMsg(mqConfig, msg, null, sendFunc);
	}

	/**
	 * 发送消息
	 */
	private static void sendMsg(MqConfig mqConfig, String msg, Integer expiration,
			java.util.function.BiConsumer<Channel, SendAction> sendFunc) throws IOException, TimeoutException {

		final String changeName = mqConfig.getChangeName();
		String routingKey = mqConfig.getRoutingKey();
		Boolean durable = mqConfig.getDurable();
		Boolean isDelay = mqConfig.getIsDelay();
		ExchangeType exchangeType = mqConfig.getExchangeType();
		Long delayTime = mqConfig.getDelayTime();
		Boolean isAutoClose = mqConfig.getIsAutoClose();
		final String queueName = mqConfig.getQueueName();
		final DeadConfig deadConfig = mqConfig.getDeadConfig();

		if (exchangeType == null) {
			exchangeType = ExchangeType.direct;
		}
		if (durable == null) {
			durable = true;
		}
		if (isAutoClose == null) {
			isAutoClose = false;
		}
		if (StrUtil.isBlankOrUndefined(routingKey)) {
			routingKey = "#";
		}
		if (isDelay == null) {
			isDelay = false;
		}
		else {
			if (isDelay && delayTime == null) {
				delayTime = 1000L;
			}
			if (isDelay && StrUtil.isBlankOrUndefined(changeName)) {
				throw new NullPointerException("延迟队列, 交换机不能为空");
			}
		}

		final MqConfig newConfig = MqConfig.of()
			.title(mqConfig.getTitle())
			.changeName(changeName)
			.queueName(mqConfig.getQueueName())
			.routingKey(routingKey)
			.durable(durable)
			.ttl(mqConfig.getTtl())
			.max(mqConfig.getMax())
			.exchangeType(exchangeType)
			.isAutoClose(isAutoClose)
			.isDelay(isDelay)
			.delayTime(delayTime)
			.deadConfig(deadConfig)
			.build();

		AtomicReference<Channel> channelRef = new AtomicReference<>(getSendChannel(newConfig));
		final ConfirmListener confirmListener = new ConfirmListener() {
			// 消息正确到达 broker
			@Override
			public void handleAck(long deliveryTag, boolean multiple) {
				log.debug("已收到消息，标识：{}", deliveryTag);
				// 做一些其他处理
				sendFunc.accept(channelRef.get(), SendAction.SUCCESS);
			}

			// RabbitMQ 因为自身内部错误导致消息丢失，就会发送一条nack消息
			@Override
			public void handleNack(long deliveryTag, boolean multiple) {
				log.warn("未确认消息，标识：{}", deliveryTag);
				// 做一些其他处理，比如消息重发等
				sendFunc.accept(channelRef.get(), SendAction.MQ_FAIL);
			}
		};
		channelRef.get().clearConfirmListeners();
		channelRef.get().addConfirmListener(confirmListener);

		// 建立通讯信道
		try {
			AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
			props.deliveryMode(2); // message持久化
			if (expiration != null) {
				props.expiration(expiration.toString());
			}
			String firstName;
			String secondName;
			if (isDelay) {
				Map<String, Object> headers = new HashMap<>();
				headers.put("x-delay", delayTime);
				props.headers(headers);
				firstName = changeName;
				secondName = routingKey;
			}
			else {
				if (StrUtil.isNotBlank(changeName)) {
					firstName = changeName;
					secondName = routingKey;
				}
				else {
					firstName = routingKey;
					secondName = queueName;
					props.priority(null);
				}
			}
			String finalFirstName = firstName;
			String finalSecondName = secondName;
			tryDo(() -> {
				log.trace("系统发送发送消息前记录: {} {}", channelRef.get().getChannelNumber(), msg);
				channelRef.get()
					.basicPublish(finalFirstName, finalSecondName, props.build(), msg.getBytes(StandardCharsets.UTF_8));
				log.trace("系统发送发送消息: {} {}", channelRef.get().getChannelNumber(), msg);
			}, () -> "发送消息异常", () -> channelRef.set(getSendChannel(newConfig)));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			returnChannel(newConfig, channelRef.get());
		}
	}

	/**
	 * 消费消息
	 * @param successMessage 消息处理函数
	 */
	public static void consumeMsg(MqConfig mqConfig, BiFunction<Channel, String, ConsumeAction> successMessage)
			throws IOException, TimeoutException {
		ExchangeType exchangeType = mqConfig.getExchangeType();
		Boolean durable = mqConfig.getDurable();
		String routingKey = mqConfig.getRoutingKey();
		Boolean isAutoClose = mqConfig.getIsAutoClose();
		final String changeName = mqConfig.getChangeName();
		final String queueName = mqConfig.getQueueName();
		final DeadConfig deadConfig = mqConfig.getDeadConfig();

		if (exchangeType == null) {
			exchangeType = ExchangeType.direct;
		}
		if (durable == null) {
			durable = true;
		}
		if (isAutoClose == null) {
			isAutoClose = false;
		}
		if (StrUtil.isBlankOrUndefined(routingKey)) {
			routingKey = "#";
		}
		// if (!isAutoClose) {
		// log.info("rabbitMq 消费启动, 监听队列:{}, 持久化:{}", queueName, (durable ? "是" : "否"));
		// }

		final MqConfig newConfig = MqConfig.of()
			.title(mqConfig.getTitle())
			.changeName(changeName)
			.queueName(mqConfig.getQueueName())
			.routingKey(routingKey)
			.durable(durable)
			.ttl(mqConfig.getTtl())
			.max(mqConfig.getMax())
			.exchangeType(exchangeType)
			.isAutoClose(isAutoClose)
			.deadConfig(deadConfig)
			.build();

		AtomicReference<Channel> channelRef = new AtomicReference<>(getConsumeChannel(newConfig));
		try {
			// 建立消费者
			Consumer consumer = new DefaultConsumer(channelRef.get()) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) {
					long deliveryTag = envelope.getDeliveryTag();
					AtomicReference<ConsumeAction> consumeResultRef = new AtomicReference<>(ConsumeAction.RETRY);
					try {
						String message = new String(body, StandardCharsets.UTF_8);
						consumeResultRef.set(successMessage.apply(channelRef.get(), message));
						// 消息丢到死信 或 从死信直接丢弃
						if (consumeResultRef.get() == ConsumeAction.ACCEPT) {
							tryDo(() -> channelRef.get().basicAck(deliveryTag, false), () -> "确认消息消费失败",
									() -> channelRef.set(getConsumeChannel(newConfig)));
						}
						else {
							tryDo(() -> channelRef.get()
								.basicNack(deliveryTag, false, consumeResultRef.get() == ConsumeAction.RETRY),
									() -> "消息重回队列失败失败", () -> channelRef.set(getConsumeChannel(newConfig)));
						}
					}
					catch (Exception e) {
						log.error("异常: {}", ExceptionUtil.stacktraceToString(e));
						throw new RuntimeException(e);
					}
				}
			};
			// 从左到右参数意思分别是：队列名称、是否读取消息后直接删除消息，消费者
			tryDo(() -> channelRef.get().basicConsume(queueName, false, consumer), () -> "消费回信应答失败",
					() -> channelRef.set(getConsumeChannel(newConfig)));
		}
		catch (Exception ex) {
			log.error("异常: {}", ExceptionUtil.stacktraceToString(ex));
			throw ex;
		}
		finally {
			returnChannel(newConfig, channelRef.get());
		}
	}

	private static Channel getSendChannel(MqConfig mqConfig) throws IOException, TimeoutException {
		mqConfig.setType("0");
		MqChannelPool pool = getMqChannelPool(mqConfig);
		return pool.getChannel();
	}

	private static Channel getConsumeChannel(MqConfig mqConfig) throws IOException, TimeoutException {
		mqConfig.setType("1");
		MqChannelPool pool = getMqChannelPool(mqConfig);
		return pool.getChannel();
	}

	private static MqChannelPool getMqChannelPool(MqConfig mqConfig) throws IOException, TimeoutException {
		MqChannelPool pool;
		if (poolMap.containsKey(mqConfig)) {
			pool = poolMap.get(mqConfig);
		}
		else {
			pool = new MqChannelPool(mqChannelPoolConfig, mqConfig);
			poolMap.put(mqConfig, pool);
		}
		return pool;
	}

	private static void returnChannel(MqConfig mqConfig, Channel channel) throws IOException, TimeoutException {
		MqChannelPool pool = getMqChannelPool(mqConfig);
		pool.returnChannel(channel);
	}

	public static void tryDo(TryDoing tryDoing, Supplier<String> errorMsg)
			throws TimeoutException, IOException, AlreadyClosedException {
		int tryCount = tryCountMax;
		do {
			try {
				tryDoing.tryDo();
				break;
			}
			catch (AlreadyClosedException e) {
				log.error("{}, 重连重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			catch (IOException e) {
				log.error("{}, 连接重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			catch (TimeoutException e) {
				log.error("{}, 超时重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			try {
				TimeUnit.SECONDS.sleep(3);
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		while (tryCount > 0);
	}

	public static void tryDo(TryDoing tryDoing, Supplier<String> errorMsg, TryDoing tryDoing2)
			throws IOException, TimeoutException, AlreadyClosedException {
		int tryCount = tryCountMax;
		do {
			try {
				tryDoing.tryDo();
				break;
			}
			catch (AlreadyClosedException e) {
				log.error("{}, 重连重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
				tryDoing2.tryDo();
				break;
			}
			catch (IOException e) {
				log.error("{}, 连接重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			catch (TimeoutException e) {
				log.error("{}, 超时重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			try {
				TimeUnit.SECONDS.sleep(3);
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		while (true);
	}

	public static <T> T tryReturn(TryReturn<T> tryDoing, Supplier<String> errorMsg) throws Exception {
		int tryCount = tryCountMax;
		do {
			try {
				return tryDoing.get();
			}
			catch (Exception e) {
				log.error("{}, 重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
				tryCount--;
				if (tryCount <= 0) {
					throw e;
				}
			}
			try {
				TimeUnit.SECONDS.sleep(3);
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		while (true);
	}

	//
	// /**
	// * 获取消息数
	// *
	// * @param queueName 队列名称
	// * @return int
	// * @throws IOException io异常
	// * @throws TimeoutException 超时异常
	// */
	// public static int getMessageCount(String queueName) throws IOException {
	// int msgCount = 0;
	// Channel channel = mqChannelPool.getChannel();
	// channel.queueDeclare(queueName, true, false, false, null); //获取队列
	// msgCount = (int) channel.messageCount(queueName);
	// return msgCount;
	// }

	@FunctionalInterface
	public interface TryDoing {

		void tryDo() throws IOException, TimeoutException;

	}

	@FunctionalInterface
	public interface TryReturn<T> {

		T get() throws IOException, TimeoutException;

	}

}
