package com.xunmo.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import com.xunmo.rabbitmq.*;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class MqHelper {
    private final static int tryCountMax = 3;
    private final static int channelUseMaxCount = 30;
    private final static AtomicBoolean isInit = new AtomicBoolean(false);
    private final static Object initLock = new Object();
    private static final AtomicInteger sendChannelCount = new AtomicInteger(1);
    private static final AtomicInteger consumerChannelCount = new AtomicInteger(1);
    private static final Map<MqConfig, List<Channel>> channelMap = new ConcurrentHashMap<>();
    private static final List<String> sendExistsChannelNames = new CopyOnWriteArrayList<>();
    private static final List<String> consumerExistsChannelNames = new CopyOnWriteArrayList<>();
    private static final List<String> closeChannelNames = new CopyOnWriteArrayList<>();
    private static MqChannelPool mqChannelPool;

    static {
    }

    // 构造方法私有化 防止直接通过类创建实例
    private MqHelper() {
    }

    public static synchronized void initMq() {
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
                final MqChannelPoolConfig mqChannelPoolConfig = new MqChannelPoolConfig();
                mqChannelPoolConfig.setHost(cfg.get("xm.mq.host"));
                mqChannelPoolConfig.setUsername(cfg.get("xm.mq.username"));
                mqChannelPoolConfig.setPassword(cfg.get("xm.mq.password"));
                mqChannelPoolConfig.setPort(cfg.getInt("xm.mq.port", 5432));
                mqChannelPoolConfig.setMaxIdle(10);
                mqChannelPoolConfig.setMaxTotal(20);
                mqChannelPoolConfig.setMinIdle(1);
                final MqChannelPoolObjectFactory mqChannelPoolObjectFactory = new MqChannelPoolObjectFactory(mqChannelPoolConfig);
                mqChannelPool = new MqChannelPool(mqChannelPoolObjectFactory);
                isInit.compareAndSet(false, true);
            }
        }
    }

    public static synchronized void initMq(
            String host,
            String username,
            String password,
            int port) {
        if (isInit.get()) {
            return;
        }
        synchronized (initLock) {
            if (isInit.get()) {
                return;
            }

            // 设置账号信息，用户名、密码、vhost
            final MqChannelPoolConfig mqChannelPoolConfig = new MqChannelPoolConfig();
            mqChannelPoolConfig.setHost(host);
            mqChannelPoolConfig.setUsername(username);
            mqChannelPoolConfig.setPassword(password);
            mqChannelPoolConfig.setPort(port);
            mqChannelPoolConfig.setMaxIdle(10);
            mqChannelPoolConfig.setMaxTotal(20);
            mqChannelPoolConfig.setMinIdle(1);
            final MqChannelPoolObjectFactory mqChannelPoolObjectFactory = new MqChannelPoolObjectFactory(mqChannelPoolConfig);
            mqChannelPool = new MqChannelPool(mqChannelPoolObjectFactory);
            isInit.compareAndSet(false, true);
        }
    }


    /**
     * 发送消息
     *
     */
    public static void sendMsg(
            MqConfig mqConfig,
            String msg,
            java.util.function.BiConsumer<Channel, SendAction> sendFunc
    ) throws IOException {
        sendMsg(mqConfig, msg, null, sendFunc);
    }

    /**
     * 发送消息
     *
     */
    private static void sendMsg(
            MqConfig mqConfig,
            String msg,
            Integer expiration,
            java.util.function.BiConsumer<Channel, SendAction> sendFunc
    ) throws IOException {

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
        } else {
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

        Channel channel = getSendChannel(newConfig);
        final ConfirmListener confirmListener = new ConfirmListener() {
            // 消息正确到达 broker
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                log.debug("已收到消息，标识：{}", deliveryTag);
                //做一些其他处理
                sendFunc.accept(channel, SendAction.SUCCESS);
            }

            // RabbitMQ 因为自身内部错误导致消息丢失，就会发送一条nack消息
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                log.warn("未确认消息，标识：{}", deliveryTag);
                //做一些其他处理，比如消息重发等
                sendFunc.accept(channel, SendAction.MQ_FAIL);
            }
        };
        channel.clearConfirmListeners();
        channel.addConfirmListener(confirmListener);

        //建立通讯信道
        try {
            AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
            if (expiration != null) {
                props.expiration(expiration.toString());
            }
            props.deliveryMode(2); // message持久化
            String finalRoutingKey = routingKey;
            final String sendErrorMsg = "发送消息异常";
            if (isDelay) {
                // 绑定路由
                channel.queueBind(queueName, changeName, routingKey);
                Map<String, Object> headers = new HashMap<>();
                headers.put("x-delay", delayTime);
                props.headers(headers);
                tryDo(() -> channel.basicPublish(changeName, finalRoutingKey, props.build(), msg.getBytes(StandardCharsets.UTF_8)),
                        () -> sendErrorMsg);
            } else {
                if (StrUtil.isNotBlank(changeName)) {
                    tryDo(() -> channel.basicPublish(changeName, finalRoutingKey, props.build(), msg.getBytes(StandardCharsets.UTF_8)),
                            () -> sendErrorMsg);
                } else {
                    props.priority(null);
                    tryDo(() -> channel.basicPublish(finalRoutingKey, queueName, props.build(), msg.getBytes(StandardCharsets.UTF_8)),
                            () -> sendErrorMsg);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (isAutoClose) {
            } else {
            }
            countChannel(sendChannelCount, channel);
        }
    }


    /**
     * 消费消息
     *
     * @param successMessage 消息处理函数
     * @throws IOException      io异常
     * @throws TimeoutException 超时异常
     */
    public static void consumeMsg(MqConfig mqConfig,
                                   BiFunction<Channel, String, ConsumeAction> successMessage) throws IOException {
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
//        if (!isAutoClose) {
//            log.info("rabbitMq 消费启动, 监听队列:{}, 持久化:{}", queueName, (durable ? "是" : "否"));
//        }

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

        final Channel channel = getConsumeChannel(newConfig);
        try {
            //建立消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    long deliveryTag = envelope.getDeliveryTag();
                    ConsumeAction consumeResult = ConsumeAction.RETRY;
                    try {
                        String message = new String(body, StandardCharsets.UTF_8);
                        consumeResult = successMessage.apply(channel, message);
                        //消息丢到死信 或 从死信直接丢弃
                        if (consumeResult == ConsumeAction.ACCEPT) {
                            // 确认消息消费
                            channel.basicAck(deliveryTag, false);
                        } else {
                            channel.basicNack(deliveryTag, false, consumeResult == ConsumeAction.RETRY); //消息重回队列
                        }
                    } catch (Exception e) {
                        log.error("异常: {}", ExceptionUtil.stacktraceToString(e));
                        throw new RuntimeException(e);
                    }
                }
            };
            // 从左到右参数意思分别是：队列名称、是否读取消息后直接删除消息，消费者
            tryDo(() -> channel.basicConsume(queueName, false, consumer), () -> "消费回信应答失败");
        } catch (Exception ex) {
            log.error("异常: {}", ExceptionUtil.stacktraceToString(ex));
            throw ex;
        } finally {
            if (isAutoClose) {
            } else {
            }
            countChannel(consumerChannelCount, channel);
        }
    }

    private static Channel getSendChannel(MqConfig mqConfig) throws IOException {
        mqConfig.setType("0");
        Channel channel;
        if (channelMap.containsKey(mqConfig)) {
            final List<Channel> channelList = channelMap.get(mqConfig);
            channel = getActiveChannel(channelList);
            if (channel == null) {
                channel = mqChannelPool.getChannel(mqConfig);
            }
        } else {
            channel = mqChannelPool.getChannel(mqConfig);
        }
        return channel;
    }

    private static Channel getConsumeChannel(MqConfig mqConfig) throws IOException {
        mqConfig.setType("1");
        Channel channel;
        if (channelMap.containsKey(mqConfig)) {
            final List<Channel> channelList = channelMap.get(mqConfig);
            channel = getActiveChannel(channelList);
            if (channel == null) {
                channel = mqChannelPool.getChannel(mqConfig);
            }
        } else {
            channel = mqChannelPool.getChannel(mqConfig);
        }
        return channel;
    }

    private static Channel getActiveChannel(final List<Channel> channelList) {
        final Iterator<Channel> iterator = channelList.iterator();
        while (iterator.hasNext()) {
            final Channel next = iterator.next();
            if (next.isOpen()) {
                return next;
            } else {
                iterator.remove();
            }
        }
        return null;
    }


    private static void tryDo(TryDoing tryDoing, Supplier<String> errorMsg) {
        int tryCount = 3;
        do {
            try {
                tryDoing.tryDo();
                break;
            } catch (IOException e) {
                log.error("{}, 重试第{}次获取!", errorMsg.get(), (tryCountMax - tryCount));
                tryCount--;
            }
        } while (tryCount > 0);
    }

    private static <T> T tryReturn(Supplier<T> tryDoing, Supplier<String> errorMsg) throws IOException {
        final String s = errorMsg.get();
        int tryCount = 3;
        do {
            try {
                return tryDoing.get();
            } catch (Exception e) {
                log.error("{}, 重试第{}次获取!", s, (tryCountMax - tryCount));
                tryCount--;
            }
        } while (tryCount > 0);

        throw new IOException(StrUtil.format("无法继续执行, 因为{} 错误!", s));
    }

    private static void countChannel(AtomicInteger channelCount, Channel channel) {
        ThreadUtil.execute(() -> {
            int count = 3;
            while (true) {
                if (count <= 0) {
                    break;
                }
                if (channel == null || !channel.isOpen()) {
                    channelCount.decrementAndGet();
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count--;
            }
        });
    }
//
//    /**
//     * 获取消息数
//     *
//     * @param queueName  队列名称
//     * @return int
//     * @throws IOException      io异常
//     * @throws TimeoutException 超时异常
//     */
//    public static int getMessageCount(String queueName) throws IOException {
//        int msgCount = 0;
//        Channel channel = mqChannelPool.getChannel();
//        channel.queueDeclare(queueName, true, false, false, null); //获取队列
//        msgCount = (int) channel.messageCount(queueName);
//        return msgCount;
//    }


    public static AtomicInteger getConsumerChannelCount() {
        return consumerChannelCount;
    }

    public static List<String> getCloseChannelNames() {
        return closeChannelNames;
    }

    public static List<String> getConsumerExistsChannelNames() {
        return consumerExistsChannelNames;
    }

    public static List<String> getSendExistsChannelNames() {
        return sendExistsChannelNames;
    }


    public static Map<MqConfig, List<Channel>> getChannelMap() {
        return channelMap;
    }

    @FunctionalInterface
   public interface TryDoing {
        public void tryDo() throws IOException;
    }
}
