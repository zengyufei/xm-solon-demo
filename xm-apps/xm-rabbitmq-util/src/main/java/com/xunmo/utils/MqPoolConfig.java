package com.xunmo.utils;


import com.rabbitmq.client.Connection;
import com.xunmo.pool.impl.GenericObjectPoolConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MqPoolConfig extends GenericObjectPoolConfig<Connection> {

    private final static int DEFAULT_MAX_CONNECTION_USING_COUNT = 300; //默认最大连接可访问次数
    private String host;
    private String username;
    private String password;
    private int port;

    public MqPoolConfig() {
        setMaxTotal(10);
        setMaxIdle(5);
        setMinIdle(2);
    }
}
