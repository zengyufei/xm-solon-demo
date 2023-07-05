package com.xunmo.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xunmo.pool.BasePooledObjectFactory;
import com.xunmo.pool.DestroyMode;
import com.xunmo.pool.PooledObject;
import com.xunmo.pool.impl.DefaultPooledObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MqChannelPoolObjectFactory extends BasePooledObjectFactory<Channel, Exception> {
    @Getter
    private MqChannelPoolConfig mqChannelPoolConfig;
    private ConnectionFactory connectionFactory = new ConnectionFactory();

    public MqChannelPoolObjectFactory(MqChannelPoolConfig mqChannelPoolConfig) {
        super();
        this.mqChannelPoolConfig = mqChannelPoolConfig;

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
    }


    @Override
    public Channel create() throws Exception {
        log.trace("在对象池中创建 Channel 对象");
        return connectionFactory.newConnection().createChannel();
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
