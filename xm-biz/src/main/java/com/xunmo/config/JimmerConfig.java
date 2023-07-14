package com.xunmo.config;

import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.DefaultExecutor;
import org.babyfish.jimmer.sql.runtime.Executor;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

@Slf4j
@Configuration
public class JimmerConfig {

	@Bean
	public JSqlClient sqlClient(@Inject DataSource dataSource) {
		return JSqlClient.newBuilder()
			// .setConnectionManager(
			// ConnectionManager
			// .simpleConnectionManager(dataSource)
			// )
			.setConnectionManager(new ConnectionManager() {
				@Override
				public <R> R execute(Function<Connection, R> block) {
					Connection con = null;
					R var3;
					try {
						con = dataSource.getConnection();
						con.setAutoCommit(false);
						var3 = block.apply(con);
						con.commit();
					}
					catch (Throwable var6) {
						if (con != null) {
							try {
								con.rollback();
							}
							catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}
						throw new RuntimeException(var6);
					}
					finally {
						if (con != null) {
							try {
								con.close();
							}
							catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}
					}
					return var3;
				}
			})
			.setExecutor(new Executor() {

				@Override
				public <R> R execute(@NotNull Args<R> args) {
					long millis = System.currentTimeMillis();
					if (!args.sql.contains("t_exception_record")) {
						// Log SQL and variables.
						log.info("Execute sql : {}, variables: {}, purpose: {}", args.sql, args.variables,
								args.purpose);
					}
					// Call DefaultExecutor
					R result = DefaultExecutor.INSTANCE.execute(args);
					millis = System.currentTimeMillis() - millis;
					if (millis > 5000) { // Slow SQL

					}
					return result;
				}
			})
			.setDefaultBatchSize(256)
			.setDefaultListBatchSize(32)
			.build();
	}

}
