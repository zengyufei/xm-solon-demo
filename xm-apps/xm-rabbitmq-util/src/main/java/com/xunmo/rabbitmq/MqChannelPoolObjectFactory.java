package com.xunmo.rabbitmq;

import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xunmo.pool.BasePooledObjectFactory;
import com.xunmo.pool.DestroyMode;
import com.xunmo.pool.PooledObject;
import com.xunmo.pool.impl.DefaultPooledObject;
import com.xunmo.rabbitmq.entity.DeadConfig;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ExchangeType;
import com.xunmo.utils.MqHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MqChannelPoolObjectFactory extends BasePooledObjectFactory<Channel, Exception> {
    @Getter
    private MqChannelPoolConfig mqChannelPoolConfig;
    private MqConfig            mqConfig;
    private Connection          connection;

    public MqChannelPoolObjectFactory(MqChannelPoolConfig mqChannelPoolConfig, MqConfig mqConfig) throws IOException, TimeoutException {
        super();
        this.mqChannelPoolConfig = mqChannelPoolConfig;
        this.mqConfig = mqConfig;

        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置服务地址
        connectionFactory.setHost(mqChannelPoolConfig.getHost());
        // 设置账号信息，用户名、密码、vhost
        connectionFactory.setUsername(mqChannelPoolConfig.getUsername());
        connectionFactory.setPassword(mqChannelPoolConfig.getPassword());
        connectionFactory.setPort(mqChannelPoolConfig.getPort());
        connectionFactory.setConnectionTimeout(300);
        connectionFactory.setChannelRpcTimeout(300);
        connectionFactory.setAutomaticRecoveryEnabled(true); // 自动重连
        connectionFactory.setTopologyRecoveryEnabled(true);
        initConn(connectionFactory);
    }

    private void initConn(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
        MqHelper.tryDo(() -> connection = connectionFactory.newConnection(), () -> "获取 connection 失败, 可能超时");
    }


    @Override
    public Channel create() throws Exception {
        log.trace("在对象池中创建 Channel 对象");
        final Channel channel = getChannel();

        if (mqConfig.getType().equals("0")) {
            final String       changeName   = mqConfig.getChangeName();
            final String       queueName    = mqConfig.getQueueName();
            final String       routingKey   = mqConfig.getRoutingKey();
            final Boolean      durable      = mqConfig.getDurable();
            final Boolean      isDelay      = mqConfig.getIsDelay();
            final ExchangeType exchangeType = mqConfig.getExchangeType();
            final Long         ttl          = mqConfig.getTtl();
            final Long         max          = mqConfig.getMax();
            final DeadConfig   deadConfig   = mqConfig.getDeadConfig();

            final ArrayList<Channel> channels = new ArrayList<>();
            channels.add(channel);

            // 注意，因为要等待broker的confirm消息，暂时不关闭channel和connection
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
                String       deadRoutingKey = deadConfig.getRoutingKey();
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
                // 绑定路由
                channel.queueBind(queueName, changeName, routingKey);
            }
            else if (StrUtil.isNotBlank(changeName)) {
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
            MqHelper.tryDo(() -> channel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列异常");
            channel.confirmSelect();// 开启发送方确认模式
        }
        else {
            final String       changeName   = mqConfig.getChangeName();
            final String       queueName    = mqConfig.getQueueName();
            final String       routingKey   = mqConfig.getRoutingKey();
            final Boolean      durable      = mqConfig.getDurable();
            final Boolean      isDelay      = mqConfig.getIsDelay();
            final ExchangeType exchangeType = mqConfig.getExchangeType();
            final Long         ttl          = mqConfig.getTtl();
            final Long         max          = mqConfig.getMax();
            final DeadConfig   deadConfig   = mqConfig.getDeadConfig();

            Map<String, Object> args = new HashMap<>();
            if (deadConfig != null) {
                final String deadChangeName = deadConfig.getChangeName();
                final String deadQueueName  = deadConfig.getQueueName();
                String       deadRoutingKey = deadConfig.getRoutingKey();
                if (StrUtil.isBlankOrUndefined(deadRoutingKey)) {
                    deadRoutingKey = "#";
                }

                MqHelper.tryDo(() -> channel.queueDeclare(deadQueueName, durable, false, false, args), () -> "获取队列异常");


                args.put("x-dead-letter-exchange", deadChangeName);
                args.put("x-dead-letter-routing-key", deadRoutingKey);

                if (!StrUtil.isBlankOrUndefined(deadChangeName)) {
                    MqHelper.tryDo(() -> channel.exchangeDeclare(deadChangeName, exchangeType.name(), durable), () -> "创建死信交换机失败");
                    String finalDeadRoutingKey = deadRoutingKey;
                    MqHelper.tryDo(() -> channel.queueBind(deadQueueName, deadChangeName, finalDeadRoutingKey), () -> "绑定死信交换机和死信队列失败");
                }
            }

            MqHelper.tryDo(() -> channel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列失败");
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
                MqHelper.tryDo(() -> channel.exchangeDeclare(changeName, exchangeType.name(), durable), () -> "创建交换机失败");
                // 绑定路由
                MqHelper.tryDo(() -> channel.queueBind(queueName, changeName, routingKey), () -> "绑定交换机和队列失败");
            }
            channel.basicQos(0, 1, false); //分发机制为触发式
        }

        return channel;
    }

    private Channel getChannel() throws Exception {
        return MqHelper.tryReturn(() -> connection.createChannel(), () -> "获取 channel 失败, 可能是 connection 已关闭");
    }


    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        log.trace("封装默认返回类型 {}", channel.toString());
        // 池对象创建实例化资源
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void destroyObject(PooledObject<Channel> p, DestroyMode destroyMode) throws Exception {
        final Channel channel = p.getObject();
        log.trace("销毁对象 {}", channel);
        // 池对象销毁资源
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

    }

    @Override
    public boolean validateObject(PooledObject<Channel> p) {
        final Channel channel = p.getObject();
        log.trace("校验对象是否可用 {}", channel);
        return channel != null && channel.isOpen();
    }

    @Override
    public void activateObject(PooledObject<Channel> p) throws Exception {
        log.trace("激活钝化的对象 {}", p.getObject());
    }

    /**
     * 钝化未使用的对象
     *
     * @param p p
     * @throws Exception 空指针异常
     */
    @Override
    public void passivateObject(PooledObject<Channel> p) throws Exception {
        log.trace("钝化未使用的对象 {}", p.getObject());
    }
}
