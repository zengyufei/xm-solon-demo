package com.xunmo.rabbitmq;


import com.rabbitmq.client.Channel;
import com.xunmo.pool.impl.GenericObjectPool;

import java.io.Closeable;


public class MqChannelPool implements Closeable {
    private final GenericObjectPool<Channel, Exception> internalPool;

    public MqChannelPool(MqChannelPoolObjectFactory factory) {
        this.internalPool = new GenericObjectPool<>(factory, new MqChannelPoolConfig());
    }

    // 获取连接
    public Channel getChannel() {
        Channel channel = null;
        try {
            channel = internalPool.borrowObject();
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
}
