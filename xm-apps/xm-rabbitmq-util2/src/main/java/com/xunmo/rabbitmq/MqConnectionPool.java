package com.xunmo.rabbitmq;

import com.rabbitmq.client.Connection;
import com.xunmo.pool.impl.GenericObjectPool;

import java.io.Closeable;

public class MqConnectionPool implements Closeable {

	private final GenericObjectPool<Connection, RuntimeException> internalPool;

	public MqConnectionPool(MqConnectionPoolObjectFactory factory) {
		this.internalPool = new GenericObjectPool<>(factory, factory.getMqConnectionPoolConfig());
	}

	// 获取连接
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = internalPool.borrowObject();
		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
			throw new RuntimeException("Could not destroy the pool", e);
		}
	}

}
