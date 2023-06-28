package com.xunmo.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonProps;

import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

@Slf4j
public class MqHelper {

    private final static Object initLock = new Object();
    private final static Object freeConnLock = new Object();
    private final static Object addConnLock = new Object();

    private static boolean isInit = false;

    public static int DEFAULT_MAX_CONNECTION_COUNT = 30; // 默认最大保持可用连接数
    private final static int DEFAULT_MAX_CONNECTION_USING_COUNT = 300; //默认最大连接可访问次数
    public static int DEFAULT_RETRY_CONNECTION_COUNT = 1; // 默认重试连接次数

    private static volatile int maxConnectionCount;
    private static Semaphore mqConnectionPoolSemaphore;

    private final static ConcurrentLinkedQueue<Connection> freeConnectionQueue = new ConcurrentLinkedQueue<>();
    private final static Map<Connection, Boolean> busyConnectionDic = new ConcurrentHashMap<>();
    private final static Map<Connection, Integer> mqConnectionPoolUsingDicNew = new ConcurrentHashMap<>();//连接池使用率

    private static String host;
    private static String username;
    private static String password;
    private static String port;
    private static String vhost;

    public static synchronized void initMq() {
        if (isInit) {
            return;
        }
        synchronized (initLock) {
            final SolonApp app = Solon.app();
            if (app != null) {
                final SolonProps cfg = Solon.cfg();
                maxConnectionCount = cfg.getInt("xm.mq.max-conn", DEFAULT_MAX_CONNECTION_COUNT);
            } else {
                maxConnectionCount = DEFAULT_MAX_CONNECTION_COUNT;
            }
            //信号量，控制同时并发可用线程数
            mqConnectionPoolSemaphore = new Semaphore(maxConnectionCount);
            isInit = true;
        }
    }

    public static synchronized void initMq(
            String host,
            String username,
            String password,
            String port) {
        MqHelper.initMq(host, username, password, port, null);
    }

    public static synchronized void initMq(
            String host,
            String username,
            String password,
            String port,
            String vhost) {
        if (isInit) {
            return;
        }
        final SolonApp app = Solon.app();
        if (app != null) {
            final SolonProps cfg = Solon.cfg();
            maxConnectionCount = cfg.getInt("xm.mq.max-conn", DEFAULT_MAX_CONNECTION_COUNT);
        } else {
            MqHelper.host = host;
            MqHelper.username = username;
            MqHelper.password = password;
            MqHelper.port = port;
            MqHelper.vhost = vhost;
            maxConnectionCount = DEFAULT_MAX_CONNECTION_COUNT;
        }
        //信号量，控制同时并发可用线程数
        mqConnectionPoolSemaphore = new Semaphore(maxConnectionCount);//信号量，控制同时并发可用线程数
        isInit = true;
    }


    /**
     * 建立连接
     *
     * @return {@link ConnectionFactory}
     */
    private static ConnectionFactory crateFactory() {
        String[] mqConnectionSetting = getMqConnectionSetting();
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置服务地址
        connectionFactory.setHost(mqConnectionSetting[0]);
        // 设置账号信息，用户名、密码、vhost
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername(mqConnectionSetting[1]);
        connectionFactory.setPassword(mqConnectionSetting[2]);
        if (mqConnectionSetting.length > 4) {
            //端口
            connectionFactory.setPort(Convert.toInt(mqConnectionSetting[3]));
            final String virtualHost = mqConnectionSetting[4];
            if (StrUtil.isNotBlank(virtualHost)) {
                connectionFactory.setVirtualHost(virtualHost);
            }
        } else if (mqConnectionSetting.length > 3) {
            //端口
            connectionFactory.setPort(Convert.toInt(mqConnectionSetting[3]));
        }
        return connectionFactory;
    }

    /**
     * 获取mq配置
     *
     * @return {@link String[]}
     */
    private static String[] getMqConnectionSetting() {
        final SolonApp app = Solon.app();
        if (app != null) {
            final SolonProps cfg = Solon.cfg();
            host = cfg.get("xm.mq.host");
            username = cfg.get("xm.mq.username");
            password = cfg.get("xm.mq.password");
            port = cfg.get("xm.mq.port");
            vhost = cfg.get("xm.mq.vhost");
            return new String[]{
                    host,
                    username,
                    password,
                    port,
                    vhost,
            };
        } else {
            return new String[]{
                    host,
                    username,
                    password,
                    port,
                    vhost,
            };
        }
    }


