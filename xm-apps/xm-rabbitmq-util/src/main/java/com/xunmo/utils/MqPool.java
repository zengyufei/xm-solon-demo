package com.xunmo.utils;


import com.rabbitmq.client.Connection;
import com.xunmo.pool.impl.GenericObjectPool;

import java.io.Closeable;


public class MqPool implements Closeable {
    private final GenericObjectPool<Connection, RuntimeException> internalPool;

    public MqPool(MqConnectionPoolObjectFactory factory) {
        this.internalPool = new GenericObjectPool<>(factory, factory.getMqPoolConfig());
    }

    // 获取连接
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = internalPool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("Could not get connection from the pool", e);
        }
        return connection;
    }

    // 返还连接
    public void returnConnection(Connection connection) {
        internalPool.returnObject(connection);
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
