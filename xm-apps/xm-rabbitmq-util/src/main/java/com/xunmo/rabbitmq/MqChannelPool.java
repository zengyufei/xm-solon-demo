package com.xunmo.rabbitmq;


import com.rabbitmq.client.Channel;
import com.xunmo.pool.impl.GenericObjectPool;
import com.xunmo.rabbitmq.entity.MqConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class MqChannelPool implements Closeable {
    private final static int                                   tryCountMax = 6;
    private final        GenericObjectPool<Channel, Exception> internalPool;

    public MqChannelPool(MqChannelPoolConfig mqChannelPoolConfig, MqConfig mqConfig) throws IOException, TimeoutException {
        MqChannelPoolObjectFactory factory = new MqChannelPoolObjectFactory(mqChannelPoolConfig, mqConfig);
        this.internalPool = new GenericObjectPool<>(factory, factory.getMqChannelPoolConfig());
    }

    // 获取连接
    public Channel getChannel() {
        int     tryCount = tryCountMax;
        Channel channel  = null;
        do {
            try {
                channel = internalPool.borrowObject();
                break;
            } catch (Exception e) {
                log.error("获取 channel 错误哦, 重试第{}次获取!", (tryCountMax - tryCount));
                tryCount--;
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } while (tryCount > 0);

        if (channel == null) {
            throw new RuntimeException("Could not get channel from the pool");
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
