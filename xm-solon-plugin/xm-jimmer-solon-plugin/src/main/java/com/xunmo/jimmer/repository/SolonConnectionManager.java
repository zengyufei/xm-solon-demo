package com.xunmo.jimmer.repository;

import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public final class SolonConnectionManager implements ConnectionManager {

	private final DataSource dataSource;

	public SolonConnectionManager(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public <R> R execute(Function<Connection, R> block) {
		Connection connection = null;
		R var3;
		try {
			connection = TranUtils.getConnection(dataSource);
			connection.setAutoCommit(false);
			var3 = block.apply(connection);
			if (!TranUtils.inTrans() && !connection.getAutoCommit()) {
				connection.commit();
			}
		}
		catch (Throwable var6) {
			if (connection != null) {
				try {
					if (!TranUtils.inTrans() && !connection.getAutoCommit()) {
						connection.rollback();
					}
				}
				catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
			throw new RuntimeException(var6);
		}
		finally {
			if (connection != null && !TranUtils.inTrans()) {
				try {
					connection.close();
				}
				catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return var3;
	}

}
