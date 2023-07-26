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
		Connection con = null;
		try {
			con = TranUtils.getConnection(dataSource);
			return block.apply(con);
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (con != null && !TranUtils.inTrans()) {
				try {
					con.close();
				}
				catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
