package com.xunmo.rabbitmq;


import com.rabbitmq.client.Channel;
import com.xunmo.pool.impl.GenericObjectPoolConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MqChannelPoolConfig extends GenericObjectPoolConfig<Channel> {

    private final static int DEFAULT_MAX_CONNECTION_USING_COUNT = 300; //默认最大连接可访问次数

    public MqChannelPoolConfig() {
        setMaxTotal(10);
        setMaxIdle(5);
        setMinIdle(2);
    }
}
