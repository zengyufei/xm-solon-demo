package com.xunmo.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import com.xunmo.rabbitmq.MqConnectionPool;
import com.xunmo.rabbitmq.MqConnectionPoolConfig;
import com.xunmo.rabbitmq.MqConnectionPoolObjectFactory;
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
    private static final Map<Channel, Integer> channelUseCountMap = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<Channel> stayCloseList = new ConcurrentLinkedQueue<>();
    private static final List<String> sendExistsChannelNames = new CopyOnWriteArrayList<>();
    private static final List<String> consumerExistsChannelNames = new CopyOnWriteArrayList<>();
    private static final List<String> closeChannelNames = new CopyOnWriteArrayList<>();
    private static MqConnectionPool mqConnectionPool;

    static {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (CollUtil.isNotEmpty(stayCloseList)) {
                    while (stayCloseList.size() > 2) {
                        final Channel channel = stayCloseList.poll();
                        closeChannel(channel);
                    }
                }
            }
        }, 1000L, 10000L);
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
                final MqConnectionPoolConfig mqPoolConfig = new MqConnectionPoolConfig();
                mqPoolConfig.setHost(cfg.get("xm.mq.host"));
                mqPoolConfig.setUsername(cfg.get("xm.mq.username"));
                mqPoolConfig.setPassword(cfg.get("xm.mq.password"));
                mqPoolConfig.setPort(cfg.getInt("xm.mq.port", 5432));
                mqPoolConfig.setMaxIdle(10);
                mqPoolConfig.setMaxTotal(20);
                mqPoolConfig.setMinIdle(1);
                final MqConnectionPoolObjectFactory mqConnectionPoolObjectFactory = new MqConnectionPoolObjectFactory(mqPoolConfig);
                mqConnectionPool = new MqConnectionPool(mqConnectionPoolObjectFactory);
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
            // 设置服务地址
            // 设置账号信息，用户名、密码、vhost
            final MqConnectionPoolConfig mqPoolConfig = new MqConnectionPoolConfig();
            mqPoolConfig.setHost(host);
            mqPoolConfig.setUsername(username);
            mqPoolConfig.setPassword(password);
            mqPoolConfig.setPort(port);
            mqPoolConfig.setMaxIdle(10);
            mqPoolConfig.setMaxTotal(20);
            mqPoolConfig.setMinIdle(1);
            final MqConnectionPoolObjectFactory mqConnectionPoolObjectFactory = new MqConnectionPoolObjectFactory(mqPoolConfig);
            mqConnectionPool = new MqConnectionPool(mqConnectionPoolObjectFactory);
            isInit.compareAndSet(false, true);
        }
    }


    /**
     * 发送消息
     *
     * @param connection 连接
     */
    private static void sendMsg(
            Connection connection,
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

        Channel channel = getSendChannel(connection, newConfig);
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
            if (channel.isOpen()) {
                final Integer count = channelUseCountMap.computeIfAbsent(channel, k -> 0);
                channelUseCountMap.put(channel, count + 1);
            }

            if (isAutoClose) {
                closeChannelAndConnection(channel, connection);
            } else {
                if (connection != null) {
                    closeConnection(connection);
                }
            }
            countChannel(sendChannelCount, channel);
        }
    }


    /**
     * 消费消息
     *
     * @param connection     连接
     * @param successMessage 消息处理函数
     * @throws IOException      io异常
     * @throws TimeoutException 超时异常
     */
    private static void consumeMsg(Connection connection,
                                   MqConfig mqConfig,
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

        final Channel channel = getConsumeChannel(connection, newConfig);
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
            if (channel.isOpen()) {
                final Integer count = channelUseCountMap.computeIfAbsent(channel, k -> 0);
                channelUseCountMap.put(channel, count + 1);
            }

            if (isAutoClose) {
                closeChannelAndConnection(channel, connection);
            } else {
                if (connection != null) {
                    closeConnection(connection);
                }
            }
            countChannel(consumerChannelCount, channel);
        }
    }

    private static Channel getSendChannel(Connection connection, MqConfig mqConfig) throws IOException {
        Channel channel;
        if (channelMap.containsKey(mqConfig)) {
            final List<Channel> channelList = channelMap.get(mqConfig);
            channel = getActiveChannel(channelList);
            if (channel == null) {
                channel = initSendChannel(connection, mqConfig);
            } else if (channelUseCountMap.containsKey(channel) && channelUseCountMap.get(channel) > channelUseMaxCount) {
                final String title = mqConfig.getTitle();
                if (channel.isOpen()) {
                    log.warn("{} send channel {} 超出使用次数限制, 重新制作新的 channel", title, channel.getChannelNumber());
                } else {
                    log.warn("{} send channel 超出使用次数限制, 重新制作新的 channel", title);
                }
                Channel oldChannel = channel;
                channel = initSendChannel(connection, mqConfig);
                channelUseCountMap.remove(oldChannel);
                stayCloseList.add(oldChannel);
            }
        } else {
            channel = initSendChannel(connection, mqConfig);
        }
        return channel;
    }

    private static Channel getConsumeChannel(Connection connection, MqConfig mqConfig) throws IOException {
        Channel channel;
        if (channelMap.containsKey(mqConfig)) {
            final List<Channel> channelList = channelMap.get(mqConfig);
            channel = getActiveChannel(channelList);
            if (channel == null) {
                channel = initConsumeChannel(connection, mqConfig);
            } else if (channelUseCountMap.containsKey(channel) && channelUseCountMap.get(channel) > channelUseMaxCount) {
                final String title = mqConfig.getTitle();
                if (channel.isOpen()) {
                    log.warn("{} consume channel {} 超出使用次数限制, 重新制作新的 channel", title, channel.getChannelNumber());
                } else {
                    log.warn("{} consume channel 超出使用次数限制, 重新制作新的 channel", title);
                }
                Channel oldChannel = channel;
                channel = initConsumeChannel(connection, mqConfig);
                channelUseCountMap.remove(oldChannel);
                stayCloseList.add(oldChannel);
            }
        } else {
            channel = initConsumeChannel(connection, mqConfig);
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


    private static Channel initSendChannel(Connection connection, MqConfig mqConfig) throws IOException {
        final String changeName = mqConfig.getChangeName();
        final String queueName = mqConfig.getQueueName();
        final String routingKey = mqConfig.getRoutingKey();
        final Boolean durable = mqConfig.getDurable();
        final Boolean isDelay = mqConfig.getIsDelay();
        final ExchangeType exchangeType = mqConfig.getExchangeType();
        final Long ttl = mqConfig.getTtl();
        final Long max = mqConfig.getMax();
        final DeadConfig deadConfig = mqConfig.getDeadConfig();

        Channel channel;
        channel = tryReturn(() -> {
            try {
                return connection.createChannel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> "获取 channel 异常");
        final ArrayList<Channel> channels = new ArrayList<>();
        channels.add(channel);
        channelMap.put(mqConfig, channels);

        // 注意，因为要等待broker的confirm消息，暂时不关闭channel和connection
        final String channelString = channel.toString();
        sendExistsChannelNames.add(channelString);

        sendChannelCount.incrementAndGet();

        Map<String, Object> args = new HashMap<>();
        // 队列设置最大长度
        if (max != null) {
            args.put("x-max-length", max);
        }
        //设置队列有效期为10秒
        if (ttl != null) {
            args.put("x-message-ttl", ttl);
        }
        if (deadConfig != null) {
            final String deadChangeName = deadConfig.getChangeName();
            String deadRoutingKey = deadConfig.getRoutingKey();
            if (StrUtil.isBlankOrUndefined(deadRoutingKey)) {
                deadRoutingKey = "#";
            }
            args.put("x-dead-letter-exchange", deadChangeName);
            args.put("x-dead-letter-routing-key", deadRoutingKey);
        }

        if (isDelay) {
            // 延迟交换器  https://xie.infoq.cn/article/e0c56c9d10a047fb179bc3aba
            args.put("x-delayed-type", exchangeType.name());
            // exchange 持久化
            channel.exchangeDeclare(changeName, "x-delayed-message", durable, false, args);
        } else if (StrUtil.isNotBlank(changeName)) {
            /*
             * 声明一个交换机
             * 参数1：交换机的名称，取值任意
             * 参数2：交换机的类型，取值为：direct、fanout、topic、headers
             * 参数3：是否为持久化的交换机
             * 注意：
             *      1) 声明交换机时，如果这个交换机已经存在，则放弃声明；如果交换机不存在，则声明该交换机
             *      2) 这行代码可有可无，但是使用前要确保该交换机已存在
             */
            channel.exchangeDeclare(changeName, exchangeType.name(), durable, false, args);
            /*
             * 将队列绑定到交换机
             * 参数1：队列的名称
             * 参数2：交换机的名称
             * 参数3：消息的RoutingKey（就是BindingKey）
             * 注意：
             *      1) 在进行队列和交换机绑定时，必须要确保交换机和队列已经成功声明
             */
            channel.queueBind(queueName, changeName, routingKey);
        }

        // 参数从前面开始分别意思为：队列名称，是否持久化，独占的队列，不使用时是否自动删除，其他参数
        tryDo(() -> channel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列异常");
        channel.confirmSelect();// 开启发送方确认模式
        return channel;
    }


    private static Channel initConsumeChannel(Connection connection, MqConfig mqConfig) throws IOException {
        final String changeName = mqConfig.getChangeName();
        final String queueName = mqConfig.getQueueName();
        final String routingKey = mqConfig.getRoutingKey();
        final Boolean durable = mqConfig.getDurable();
        final Boolean isDelay = mqConfig.getIsDelay();
        final ExchangeType exchangeType = mqConfig.getExchangeType();
        final Long ttl = mqConfig.getTtl();
        final Long max = mqConfig.getMax();
        final DeadConfig deadConfig = mqConfig.getDeadConfig();

        Channel channel;
        channel = tryReturn(() -> {
            try {
                return connection.createChannel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> "获取 channel 异常");
        final ArrayList<Channel> channels = new ArrayList<>();
        channels.add(channel);
        channelMap.put(mqConfig, channels);


        final String channelString = channel.toString();
        consumerExistsChannelNames.add(channelString);
        consumerChannelCount.incrementAndGet();
        Map<String, Object> args = new HashMap<>();
        if (deadConfig != null) {
            final String deadChangeName = deadConfig.getChangeName();
            final String deadQueueName = deadConfig.getQueueName();
            String deadRoutingKey = deadConfig.getRoutingKey();
            if (StrUtil.isBlankOrUndefined(deadRoutingKey)) {
                deadRoutingKey = "#";
            }

            tryDo(() -> channel.queueDeclare(deadQueueName, durable, false, false, args), () -> "获取队列异常");


            args.put("x-dead-letter-exchange", deadChangeName);
            args.put("x-dead-letter-routing-key", deadRoutingKey);

            if (!StrUtil.isBlankOrUndefined(deadChangeName)) {
                tryDo(() -> channel.exchangeDeclare(deadChangeName, exchangeType.name(), durable), () -> "创建死信交换机失败");
                String finalDeadRoutingKey = deadRoutingKey;
                tryDo(() -> channel.queueBind(deadQueueName, deadChangeName, finalDeadRoutingKey), () -> "绑定死信交换机和死信队列失败");
            }
        }

        tryDo(() -> channel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列失败");
        if (StrUtil.isNotBlank(changeName)) {
            /*
             * 声明一个交换机
             * 参数1：交换机的名称，取值任意
             * 参数2：交换机的类型，取值为：direct、fanout、topic、headers
             * 参数3：是否为持久化的交换机
             * 注意：
             *      1) 声明交换机时，如果这个交换机已经存在，则放弃声明；如果交换机不存在，则声明该交换机
             *      2) 这行代码可有可无，但是使用前要确保该交换机已存在
             */
            tryDo(() -> channel.exchangeDeclare(changeName, exchangeType.name(), durable), () -> "创建交换机失败");
            // 绑定路由
            tryDo(() -> channel.queueBind(queueName, changeName, routingKey), () -> "绑定交换机和队列失败");
        }
        channel.basicQos(0, 1, false); //分发机制为触发式
        return channel;
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

    /**
     * 发送消息
     *
     * @param msg 消息
     * @return {@link String}
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static void sendMsg(MqConfig mqConfig, String msg, java.util.function.BiConsumer<Channel, SendAction> sendFunc) throws IOException {
        final Connection connection = getMqPoolConnection();
        sendMsg(connection, mqConfig, msg, null, sendFunc);
    }

    public static void consumeMsg(MqConfig mqConfig, BiFunction<Channel, String, ConsumeAction> successMessage) throws IOException {
        final Connection connection = getMqPoolConnection();
        consumeMsg(connection, mqConfig, successMessage);
    }

    private static Connection getMqPoolConnection() {
        return mqConnectionPool.getConnection();
    }

    /**
     * 获取消息数
     *
     * @param queueName 队列名称
     * @return int
     * @throws IOException      io异常
     * @throws TimeoutException 超时异常
     */
    public static int getMessageCount(String queueName) throws IOException, TimeoutException {
        final Connection connection = getMqPoolConnection();
        return getMessageCount(connection, queueName);
    }

    /**
     * 获取消息数
     *
     * @param connection 连接
     * @param queueName  队列名称
     * @return int
     * @throws IOException      io异常
     * @throws TimeoutException 超时异常
     */
    public static int getMessageCount(Connection connection, String queueName) throws IOException, TimeoutException {
        int msgCount = 0;
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, true, false, false, null); //获取队列
            msgCount = (int) channel.messageCount(queueName);
        } finally {
            closeConnection(connection);
        }
        return msgCount;
    }

    private static void closeChannelAndConnection(Channel channel, Connection connection) {
        String channelString = null;
        if (channel != null) {
            channelString = channel.toString();
        }
        while (channel == null || !channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (channelString != null) {
            closeChannelNames.add(channelString);
        }
        log.trace("关闭channel");
        if (connection != null) {
            closeConnection(connection);
        }
    }

    private static void closeChannel(Channel channel) {
        String channelString = null;
        if (channel != null) {
            channelString = channel.toString();
        }
        while (channel == null || !channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (channelString != null) {
            closeChannelNames.add(channelString);
        }
        log.trace("关闭channel");
    }

    private static void closeConnection(Connection connection) {
        if (connection != null) {
            closeConnec(connection);
        }
    }

    private static void closeConnec(Connection connection) {
        mqConnectionPool.returnConnection(connection);
    }

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
    interface TryDoing {
        void tryDo() throws IOException;
    }
}
