package com.xunmo.jimmer.integration;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.Repository;
import com.xunmo.jimmer.cfg.JimmerProperties;
import com.xunmo.jimmer.repository.JRepository;
import com.xunmo.jimmer.repository.SolonConnectionManager;
import com.xunmo.jimmer.repository.SolonTransientResolverProvider;
import com.xunmo.jimmer.repository.support.JimmerRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.babyfish.jimmer.sql.EnumType;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.cache.CacheAbandonedCallback;
import org.babyfish.jimmer.sql.cache.CacheFactory;
import org.babyfish.jimmer.sql.cache.CacheOperator;
import org.babyfish.jimmer.sql.dialect.DefaultDialect;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.event.Triggers;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.kt.cfg.KCustomizer;
import org.babyfish.jimmer.sql.kt.cfg.KInitializer;
import org.babyfish.jimmer.sql.kt.filter.KFilter;
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy;
import org.babyfish.jimmer.sql.runtime.*;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.GenericUtil;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	Map<Class<?>, Object> mapperCached = new HashMap<>();

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
		}
		else {
			this.dsProps = dsProps;
		}
		final JimmerProperties properties = this.dsProps.getBean(JimmerProperties.class);
		initProp(properties);
		jSqlClient = initSqlClient(properties);
		jimmerRepositoryFactory = new JimmerRepositoryFactory(jSqlClient);
	}

	private void initProp(JimmerProperties properties) {
		final JimmerProperties.DatabaseValidation databaseValidation = this.dsProps
			.getBean(JimmerProperties.DatabaseValidation.class);
		properties.setDatabaseValidation(databaseValidation);

		String language = properties.getLanguage();
		String dialect = properties.getDialect();
		final String enumStrategy = properties.getDefaultEnumStrategy();

		final String databaseValidationMode = properties.getDatabaseValidationMode();
		final String triggerType = properties.getTriggerType();
		final Integer transactionCacheOperatorFixedDelay = properties.getTransactionCacheOperatorFixedDelay();

		if (language == null) {
			language = "java";
			properties.setLanguage(language);
		}
		else {
			if (!language.equals("java") && !language.equals("kotlin")) {
				throw new IllegalArgumentException("`jimmer.language` must be \"java\" or \"kotlin\"");
			}
		}
		Dialect finalDialect;
		if (dialect == null) {
			finalDialect = DefaultDialect.INSTANCE;
		}
		else {
			Class<?> clazz;
			try {
				clazz = Class.forName(dialect, true, Dialect.class.getClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(
						"The class \"" + dialect + "\" specified by `jimmer.language` cannot be found");
			}
			if (!Dialect.class.isAssignableFrom(clazz) || clazz.isInterface()) {
				throw new IllegalArgumentException("The class \"" + dialect
						+ "\" specified by `jimmer.language` must be a valid dialect implementation");
			}
			try {
				finalDialect = (Dialect) clazz.getConstructor().newInstance();
			}
			catch (InvocationTargetException ex) {
				throw new IllegalArgumentException(
						"Create create instance for the class \"" + dialect + "\" specified by `jimmer.language`",
						ex.getTargetException());
			}
			catch (Exception ex) {
				throw new IllegalArgumentException(
						"Create create instance for the class \"" + dialect + "\" specified by `jimmer.language`", ex);
			}
		}
		properties.setFinalDialect(finalDialect);

		if (databaseValidationMode != null && databaseValidation != null) {
			throw new IllegalArgumentException(
					"Conflict configuration properties: \"jimmer.database-validation.mode\" and "
							+ "\"jimmer.database-validation-mode(deprecated)\"");
		}
		if (databaseValidation == null) {

			final DatabaseValidationMode finalDatabaseValidationMode = EnumUtil.fromString(DatabaseValidationMode.class,
					databaseValidationMode);
			properties.setDatabaseValidation(new JimmerProperties.DatabaseValidation(
					finalDatabaseValidationMode == null ? DatabaseValidationMode.NONE : finalDatabaseValidationMode,
					null, null));
		}

		if (StrUtil.isNotBlank(triggerType)) {
			final TriggerType finalTriggerType = EnumUtil.fromString(TriggerType.class, triggerType);
			properties.setFinalTriggerType(finalTriggerType != null ? finalTriggerType : TriggerType.BINLOG_ONLY);
		}

		properties.setTransactionCacheOperatorFixedDelay(
				transactionCacheOperatorFixedDelay != null ? transactionCacheOperatorFixedDelay : 5000);

		if (StrUtil.isNotBlank(enumStrategy)) {
			final EnumType.Strategy finalEnumStrategy = EnumUtil.fromString(EnumType.Strategy.class, enumStrategy);
			properties
				.setFinalDefaultEnumStrategy(finalEnumStrategy == null ? EnumType.Strategy.NAME : finalEnumStrategy);
		}

		final Integer defaultBatchSize = properties.getDefaultBatchSize();
		final Integer defaultListBatchSize = properties.getDefaultListBatchSize();
		final Integer offsetOptimizingThreshold = properties.getOffsetOptimizingThreshold();

		properties
			.setDefaultBatchSize(defaultBatchSize != null ? defaultBatchSize : JSqlClient.Builder.DEFAULT_BATCH_SIZE);

		properties.setDefaultListBatchSize(
				defaultListBatchSize != null ? defaultListBatchSize : JSqlClient.Builder.DEFAULT_LIST_BATCH_SIZE);

		properties.setOffsetOptimizingThreshold(
				offsetOptimizingThreshold != null ? offsetOptimizingThreshold : Integer.MAX_VALUE);

		final Boolean isForeignKeyEnabledByDefault = properties.isForeignKeyEnabledByDefault();
		properties
			.setForeignKeyEnabledByDefault(isForeignKeyEnabledByDefault != null ? isForeignKeyEnabledByDefault : true);

		final String microServiceName = properties.getMicroServiceName();
		properties.setMicroServiceName(microServiceName != null ? microServiceName : "");

		final JimmerProperties.ErrorTranslator errorTranslator = properties.getErrorTranslator();
		if (errorTranslator == null) {
			properties.setErrorTranslator(new JimmerProperties.ErrorTranslator(null, null, null, null));
		}
		final JimmerProperties.Client client = properties.getClient();
		if (client == null) {
			properties.setClient(new JimmerProperties.Client(null, null));
		}
	}

	@Override
	public BeanWrap getDsWrap() {
		return dsWrap;
	}

	protected DataSource getDataSource() {
		return dsWrap.raw();
	}

	@Override
	public JSqlClient sqlClient() {
		return jSqlClient;
	}

	private JSqlClient initSqlClient(JimmerProperties properties) {

		final List<DraftInterceptor<?>> interceptors = new ArrayList<>();
		final List<CacheAbandonedCallback> callbacks = new ArrayList<>();
		final List<ScalarProvider<?, ?>> providers = new ArrayList<>();
		final List<Filter<?>> javaFilters = new ArrayList<>();
		final List<KFilter<?>> kotlinFilters = new ArrayList<>();
		final List<Customizer> javaCustomizers = new ArrayList<>();
		final List<KCustomizer> kotlinCustomizers = new ArrayList<>();
		final List<Initializer> javaInitializers = new ArrayList<>();
		final List<KInitializer> kotlinInitializers = new ArrayList<>();

		Solon.context().subBeansOfType(DraftInterceptor.class, interceptors::add);
		Solon.context().subBeansOfType(CacheAbandonedCallback.class, callbacks::add);
		Solon.context().subBeansOfType(ScalarProvider.class, providers::add);
		Solon.context().subBeansOfType(Filter.class, javaFilters::add);
		Solon.context().subBeansOfType(KFilter.class, kotlinFilters::add);
		Solon.context().subBeansOfType(Customizer.class, javaCustomizers::add);
		Solon.context().subBeansOfType(KCustomizer.class, kotlinCustomizers::add);
		Solon.context().subBeansOfType(Initializer.class, javaInitializers::add);
		Solon.context().subBeansOfType(KInitializer.class, kotlinInitializers::add);

		if (!kotlinFilters.isEmpty()) {
			log.warn("Jimmer is working in java mode, but some kotlin filters "
					+ "has been found in spring context, they will be ignored");
		}
		if (!kotlinCustomizers.isEmpty()) {
			log.warn("Jimmer is working in java mode, but some kotlin customizers "
					+ "has been found in spring context, they will be ignored");
		}
		if (!kotlinInitializers.isEmpty()) {
			log.warn("Jimmer is working in kotlin mode, but some kotlin initializers "
					+ "has been found in spring context, they will be ignored");
		}

		JSqlClient.Builder builder = JSqlClient.newBuilder();
		final AopContext context = dsWrap.context();
		Executor executor = context.getBean(Executor.class);
		if (executor == null) {
			executor = new Executor() {

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
					return result;
				}
			};
		}
		final CacheFactory cacheFactory = context.getBean(CacheFactory.class);
		preCreateSqlClient(builder, context, properties, getDataSource(), context.getBean(SolonConnectionManager.class),
				context.getBean(SolonTransientResolverProvider.class), context.getBean(EntityManager.class),
				context.getBean(DatabaseNamingStrategy.class), context.getBean(Dialect.class), executor,
				context.getBean(SqlFormatter.class), cacheFactory, context.getBean(CacheOperator.class),
				context.getBean(MicroServiceExchange.class), callbacks, providers, interceptors, javaFilters,
				javaCustomizers, javaInitializers);

		final JSqlClient sqlClient = builder.build();
		postCreateSqlClient((JSqlClientImplementor) sqlClient);
		return sqlClient;
	}

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

	private static void preCreateSqlClient(JSqlClient.Builder builder, AopContext ctx, JimmerProperties properties,
			DataSource dataSource, SolonConnectionManager connectionManager,
			SolonTransientResolverProvider transientResolverProvider, EntityManager entityManager,
			DatabaseNamingStrategy databaseNamingStrategy, Dialect dialect, Executor executor,
			SqlFormatter sqlFormatter, CacheFactory cacheFactory, CacheOperator cacheOperator,
			MicroServiceExchange exchange, List<CacheAbandonedCallback> callbacks, List<ScalarProvider<?, ?>> providers,
			List<DraftInterceptor<?>> interceptors, List<Filter<?>> filters, List<Customizer> customizers,
			List<Initializer> initializers) {
		if (connectionManager != null) {
			builder.setConnectionManager(connectionManager);
		}
		else if (dataSource != null) {
			builder.setConnectionManager(new SolonConnectionManager(dataSource));
		}
		if (transientResolverProvider != null) {
			builder.setTransientResolverProvider(transientResolverProvider);
		}
		else {
			builder.setTransientResolverProvider(new SolonTransientResolverProvider(ctx));
		}
		if (entityManager != null) {
			builder.setEntityManager(entityManager);
		}
		if (databaseNamingStrategy != null) {
			builder.setDatabaseNamingStrategy(databaseNamingStrategy);
		}

		builder.setDialect(dialect != null ? dialect : properties.getFinalDialect());
		final String triggerType = properties.getTriggerType();
		if (StrUtil.isNotBlank(triggerType)) {
			final TriggerType finalTriggerType = EnumUtil.fromString(TriggerType.class, triggerType);
			builder.setTriggerType(finalTriggerType);
		}
		builder.setDefaultEnumStrategy(properties.getFinalDefaultEnumStrategy());
		builder.setDefaultBatchSize(properties.getDefaultBatchSize());
		builder.setDefaultListBatchSize(properties.getDefaultListBatchSize());
		builder.setOffsetOptimizingThreshold(properties.getOffsetOptimizingThreshold());
		builder.setForeignKeyEnabledByDefault(properties.isForeignKeyEnabledByDefault());
		builder.setExecutorContextPrefixes(properties.getExecutorContextPrefixes());

		if (BooleanUtil.isTrue(properties.isShowSql())) {
			builder.setExecutor(Executor.log(executor));
		}
		else {
			builder.setExecutor(executor);
		}
		if (sqlFormatter != null) {
			builder.setSqlFormatter(sqlFormatter);
		}
		else if (BooleanUtil.isTrue(properties.isPrettySql())) {
			builder.setSqlFormatter(SqlFormatter.PRETTY);
		}
		builder.setDatabaseValidationMode(properties.getDatabaseValidation().getMode())
			.setDatabaseValidationCatalog(properties.getDatabaseValidation().getCatalog())
			.setDatabaseValidationSchema(properties.getDatabaseValidation().getSchema())
			.setCacheFactory(cacheFactory)
			.setCacheOperator(cacheOperator)
			.addCacheAbandonedCallbacks(callbacks);

		for (ScalarProvider<?, ?> provider : providers) {
			builder.addScalarProvider(provider);
		}

		builder.addDraftInterceptors(interceptors);
		builder.addFilters(filters);
		builder.addCustomizers(customizers);
		builder.addInitializers(initializers);

		builder.setMicroServiceName(properties.getMicroServiceName());
		if (!properties.getMicroServiceName().isEmpty()) {
			builder.setMicroServiceExchange(exchange);
		}
	}

	private static void postCreateSqlClient(JSqlClientImplementor sqlClient) {
		if (!(sqlClient.getConnectionManager() instanceof SolonConnectionManager)) {
			throw new IllegalStateException(
					"The connection manager of sqlClient must be \"" + SolonConnectionManager.class.getName() + "\"");
		}

		if (sqlClient.getSlaveConnectionManager(false) != null
				&& !(sqlClient.getSlaveConnectionManager(false) instanceof SolonConnectionManager)) {
			throw new IllegalStateException("The slave connection manager of sqlClient must be null or \""
					+ SolonConnectionManager.class.getName() + "\"");
		}

		if (!SolonTransientResolverProvider.class.isAssignableFrom(sqlClient.getResolverProviderClass())) {
			throw new IllegalStateException("The transient resolver provider of sqlClient must be \""
					+ SolonTransientResolverProvider.class.getName() + "\"");
		}

		Triggers[] triggersArr = sqlClient.getTriggerType() == TriggerType.BOTH
				? new Triggers[] { sqlClient.getTriggers(), sqlClient.getTriggers(true) }
				: new Triggers[] { sqlClient.getTriggers() };
		for (Triggers triggers : triggersArr) {
			triggers.addEntityListener(EventBus::push);
			triggers.addAssociationListener(EventBus::push);
		}
	}

}
