package com.xunmo.rabbitmq;


import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import com.xunmo.pool.impl.GenericObjectPool;
import com.xunmo.rabbitmq.entity.DeadConfig;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ExchangeType;
import com.xunmo.utils.MqHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class MqChannelPool implements Closeable {
    private final static int tryCountMax = 3;
    private final GenericObjectPool<Channel, Exception> internalPool;

    public MqChannelPool(MqChannelPoolObjectFactory factory) {
        this.internalPool = new GenericObjectPool<>(factory, factory.getMqChannelPoolConfig());
    }

    // 获取连接
    public Channel getChannel(MqConfig mqConfig) {
        Channel channel = null;
        try {
            channel = internalPool.borrowObject();

                Channel finalChannel = channel;
            if (mqConfig.getType().equals("0")) {
                final String changeName = mqConfig.getChangeName();
                final String queueName = mqConfig.getQueueName();
                final String routingKey = mqConfig.getRoutingKey();
                final Boolean durable = mqConfig.getDurable();
                final Boolean isDelay = mqConfig.getIsDelay();
                final ExchangeType exchangeType = mqConfig.getExchangeType();
                final Long ttl = mqConfig.getTtl();
                final Long max = mqConfig.getMax();
                final DeadConfig deadConfig = mqConfig.getDeadConfig();

                final ArrayList<Channel> channels = new ArrayList<>();
                channels.add(channel);

                // 注意，因为要等待broker的confirm消息，暂时不关闭channel和connection
                final String channelString = channel.toString();

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
                tryDo(() -> finalChannel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列异常");
                channel.confirmSelect();// 开启发送方确认模式
            } else {
                final String changeName = mqConfig.getChangeName();
                final String queueName = mqConfig.getQueueName();
                final String routingKey = mqConfig.getRoutingKey();
                final Boolean durable = mqConfig.getDurable();
                final Boolean isDelay = mqConfig.getIsDelay();
                final ExchangeType exchangeType = mqConfig.getExchangeType();
                final Long ttl = mqConfig.getTtl();
                final Long max = mqConfig.getMax();
                final DeadConfig deadConfig = mqConfig.getDeadConfig();

                final ArrayList<Channel> channels = new ArrayList<>();
                channels.add(channel);


                final String channelString = channel.toString();
                Map<String, Object> args = new HashMap<>();
                if (deadConfig != null) {
                    final String deadChangeName = deadConfig.getChangeName();
                    final String deadQueueName = deadConfig.getQueueName();
                    String deadRoutingKey = deadConfig.getRoutingKey();
                    if (StrUtil.isBlankOrUndefined(deadRoutingKey)) {
                        deadRoutingKey = "#";
                    }

                    tryDo(() -> finalChannel.queueDeclare(deadQueueName, durable, false, false, args), () -> "获取队列异常");


                    args.put("x-dead-letter-exchange", deadChangeName);
                    args.put("x-dead-letter-routing-key", deadRoutingKey);

                    if (!StrUtil.isBlankOrUndefined(deadChangeName)) {
                        tryDo(() -> finalChannel.exchangeDeclare(deadChangeName, exchangeType.name(), durable), () -> "创建死信交换机失败");
                        String finalDeadRoutingKey = deadRoutingKey;
                        tryDo(() -> finalChannel.queueBind(deadQueueName, deadChangeName, finalDeadRoutingKey), () -> "绑定死信交换机和死信队列失败");
                    }
                }

                tryDo(() -> finalChannel.queueDeclare(queueName, durable, false, false, args), () -> "获取队列失败");
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
                    tryDo(() -> finalChannel.exchangeDeclare(changeName, exchangeType.name(), durable), () -> "创建交换机失败");
                    // 绑定路由
                    tryDo(() -> finalChannel.queueBind(queueName, changeName, routingKey), () -> "绑定交换机和队列失败");
                }
                channel.basicQos(0, 1, false); //分发机制为触发式
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not get channel from the pool", e);
        }
        return channel;
    }

    // 返还连接
    public void returnChannel(Channel channel) {
        internalPool.returnObject(channel);
    }

    @Override
    public void close() {
        this.closeInternalPool();
    }

    private void closeInternalPool() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new RuntimeException("Could not destroy the pool", e);
        }
    }

    private static void tryDo(MqHelper.TryDoing tryDoing, Supplier<String> errorMsg) {
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

}
