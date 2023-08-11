package com.xunmo.jimmer.cfg;

import cn.hutool.http.HttpStatus;
import org.babyfish.jimmer.sql.EnumType;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.runtime.DatabaseValidationMode;

import java.util.Collection;
import java.util.Map;

//@ConstructorBinding
//@ConfigurationProperties("jimmer")
public class JimmerProperties {

	private String language;

	private String dialect;

	private Dialect finalDialect;

	private Boolean showSql;

	private Boolean prettySql;

	private DatabaseValidation databaseValidation;

	private DatabaseValidationMode databaseValidationMode;

	private TriggerType triggerType;

	// private IdOnlyTargetCheckingLevel idOnlyTargetCheckingLevel;

	private Integer transactionCacheOperatorFixedDelay;

	private String defaultEnumStrategy;

	private EnumType.Strategy finalDefaultEnumStrategy;

	private Integer defaultBatchSize = 1000;

	private Integer defaultListBatchSize = 1000;

	private Integer offsetOptimizingThreshold;

	private Boolean isForeignKeyEnabledByDefault;

	private Collection<String> executorContextPrefixes;

	private String microServiceName;

	private ErrorTranslator errorTranslator;

	private Client client;

	private Map<String, Client> clients;

	public JimmerProperties() {
	}

	public JimmerProperties setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getLanguage() {
		return language;
	}

	public String getDialect() {
		return dialect;
	}

	public Dialect getFinalDialect() {
		return finalDialect;
	}

	public JimmerProperties setFinalDialect(Dialect finalDialect) {
		this.finalDialect = finalDialect;
		return this;
	}

	public Boolean isShowSql() {
		return showSql;
	}

	public Boolean isPrettySql() {
		return prettySql;
	}

	public JimmerProperties setDatabaseValidation(DatabaseValidation databaseValidation) {
		this.databaseValidation = databaseValidation;
		return this;
	}

	public DatabaseValidation getDatabaseValidation() {
		return databaseValidation;
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}

	//
	// public IdOnlyTargetCheckingLevel getIdOnlyTargetCheckingLevel() {
	// return idOnlyTargetCheckingLevel;
	// }

	public JimmerProperties setDialect(String dialect) {
		this.dialect = dialect;
		return this;
	}

	public JimmerProperties setShowSql(Boolean showSql) {
		this.showSql = showSql;
		return this;
	}

	public JimmerProperties setPrettySql(Boolean prettySql) {
		this.prettySql = prettySql;
		return this;
	}

