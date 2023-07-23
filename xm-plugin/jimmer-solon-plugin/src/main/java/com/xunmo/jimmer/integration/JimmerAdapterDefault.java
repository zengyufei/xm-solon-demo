package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.Repository;
import com.xunmo.jimmer.cfg.JimmerProperties;
import com.xunmo.jimmer.repository.JRepository;
import com.xunmo.jimmer.repository.support.JimmerRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.DefaultExecutor;
import org.babyfish.jimmer.sql.runtime.Executor;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * jimmer 适配器默认实现
 *
 * @author zengyufei
 * @since 0.1
 */
@Slf4j
public class JimmerAdapterDefault implements JimmerAdapter {

	protected final BeanWrap dsWrap;

	protected final Props dsProps;

	protected final JSqlClient jSqlClient;

	protected final JimmerRepositoryFactory jimmerRepositoryFactory;

	/**
	 * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
	 */
	protected JimmerAdapterDefault(BeanWrap dsWrap) {
		this(dsWrap, Solon.cfg().getProp("jimmer"));
	}

	/**
	 * 构建Sql工厂适配器，使用属性配置
	 */
	protected JimmerAdapterDefault(BeanWrap dsWrap, Props dsProps) {
		this.dsWrap = dsWrap;
		if (dsProps == null) {
			this.dsProps = new Props();
		} else {
			this.dsProps = dsProps;
		}
		jSqlClient = initSqlClient(this.dsProps.getBean(JimmerProperties.class));
		jimmerRepositoryFactory = new JimmerRepositoryFactory(jSqlClient);
	}

	protected DataSource getDataSource() {
		return dsWrap.raw();
	}

	@Override
	public JSqlClient sqlClient() {
		return jSqlClient;
	}

	private JSqlClient initSqlClient(JimmerProperties properties) {
		final Dialect dialect = properties.getDialect();
		final JSqlClient.Builder builder = JSqlClient.newBuilder();
		builder.setDialect(dialect);
		builder.setTriggerType(properties.getTriggerType());
		builder.setDefaultEnumStrategy(properties.getDefaultEnumStrategy());
		builder.setDefaultBatchSize(properties.getDefaultBatchSize());
		builder.setDefaultListBatchSize(properties.getDefaultListBatchSize());
		builder.setOffsetOptimizingThreshold(properties.getOffsetOptimizingThreshold());
		builder.setForeignKeyEnabledByDefault(properties.isForeignKeyEnabledByDefault());
		builder.setExecutorContextPrefixes(properties.getExecutorContextPrefixes());

		return builder.setConnectionManager(new ConnectionManager() {
			@Override
			public <R> R execute(Function<Connection, R> block) {
				Connection connection = null;
				R var3;
				try {
					connection = TranUtils.getConnection(getDataSource());
					connection.setAutoCommit(false);
					var3 = block.apply(connection);
					if (!TranUtils.inTrans() && !connection.getAutoCommit()) {
						connection.commit();
					}
				} catch (Throwable var6) {
					if (connection != null) {
						try {
							if (!TranUtils.inTrans() && !connection.getAutoCommit()) {
								connection.rollback();
							}
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
					throw new RuntimeException(var6);
				} finally {
					if (connection != null && !TranUtils.inTrans()) {
						try {
							connection.close();
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return var3;
			}
		}).setExecutor(new Executor() {

			@Override
			public <R> R execute(@NotNull Args<R> args) {
				long millis = System.currentTimeMillis();
				if (!args.sql.contains("t_exception_record")) {
					// Log SQL and variables.
					log.info("Execute sql : {}, variables: {}, purpose: {}", args.sql, args.variables, args.purpose);
				}
				// Call DefaultExecutor
				R result = DefaultExecutor.INSTANCE.execute(args);
				millis = System.currentTimeMillis() - millis;
				return result;
			}
		}).build();
	}

	Map<Class<?>, Object> mapperCached = new HashMap<>();

	@Override
	public <T> T getRepository(Class<T> repositoryClz) {
		Object repository = mapperCached.get(repositoryClz);

		if (repository == null) {
			synchronized (repositoryClz) {
				repository = mapperCached.get(repositoryClz);
				if (repository == null) {
					Class<?>[] typeArguments = GenericUtil.resolveTypeArguments(repositoryClz, JRepository.class);
					if (typeArguments == null) {
						throw new IllegalArgumentException("The class \"" + this.getClass() + "\" "
								+ "does not explicitly specify the type arguments of \"" + JRepository.class.getName()
								+ "\" so that the entityType must be specified");
					}
					repository = jimmerRepositoryFactory.getTargetRepository(repositoryClz, typeArguments[0]);
					mapperCached.put(repositoryClz, repository);
				}
			}
		}

		return (T) repository;
	}

	@Override
	public void injectTo(VarHolder varH, BeanWrap dsBw) {
		final Class<?> varHolderType = varH.getType();
		if (JSqlClient.class.isAssignableFrom(varHolderType)) {
			final JSqlClient sqlClient = this.sqlClient();
			if (sqlClient != null) {
				varH.setValue(sqlClient);
			}
		}
		if (Repository.class.isAssignableFrom(varHolderType)) {
			final Object repository = this.getRepository(varHolderType);
			varH.setValue(repository);
			// 进入容器，用于 @Inject 注入
			dsBw.context().wrapAndPut(varHolderType, repository);
		}
	}

}
