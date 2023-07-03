package com.xunmo.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import com.xunmo.entity.DeadConfig;
import com.xunmo.entity.MqConfig;
import com.xunmo.enums.ConsumeAction;
import com.xunmo.enums.ExchangeType;
import com.xunmo.enums.SendAction;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonProps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public class MqHelper {
    private final static AtomicBoolean isInit = new AtomicBoolean(false);
    private final static Object initLock = new Object();
    private static final AtomicInteger sendChannelCount = new AtomicInteger(1);
    private static final AtomicInteger consumerChannelCount = new AtomicInteger(1);
    private static MqPool mqPool;

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
                final MqPoolConfig mqPoolConfig = new MqPoolConfig();
                mqPoolConfig.setHost(cfg.get("xm.mq.host"));
                mqPoolConfig.setUsername(cfg.get("xm.mq.username"));
                mqPoolConfig.setPassword(cfg.get("xm.mq.password"));
                mqPoolConfig.setPort(cfg.getInt("xm.mq.port", 5432));
                mqPoolConfig.setMaxIdle(10);
                mqPoolConfig.setMaxTotal(20);
                mqPoolConfig.setMinIdle(1);
                final MqConnectionPoolObjectFactory mqConnectionPoolObjectFactory = new MqConnectionPoolObjectFactory(mqPoolConfig);
                mqPool = new MqPool(mqConnectionPoolObjectFactory);
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
            final MqPoolConfig mqPoolConfig = new MqPoolConfig();
            mqPoolConfig.setHost(host);
            mqPoolConfig.setUsername(username);
            mqPoolConfig.setPassword(password);
            mqPoolConfig.setPort(port);
            mqPoolConfig.setMaxIdle(10);
            mqPoolConfig.setMaxTotal(20);
            mqPoolConfig.setMinIdle(1);
            final MqConnectionPoolObjectFactory mqConnectionPoolObjectFactory = new MqConnectionPoolObjectFactory(mqPoolConfig);
            mqPool = new MqPool(mqConnectionPoolObjectFactory);
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
            java.util.function.Consumer<SendAction> sendFunc
    ) {

        final String changeName = mqConfig.getChangeName();
        String routingKey = mqConfig.getRoutingKey();
        Boolean durable = mqConfig.getDurable();
        Boolean isDelay = mqConfig.getIsDelay();
        ExchangeType exchangeType = mqConfig.getExchangeType();
        Long delayTime = mqConfig.getDelayTime();
        Boolean isAutoClose = mqConfig.getIsAutoClose();
        final String queueName = mqConfig.getQueueName();
        final Long ttl = mqConfig.getTtl();
        final Long max = mqConfig.getMax();
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
        Channel channel = null;
        //建立通讯信道
        try {
            // 注意，因为要等待broker的confirm消息，暂时不关闭channel和connection
            channel = connection.createChannel();
            sendChannelCount.incrementAndGet();
            channel.confirmSelect();// 开启发送方确认模式

            Boolean finalIsAutoClose = isAutoClose;
            Channel finalChannel = channel;
            final ConfirmListener confirmListener = new ConfirmListener() {
                // 消息正确到达 broker
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("已收到消息，标识：{}", deliveryTag);
                    //做一些其他处理
                    sendFunc.accept(SendAction.SUCCESS);
                    if (finalIsAutoClose) {
                        closeChannelAndConnection(finalChannel, connection);
                    } else {
                        closeConnection(connection);
                    }
                    countChannel(sendChannelCount, finalChannel);
                }

                // RabbitMQ 因为自身内部错误导致消息丢失，就会发送一条nack消息
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.warn("未确认消息，标识：{}", deliveryTag);
                    //做一些其他处理，比如消息重发等
                    sendFunc.accept(SendAction.MQ_FAIL);
                    if (finalIsAutoClose) {
                        closeChannelAndConnection(finalChannel, connection);
                    } else {
                        closeConnection(connection);
                    }
                    countChannel(sendChannelCount, finalChannel);
                }
            };
            channel.addConfirmListener(confirmListener);

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
            channel.queueDeclare(queueName, durable, false, false, args);

            AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
            if (expiration != null) {
                props.expiration(expiration.toString());
            }
            props.deliveryMode(2); // message持久化
            if (isDelay) {
                // 绑定路由
                channel.queueBind(queueName, changeName, routingKey);
                Map<String, Object> headers = new HashMap<>();
                headers.put("x-delay", delayTime);
                props.headers(headers);
                AMQP.BasicProperties properties = props.build();
                channel.basicPublish(changeName, routingKey, properties, msg.getBytes(StandardCharsets.UTF_8));
            } else {
                if (StrUtil.isNotBlank(changeName)) {
                    AMQP.BasicProperties properties = props.build();
                    channel.basicPublish(changeName, routingKey, properties, msg.getBytes(StandardCharsets.UTF_8));
                } else {
                    props.priority(null);
                    AMQP.BasicProperties properties = props.build();
                    channel.basicPublish(routingKey, queueName, properties, msg.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            if (isAutoClose) {
                closeChannelAndConnection(channel, connection);
            } else {
                closeConnection(connection);
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
                                   Function<String, ConsumeAction> successMessage) throws IOException {
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
        Channel channel = null;
        try {
            channel = connection.createChannel();
            consumerChannelCount.incrementAndGet();
            Map<String, Object> args = new HashMap<>();
            if (deadConfig != null) {
                final String deadChangeName = deadConfig.getChangeName();
                final String deadQueueName = deadConfig.getQueueName();
                String deadRoutingKey = deadConfig.getRoutingKey();
                if (StrUtil.isBlankOrUndefined(deadRoutingKey)) {
                    deadRoutingKey = "#";
                }
                channel.queueDeclare(deadQueueName, durable, false, false, args); //获取队列
                args.put("x-dead-letter-exchange", deadChangeName);
                args.put("x-dead-letter-routing-key", deadRoutingKey);

                if (!StrUtil.isBlankOrUndefined(deadChangeName)) {
                    channel.exchangeDeclare(deadChangeName, exchangeType.name(), durable);
                    // 绑定
                    channel.queueBind(deadQueueName, deadChangeName, deadRoutingKey);
                }
            }

            channel.queueDeclare(queueName, durable, false, false, args); //获取队列
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
                channel.exchangeDeclare(changeName, exchangeType.name(), durable);
                // 绑定路由
                channel.queueBind(queueName, changeName, routingKey);
            }
            channel.basicQos(0, 1, false); //分发机制为触发式

            //建立消费者
            boolean finalIsAutoClose = isAutoClose;
            Channel finalChannel = channel;
            Consumer consumer = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    long deliveryTag = envelope.getDeliveryTag();
                    ConsumeAction consumeResult = ConsumeAction.RETRY;
                    try {
                        String message = new String(body, StandardCharsets.UTF_8);
                        consumeResult = successMessage.apply(message);
                        //消息丢到死信 或 从死信直接丢弃
                        if (consumeResult == ConsumeAction.ACCEPT) {
                            // 确认消息消费
                            finalChannel.basicAck(deliveryTag, false);
                        } else {
                            finalChannel.basicNack(deliveryTag, false, consumeResult == ConsumeAction.RETRY); //消息重回队列
                        }
                        if (finalIsAutoClose) {
                            closeChannelAndConnection(finalChannel, connection);
                        } else {
                            closeConnection(connection);
                        }
                        countChannel(consumerChannelCount, finalChannel);
                    } catch (Exception e) {
                        log.error("异常: {}", ExceptionUtil.stacktraceToString(e));
                        if (finalIsAutoClose) {
                            closeChannelAndConnection(finalChannel, connection);
                        } else {
                            closeConnection(connection);
                        }
                        countChannel(consumerChannelCount, finalChannel);
                        throw new RuntimeException(e);
                    }
                }
            };
            // 从左到右参数意思分别是：队列名称、是否读取消息后直接删除消息，消费者
            channel.basicConsume(queueName, false, consumer);
        } catch (Exception ex) {
            log.error("异常: {}", ExceptionUtil.stacktraceToString(ex));
            if (isAutoClose) {
                closeChannelAndConnection(channel, connection);
            } else {
                closeConnection(connection);
            }
            countChannel(consumerChannelCount, channel);
            throw ex;
        }
    }

    private static void countChannel(AtomicInteger channelCount, Channel channel) {
        ThreadUtil.execute(() -> {
            while (true) {
                try {
                    if (!channel.isOpen()) {
                        channelCount.decrementAndGet();
                    }
                } catch (Exception e) {
                    throw e;
                }
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
    public static void sendMsg(MqConfig mqConfig, String msg, java.util.function.Consumer<SendAction> sendFunc) {
        final Connection connection = mqPool.getConnection();
        sendMsg(connection, mqConfig, msg, null, sendFunc);
    }


    public static void consumeMsg(MqConfig mqConfig,
                                  Function<String, ConsumeAction> successMessage) throws IOException {
        final Connection connection = mqPool.getConnection();
        consumeMsg(connection, mqConfig,
                successMessage);
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
        final Connection connection = mqPool.getConnection();
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
            mqPool.returnConnection(connection);
        }
        return msgCount;
    }

    private static void closeChannelAndConnection(Channel channel, Connection connection) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        if (connection != null) {
            mqPool.returnConnection(connection);
        }
    }


    private static void closeConnection(Connection connection) {
        if (connection != null) {
            mqPool.returnConnection(connection);
        }
    }

    public static AtomicInteger getSendChannelCount() {
        return sendChannelCount;
    }

    public static AtomicInteger getConsumerChannelCount() {
        return consumerChannelCount;
    }
}