	public JimmerProperties setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
		return this;
	}

	public JimmerProperties setTransactionCacheOperatorFixedDelay(Integer transactionCacheOperatorFixedDelay) {
		this.transactionCacheOperatorFixedDelay = transactionCacheOperatorFixedDelay;
		return this;
	}

	public Integer getTransactionCacheOperatorFixedDelay() {
		return transactionCacheOperatorFixedDelay;
	}

	public JimmerProperties setDefaultBatchSize(Integer defaultBatchSize) {
		this.defaultBatchSize = defaultBatchSize;
		return this;
	}

	public JimmerProperties setDefaultListBatchSize(Integer defaultListBatchSize) {
		this.defaultListBatchSize = defaultListBatchSize;
		return this;
	}

	public JimmerProperties setOffsetOptimizingThreshold(Integer offsetOptimizingThreshold) {
		this.offsetOptimizingThreshold = offsetOptimizingThreshold;
		return this;
	}

	public JimmerProperties setForeignKeyEnabledByDefault(Boolean foreignKeyEnabledByDefault) {
		isForeignKeyEnabledByDefault = foreignKeyEnabledByDefault;
		return this;
	}

	public JimmerProperties setExecutorContextPrefixes(Collection<String> executorContextPrefixes) {
		this.executorContextPrefixes = executorContextPrefixes;
		return this;
	}

	public JimmerProperties setMicroServiceName(String microServiceName) {
		this.microServiceName = microServiceName;
		return this;
	}

	public JimmerProperties setErrorTranslator(ErrorTranslator errorTranslator) {
		this.errorTranslator = errorTranslator;
		return this;
	}

	public JimmerProperties setClient(Client client) {
		this.client = client;
		return this;
	}

	public JimmerProperties setClients(Map<String, Client> clients) {
		this.clients = clients;
		return this;
	}

	public Integer getDefaultBatchSize() {
		return defaultBatchSize;
	}

	public Integer getDefaultListBatchSize() {
		return defaultListBatchSize;
	}

	/**
	 * For RDBMS, pagination is slow if `offset` is large, especially for MySQL.
	 * <p>
	 * If `offset` >= $thisArgument
	 *
	 * <pre>{@code
	 *  select t.* from Table t ... limit ? offset ?
	 * }</pre>
	 * <p>
	 * will be automatically changed to
	 *
	 * <pre>{@code
	 *  select t.* from (
	 *      select
	 *          t.id as optimized_core_id_
	 *      from Table t ... limit ? offset ?
	 *  ) optimized_core_
	 *  inner join Table as optimized_
	 *      on optimized_.optimized_core_id_ = optimized_core_.optimized_core_id_
	 * }</pre>
	 * @return An integer which is greater than 0
	 */
	public Integer getOffsetOptimizingThreshold() {
		return offsetOptimizingThreshold;
	}

	/**
	 * This configuration is only useful for {@link org.babyfish.jimmer.sql.JoinColumn} of
	 * local associations (not remote associations across microservice boundaries) whose
	 * `foreignKeyType` is specified as `AUTO`.Its value indicates whether the foreign key
	 * is real, that is, whether there is a foreign key constraint in the database.
	 *
	 * <p>
	 * In general, you should ignore this configuration (defaults to true) or set it to
	 * true.
	 * </p>
	 * <p>
	 * In some cases, you need to set it to false, such as
	 * <ul>
	 * <li>Using database/table sharding technology, such as sharding-jdbc</li>
	 * <li>Using database that does not support foreign key, such as TiDB</li>
	 * </ul>
	 */
	public Boolean isForeignKeyEnabledByDefault() {
		return isForeignKeyEnabledByDefault;
	}

	/**
	 * If this option is configured, when jimmer calls back
	 * `org.babyfish.jimmer.sql.runtime.Executor.execute` before executing SQL, it will
	 * check the stack trace information of the current thread.
	 * <p>
	 * However, these stack traces have too much information, including infrastructure
	 * call frames represented by jdk, jdbc driver, jimmer, and spring, and the
	 * business-related information you care about will be submerged in the ocean of
	 * information.
	 * <p>
	 * Through this configuration, you can specify multiple package or class prefixes, and
	 * jimmer will judge whether there are some call frames in the stack trace whose class
	 * names start with some of these prefixes. If the judgment is true, jimmer believes
	 * that the current callback is related to your business, and the `ctx` parameter of
	 * `org.babyfish.jimmer.sql.runtime.Executor.execute` will be passed as non-null.
	 * <p>
	 * If the SQL logging configuration is enabled at the same time, when a SQL statement
	 * is caused by the business you care about, the business call frame will be printed
	 * together with the SQL log.
	 */

	public Collection<String> getExecutorContextPrefixes() {
		return executorContextPrefixes;
	}

	public String getMicroServiceName() {
		return microServiceName;
	}

	public ErrorTranslator getErrorTranslator() {
		return errorTranslator;
	}

	public Client getClient() {
		return client;
	}

	public DatabaseValidationMode getDatabaseValidationMode() {
		return databaseValidationMode;
	}

	public JimmerProperties setDatabaseValidationMode(DatabaseValidationMode databaseValidationMode) {
		this.databaseValidationMode = databaseValidationMode;
		return this;
	}

	public String getDefaultEnumStrategy() {
		return defaultEnumStrategy;
	}

	public Boolean getShowSql() {
		return showSql;
	}

	public Boolean getPrettySql() {
		return prettySql;
	}

	public JimmerProperties setDefaultEnumStrategy(String defaultEnumStrategy) {
		this.defaultEnumStrategy = defaultEnumStrategy;
		return this;
	}

	public EnumType.Strategy getFinalDefaultEnumStrategy() {
		return finalDefaultEnumStrategy;
	}

	public JimmerProperties setFinalDefaultEnumStrategy(EnumType.Strategy finalDefaultEnumStrategy) {
		this.finalDefaultEnumStrategy = finalDefaultEnumStrategy;
		return this;
	}

	public Boolean getForeignKeyEnabledByDefault() {
		return isForeignKeyEnabledByDefault;
	}

	public Map<String, Client> getClients() {
		return clients;
	}

	@Override
	public String toString() {
		return "JimmerProperties{" + "language='" + language + '\'' + ", dialect=" + dialect + ", showSql=" + showSql
				+ ", prettySql=" + prettySql + ", databaseValidation=" + databaseValidation + ", triggerType="
				+ triggerType + ", transactionCacheOperatorFixedDelay=" + transactionCacheOperatorFixedDelay
				+ ", defaultEnumStrategy=" + defaultEnumStrategy + ", defaultBatchSize=" + defaultBatchSize
				+ ", defaultListBatchSize=" + defaultListBatchSize + ", offsetOptimizingThreshold="
				+ offsetOptimizingThreshold + ", isForeignKeyEnabledByDefault=" + isForeignKeyEnabledByDefault
				+ ", executorContextPrefixes=" + executorContextPrefixes + ", microServiceName='" + microServiceName
				+ '\'' + ", errorTranslator=" + errorTranslator + ", client=" + client + ", clients=" + clients + '}';
	}

	// @ConstructorBinding
	public static class DatabaseValidation {

		private final DatabaseValidationMode mode;

		private final String catalog;

		private final String schema;

		public DatabaseValidation(DatabaseValidationMode mode, String catalog, String schema) {
			this.mode = mode != null ? mode : DatabaseValidationMode.NONE;
			this.catalog = catalog != null && !catalog.isEmpty() ? catalog : null;
			this.schema = schema != null && !schema.isEmpty() ? schema : null;
		}

		public DatabaseValidationMode getMode() {
			return mode;
		}

		public String getCatalog() {
			return catalog;
		}

		public String getSchema() {
			return schema;
		}

		@Override
		public String toString() {
			return "Validation{" + "mode=" + mode + ", catalog='" + catalog + '\'' + '}';
		}

	}

	// @ConstructorBinding
	public static class ErrorTranslator {

		private final Boolean disabled;

		private final HttpStatus httpStatus;

		private final Boolean debugInfoSupported;

		private final Integer debugInfoMaxStackTraceCount;

		public ErrorTranslator(Boolean disabled, HttpStatus httpStatus, Boolean debugInfoSupported,
				Integer debugInfoMaxStackTraceCount) {
			this.disabled = disabled != null ? disabled : false;
			this.httpStatus = httpStatus != null ? httpStatus : new HttpStatus();
			this.debugInfoSupported = debugInfoSupported != null ? debugInfoSupported : false;
			this.debugInfoMaxStackTraceCount = debugInfoMaxStackTraceCount != null ? debugInfoMaxStackTraceCount
					: Integer.MAX_VALUE;
		}

		public Boolean isDisabled() {
			return disabled;
		}

		public HttpStatus getHttpStatus() {
			return httpStatus;
		}

		public Boolean isDebugInfoSupported() {
			return debugInfoSupported;
		}

		public Integer getDebugInfoMaxStackTraceCount() {
			return debugInfoMaxStackTraceCount;
		}

		@Override
		public String toString() {
			return "ErrorTranslator{" + "httpStatus=" + httpStatus + ", debugInfoSupported=" + debugInfoSupported + '}';
		}

	}

	// @ConstructorBinding
	public static class Client {

		private final TypeScript ts;

		private final JavaFeign javaFeign;

		public Client(TypeScript ts, JavaFeign javaFeign) {
			if (ts == null) {
				this.ts = new TypeScript(null, "Api", 4, false);
			}
			else {
				this.ts = ts;
			}
			if (javaFeign == null) {
				this.javaFeign = new JavaFeign(null, "", 4, "");
			}
			else {
				this.javaFeign = javaFeign;
			}
		}

		public TypeScript getTs() {
			return ts;
		}

		public JavaFeign getJavaFeign() {
			return javaFeign;
		}

		@Override
		public String toString() {
			return "Client{" + "ts=" + ts + ", javaFeign=" + javaFeign + '}';
		}

		// @ConstructorBinding
		public static class TypeScript {

			private final String path;

			private final String apiName;

			private final Integer indent;

			private final Boolean anonymous;

			public TypeScript(String path, String apiName, Integer indent, Boolean anonymous) {
				if (path == null || path.isEmpty()) {
					this.path = null;
				}
				else {
					if (!path.startsWith("/")) {
						throw new IllegalArgumentException("`jimmer.client.ts.path` must start with \"/\"");
					}
					this.path = path;
				}
				if (apiName == null || apiName.isEmpty()) {
					this.apiName = "Api";
				}
				else {
					this.apiName = apiName;
				}
				this.indent = indent != 0 ? Math.max(indent, 2) : 4;
				this.anonymous = anonymous;
			}

			public String getPath() {
				return path;
			}

			public String getApiName() {
				return apiName;
			}

			public Integer getIndent() {
				return indent;
			}

			public Boolean isAnonymous() {
				return anonymous;
			}

			@Override
			public String toString() {
				return "TypeScript{" + "path='" + path + '\'' + ", anonymous=" + anonymous + '}';
			}

		}

		// @ConstructorBinding
		public static class JavaFeign {

			private final String path;

			private final String apiName;

			private final Integer indent;

			private final String basePackage;

			public JavaFeign(String path, String apiName, Integer indent, String basePackage) {
				if (path == null || path.isEmpty()) {
					this.path = null;
				}
				else {
					if (!path.startsWith("/")) {
						throw new IllegalArgumentException("`jimmer.client.ts.path` must start with \"/\"");
					}
					this.path = path;
				}
				if (apiName == null || apiName.isEmpty()) {
					this.apiName = "Api";
				}
				else {
					this.apiName = apiName;
				}
				this.indent = indent != 0 ? Math.max(indent, 2) : 4;
				if (basePackage == null || basePackage.isEmpty()) {
					this.basePackage = "";
				}
				else {
					this.basePackage = basePackage;
				}
			}

			public String getPath() {
				return path;
			}

			public String getApiName() {
				return apiName;
			}

			public Integer getIndent() {
				return indent;
			}

			public String getBasePackage() {
				return basePackage;
			}

			@Override
			public String toString() {
				return "JavaFeign{" + "path='" + path + '\'' + ", clientName='" + apiName + '\'' + ", indent=" + indent
						+ ", basePackage=" + basePackage + '}';
			}

		}

	}

}