    /**
     * 创建mq连接
     *
     * @return {@link Connection}
     * @throws IOException      ioexception
     * @throws TimeoutException 超时异常
     */
    public static Connection createMqConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = crateFactory();
        factory.setAutomaticRecoveryEnabled(true); // 自动重连
        return factory.newConnection();
    }


    /**
     * 新创建mq连接池
     *
     * @return {@link Connection}
     * @throws InterruptedException 中断异常
     * @throws IOException          ioexception
     * @throws TimeoutException     超时异常
     */
    public static Connection createMqConnectionInPoolNew() throws InterruptedException, IOException, TimeoutException {
        initMq();
        mqConnectionPoolSemaphore.tryAcquire(10000, TimeUnit.MINUTES);// 当 令牌数 < maxConnectionCount 时，会直接进入，否则会等待直到空闲连接出现
        Connection mqConnection = null;

        try {
            // 空闲 + 在忙 = 已有连接数
            // 如果 已有连接数 < 最大可用连接数，则直接创建新连接
            if ((freeConnectionQueue.size() + busyConnectionDic.size()) < maxConnectionCount) {
                synchronized (addConnLock) {
                    if ((freeConnectionQueue.size() + busyConnectionDic.size()) < maxConnectionCount) {
                        mqConnection = createMqConnection();
                        busyConnectionDic.put(mqConnection, true);// 加入到忙连接集合中
                        mqConnectionPoolUsingDicNew.put(mqConnection, 1);
                        return mqConnection;
                    }
                }
            }
            // 分支：尝试从空间池里取出
            // 如果空闲池没有连接了，说明都在忙
            // 如果没有可用空闲连接，则重新进入， mqConnectionPoolSemaphore.acquire() 会阻塞，等待排队领取令牌
            mqConnection = freeConnectionQueue.peek();
            if (mqConnection == null || !freeConnectionQueue.remove(mqConnection)) {
                return createMqConnectionInPoolNew();
            } else if (mqConnectionPoolUsingDicNew.get(mqConnection) + 1 > DEFAULT_MAX_CONNECTION_USING_COUNT || !mqConnection.isOpen()) {
                // 如果取到空闲连接，判断是否使用次数是否超过最大限制, 超过则释放连接并重新创建
                if (mqConnection.isOpen()) {
                    mqConnection.close();
                }

                log.debug("close > DefaultMaxConnectionUsingCount mqConnection,freeConnectionCount:{}, busyConnectionCount:{}", freeConnectionQueue.size(), busyConnectionDic.size());
                mqConnectionPoolUsingDicNew.remove(mqConnection);
                mqConnection = createMqConnection();
                mqConnectionPoolUsingDicNew.put(mqConnection, 0);
                log.debug("create new mqConnection,freeConnectionCount:{}, busyConnectionCount:{}", freeConnectionQueue.size(), busyConnectionDic.size());
            }

            busyConnectionDic.put(mqConnection, true);//加入到忙连接集合中
            mqConnectionPoolUsingDicNew.put(mqConnection, mqConnectionPoolUsingDicNew.get(mqConnection) + 1);//使用次数加1

            log.debug("set busyConnectionDic:{},freeConnectionCount:{}, busyConnectionCount:{}", mqConnection.hashCode(), freeConnectionQueue.size(), busyConnectionDic.size());

            return mqConnection;
        } catch (Exception e) {
            //如果在创建连接发生错误，则判断当前是否已获得Connection，如果获得则释放连接，最终都会释放连接池计数
            if (mqConnection != null) {
                resetMQConnectionToFree(mqConnection);
            } else {
                mqConnectionPoolSemaphore.release();
            }
            throw e;
        }
    }

    private static void resetMQConnectionToFree(Connection connection) throws IOException {
        synchronized (freeConnLock) {
            boolean result = busyConnectionDic.remove(connection);
            //从忙队列中取出
            if (result) {
                log.debug("set freeConnectionQueue:{}, freeConnectionCount:{}, busyConnectionCount:{}", connection.hashCode(), freeConnectionQueue.size(), busyConnectionDic.size());
            } else {
                log.debug("failed tryRemove busyConnectionDic:{}, freeConnectionCount:{}, busyConnectionCount:{}", connection.hashCode(), freeConnectionQueue.size(), busyConnectionDic.size());
                //若极小概率移除失败，则再重试一次
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                result = busyConnectionDic.remove(connection);
            }

            if (result) {
                // 如果因为高并发出现极少概率的>maxConnectionCount，则直接释放该连接
                if ((freeConnectionQueue.size() + busyConnectionDic.size()) > maxConnectionCount) {
                    connection.close();
                } else if (connection.isOpen()) {
                    // 如果是 OPEN 状态才加入空闲队列，否则直接丢弃
                    // 加入到空闲队列，以便持续提供连接服务
                    freeConnectionQueue.offer(connection);
                }

                //释放一个空闲连接信号
                mqConnectionPoolSemaphore.release();

                log.debug("Enqueue freeConnectionQueue:{},freeConnectionCount:{}, busyConnectionCount:{}", connection.hashCode(), freeConnectionQueue.size(), busyConnectionDic.size());

            }
        }
    }

    public enum ConsumeAction {
        ACCEPT,  // 消费成功
        RETRY,   // 消费失败，可以放回队列重新消费
        REJECT,  // 消费失败，直接丢弃
    }


    /**
     * 发送消息
     *
     * @param queueName 队列名称
     * @param msg       消息
     * @param durable   是否持久化
     * @return {@link String}
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static String sendMsg(String queueName, String msg, Boolean durable) throws IOException, TimeoutException, InterruptedException {
        final Connection connection = MqHelper.createMqConnectionInPoolNew();
        return sendMsg(connection, queueName, msg, durable);
    }


    /**
     * 发送消息
     *
     * @param queueName 队列名称
     * @param msg       消息
     * @return {@link String}
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static String sendMsg(String queueName, String msg) throws IOException, TimeoutException, InterruptedException {
        final Connection connection = MqHelper.createMqConnectionInPoolNew();
        return sendMsg(connection, queueName, msg, true);
    }

    /**
     * 发送消息
     *
     * @param connection 连接
     * @param queueName  队列名称
     * @param msg        消息
     * @return {@link String}
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static String sendMsg(Connection connection, String queueName, String msg) throws IOException, TimeoutException, InterruptedException {
        return sendMsg(connection, queueName, msg, true);
    }

    /**
     * 发送消息
     *
     * @param connection 连接
     * @param queueName  队列名称
     * @param durable    是否持久化
     * @param msg        消息
     * @return {@link String}
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static String sendMsg(Connection connection, String queueName, String msg, Boolean durable) throws IOException, TimeoutException, InterruptedException {
        if (durable == null) {
            durable = true;
        }
        boolean reTry = false;
        int reTryCount = 0;
        String sendErrMsg = null;

        do {
            reTry = false;
            //建立通讯信道
            try (Channel channel = connection.createChannel()) {
                // 延迟交换器  https://xie.infoq.cn/article/e0c56c9d10a047fb179bc3aba
                final String changeName = "exchange.normal";
                Map<String, Object> args = new HashMap<>();
                args.put("x-delayed-type", "direct");
                channel.exchangeDeclare(changeName, "x-delayed-message", true, false, args);

                // 参数从前面开始分别意思为：队列名称，是否持久化，独占的队列，不使用时是否自动删除，其他参数
                channel.queueDeclare(queueName, durable, false, false, null);
                // 绑定路由
                channel.queueBind(queueName, changeName, "");

                AMQP.BasicProperties properties = null;

                final boolean isDelay = true;
                if (isDelay) {
                    AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
                    Map<String, Object> headers = new HashMap<>();
                    headers.put("x-delay", 1 * 1000);
                    props.headers(headers);
                    properties = props.build();
                }
                channel.basicPublish(changeName, "", properties, msg.getBytes(StandardCharsets.UTF_8));
                sendErrMsg = "";
            } catch (Exception ex) {
                if (ex instanceof SocketException) {
                    if ((++reTryCount) <= DEFAULT_RETRY_CONNECTION_COUNT) {
                        //可重试1次
                        resetMQConnectionToFree(connection);
                        connection = createMqConnectionInPoolNew();
                        reTry = true;
                    }
                }
                sendErrMsg = ex.getMessage();
            } finally {
                if (!reTry) {
                    resetMQConnectionToFree(connection);
                }
            }
        } while (reTry);

        return sendErrMsg;
    }


    /**
     * 消费消息
     *
     * @param queueName   队列名称
     * @param isAutoClose 是否关闭持续监听
     * @param dealMessage 消息处理函数
     * @throws IOException          io异常
     * @throws InterruptedException 中断异常
     * @throws TimeoutException     超时异常
     */
    public static void consumeMsg(String queueName, boolean isAutoClose, Function<String, ConsumeAction> dealMessage) throws IOException, InterruptedException, TimeoutException {
        final Connection connection = MqHelper.createMqConnectionInPoolNew();
        consumeMsg(connection, queueName, true, isAutoClose, dealMessage);
    }


    /**
     * 消费消息
     *
     * @param queueName   队列名称
     * @param durable     是否持久化
     * @param isAutoClose 是否关闭持续监听
     * @param dealMessage 消息处理函数
     * @throws IOException          io异常
     * @throws InterruptedException 中断异常
     * @throws TimeoutException     超时异常
     */
    public static void consumeMsg(String queueName, boolean durable, boolean isAutoClose, Function<String, ConsumeAction> dealMessage) throws IOException, InterruptedException, TimeoutException {
        final Connection connection = MqHelper.createMqConnectionInPoolNew();
        consumeMsg(connection, queueName, durable, isAutoClose, dealMessage);
    }

    /**
     * 消费消息
     *
     * @param connection  连接
     * @param queueName   队列名称
     * @param durable     是否持久化
     * @param isAutoClose 是否关闭持续监听
     * @param dealMessage 消息处理函数
     * @throws IOException      io异常
     * @throws TimeoutException 超时异常
     */
    public static void consumeMsg(Connection connection, String queueName, boolean durable, boolean isAutoClose, Function<String, ConsumeAction> dealMessage) throws IOException {
        if (!isAutoClose) {
            log.info("rabbitMq 消费启动, 监听队列:{}, 持久化:{}", queueName, (durable ? "是" : "否"));
        }
        try {
            Channel channel = connection.createChannel();

            final String changeName = "exchange.normal";
            channel.queueDeclare(queueName, durable, false, false, null); //获取队列
            // 绑定路由
            channel.queueBind(queueName, changeName, "");

            channel.basicQos(0, 1, false); //分发机制为触发式
            //建立消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    long deliveryTag = envelope.getDeliveryTag();
                    ConsumeAction consumeResult = ConsumeAction.RETRY;
                    try {
                        String message = new String(body, StandardCharsets.UTF_8);
//                        System.out.println("Customer Received '" + message + "'");
                        consumeResult = dealMessage.apply(message);
                    } catch (Exception e) {
                        if (isAutoClose) {
                            try {
                                channel.close();
                            } catch (TimeoutException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        log.error("异常: {}", ExceptionUtil.stacktraceToString(e));
                        throw new RuntimeException(e);
                    }

                    if (consumeResult == ConsumeAction.ACCEPT) {
                        // 确认消息消费
                        channel.basicAck(deliveryTag, false);
                    } else if (consumeResult == ConsumeAction.RETRY) {
                        channel.basicNack(deliveryTag, false, true); //消息重回队列
                    } else {
                        channel.basicNack(deliveryTag, false, false); //消息直接丢弃
                    }
                    if (isAutoClose) {
                        try {
                            channel.close();
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            // 从左到右参数意思分别是：队列名称、是否读取消息后直接删除消息，消费者
            channel.basicConsume(queueName, false, consumer);
        } catch (Exception ex) {
            log.error("QueueName:" + queueName, ex);
            throw ex;
        } finally {
            if (isAutoClose) {
                resetMQConnectionToFree(connection);
            }
        }
    }


    /**
     * 获取消息数
     *
     * @param QueueName 队列名称
     * @return int
     * @throws IOException          ioexception
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static int getMessageCount(String QueueName) throws IOException, TimeoutException, InterruptedException {
        return getMessageCount(createMqConnectionInPoolNew(), QueueName);
    }


    /**
     * 获取消息数
     *
     * @param connection 连接
     * @param QueueName  队列名称
     * @return int
     * @throws IOException          io异常
     * @throws TimeoutException     超时异常
     * @throws InterruptedException 中断异常
     */
    public static int getMessageCount(Connection connection, String QueueName) throws IOException, TimeoutException, InterruptedException {
        int msgCount = 0;
        boolean reTry = false;
        int reTryCount = 0;

        do {
            reTry = false;
            Channel channel = connection.createChannel();
            try {
                channel.queueDeclare(QueueName, true, false, false, null); //获取队列
                msgCount = (int) channel.messageCount(QueueName);
            } catch (Exception ex) {
                if (ex instanceof SocketException) {
                    if ((++reTryCount) <= DEFAULT_RETRY_CONNECTION_COUNT)//可重试1次
                    {
                        resetMQConnectionToFree(connection);
                        connection = createMqConnectionInPoolNew();
                        reTry = true;
                    }
                }

                throw ex;
            } finally {
                channel.close();
                if (!reTry) {
                    resetMQConnectionToFree(connection);
                }
            }
        } while (reTry);

        return msgCount;
    }

}
