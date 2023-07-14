package com.xunmo.rabbitmq;

import cn.hutool.json.JSONUtil;
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
public class MqConnectionPoolObjectFactory extends BasePooledObjectFactory<Connection, RuntimeException> {

	@Getter
	private MqConnectionPoolConfig mqConnectionPoolConfig;

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	public MqConnectionPoolObjectFactory(MqConnectionPoolConfig mqConnectionPoolConfig) {
		super();
		this.mqConnectionPoolConfig = mqConnectionPoolConfig;

		// 设置服务地址
		connectionFactory.setHost(mqConnectionPoolConfig.getHost());
		// 设置账号信息，用户名、密码、vhost
		connectionFactory.setUsername(mqConnectionPoolConfig.getUsername());
		connectionFactory.setPassword(mqConnectionPoolConfig.getPassword());
		connectionFactory.setPort(mqConnectionPoolConfig.getPort());
		connectionFactory.setConnectionTimeout(300);
		connectionFactory.setChannelRpcTimeout(300);
		connectionFactory.setAutomaticRecoveryEnabled(true); // 自动重连
		connectionFactory.setTopologyRecoveryEnabled(true);
	}

	@Override
	public Connection create() throws RuntimeException {
		log.trace("在对象池中创建对象 {}", JSONUtil.toJsonPrettyStr(mqConnectionPoolConfig));
		try {
			return connectionFactory.newConnection();
		}
		catch (IOException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PooledObject<Connection> wrap(Connection connection) {
		log.trace("封装默认返回类型 {}", connection.toString());
		return new DefaultPooledObject(connection);
	}

	@Override
	public void destroyObject(PooledObject<Connection> p, DestroyMode destroyMode) throws RuntimeException {
		log.trace("销毁对象 {}", p.getObject());
		Connection connection = p.getObject();
		try {
			connection.close();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean validateObject(PooledObject<Connection> p) {
		final Connection connection = p.getObject();
		log.trace("校验对象是否可用 {}", connection);
		return connection.isOpen();
	}

	@Override
	public void activateObject(PooledObject<Connection> p) throws RuntimeException {
		log.trace("激活钝化的对象 {}", p.getObject());
	}

	/**
	 * 钝化未使用的对象
	 * @param p p
	 * @throws RuntimeException 空指针异常
	 */
	@Override
	public void passivateObject(PooledObject<Connection> p) throws RuntimeException {
		log.trace("钝化未使用的对象 {}", p.getObject());
	}

}
