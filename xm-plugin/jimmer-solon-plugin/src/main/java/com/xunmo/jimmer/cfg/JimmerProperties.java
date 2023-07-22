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

	private Dialect dialect;

	private boolean showSql;

	private boolean prettySql;

	private DatabaseValidation databaseValidation;

	private TriggerType triggerType;

//    private  IdOnlyTargetCheckingLevel idOnlyTargetCheckingLevel;

	private int transactionCacheOperatorFixedDelay;

	private EnumType.Strategy defaultEnumStrategy;

	private int defaultBatchSize = 1000;

	private int defaultListBatchSize = 1000;

	private int offsetOptimizingThreshold;

	private boolean isForeignKeyEnabledByDefault;

	private Collection<String> executorContextPrefixes;

	private String microServiceName;

	private ErrorTranslator errorTranslator;

	private Client client;

	private Map<String, Client> clients;

	public JimmerProperties() {
		if (language == null) {
			this.language = "java";
		} else {
			if (!language.equals("java") && !language.equals("kotlin")) {
				throw new IllegalArgumentException("`jimmer.language` must be \"java\" or \"kotlin\"");
			}
			this.language = language;
		}
		this.showSql = showSql;
		this.prettySql = prettySql;
		this.executorContextPrefixes = executorContextPrefixes;
		this.microServiceName =
				microServiceName != null ?
						microServiceName :
						"";
		if (errorTranslator == null) {
			this.errorTranslator = new ErrorTranslator(null, null, null, null);
		} else {
			this.errorTranslator = errorTranslator;
		}
		if (client == null) {
			this.client = new Client(null, null);
		} else {
			this.client = client;
		}
		this.clients = clients;
	}


	public String getLanguage() {
		return language;
	}


	public Dialect getDialect() {
		return dialect;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public boolean isPrettySql() {
		return prettySql;
	}


	public DatabaseValidation getDatabaseValidation() {
		return databaseValidation;
	}


	public TriggerType getTriggerType() {
		return triggerType;
	}

//    
//    public IdOnlyTargetCheckingLevel getIdOnlyTargetCheckingLevel() {
//        return idOnlyTargetCheckingLevel;
//    }


	public EnumType.Strategy getDefaultEnumStrategy() {
		return defaultEnumStrategy;
	}

	public int getDefaultBatchSize() {
		return defaultBatchSize;
	}

	public int getDefaultListBatchSize() {
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
	 *
	 * @return An integer which is greater than 0
	 */
	public int getOffsetOptimizingThreshold() {
		return offsetOptimizingThreshold;
	}

	/**
	 * This configuration is only useful for {@link org.babyfish.jimmer.sql.JoinColumn}
	 * of local associations (not remote associations across microservice boundaries)
	 * whose `foreignKeyType` is specified as `AUTO`.Its value indicates whether the
	 * foreign key is real, that is, whether there is a foreign key constraint in the database.
	 *
	 * <p>In general, you should ignore this configuration (defaults to true) or set it to true.</p>
	 * <p>
	 * In some cases, you need to set it to false, such as
	 * <ul>
	 *  <li>Using database/table sharding technology, such as sharding-jdbc</li>
	 *  <li>Using database that does not support foreign key, such as TiDB</li>
	 * </ul>
	 */
	public boolean isForeignKeyEnabledByDefault() {
		return isForeignKeyEnabledByDefault;
	}

	/**
	 * If this option is configured, when jimmer calls back
	 * `org.babyfish.jimmer.sql.runtime.Executor.execute` before executing SQL,
	 * it will check the stack trace information of the current thread.
	 * <p>
	 * However, these stack traces have too much information, including
	 * infrastructure call frames represented by jdk, jdbc driver, jimmer, and spring,
	 * and the business-related information you care about will be submerged in the ocean of information.
	 * <p>
	 * Through this configuration, you can specify multiple package or class prefixes, and jimmer will
	 * judge whether there are some call frames in the stack trace whose class names start with some
	 * of these prefixes. If the judgment is true, jimmer believes that the current callback is related
	 * to your business, and the `ctx` parameter of `org.babyfish.jimmer.sql.runtime.Executor.execute`
	 * will be passed as non-null.
	 * <p>
	 * If the SQL logging configuration is enabled at the same time, when a SQL statement is caused by
	 * the business you care about, the business call frame will be printed together with the SQL log.
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

	@Override
	public String toString() {
		return "JimmerProperties{" +
				"language='" + language + '\'' +
				", dialect=" + dialect +
				", showSql=" + showSql +
				", prettySql=" + prettySql +
				", databaseValidation=" + databaseValidation +
				", triggerType=" + triggerType +
				", transactionCacheOperatorFixedDelay=" + transactionCacheOperatorFixedDelay +
				", defaultEnumStrategy=" + defaultEnumStrategy +
				", defaultBatchSize=" + defaultBatchSize +
				", defaultListBatchSize=" + defaultListBatchSize +
				", offsetOptimizingThreshold=" + offsetOptimizingThreshold +
				", isForeignKeyEnabledByDefault=" + isForeignKeyEnabledByDefault +
				", executorContextPrefixes=" + executorContextPrefixes +
				", microServiceName='" + microServiceName + '\'' +
				", errorTranslator=" + errorTranslator +
				", client=" + client +
				", clients=" + clients +
				'}';
	}

	//    @ConstructorBinding
	public static class DatabaseValidation {


		private final DatabaseValidationMode mode;


		private final String catalog;


		private final String schema;

		public DatabaseValidation(
				DatabaseValidationMode mode,
				String catalog,
				String schema
		) {
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
			return "Validation{" +
					"mode=" + mode +
					", catalog='" + catalog + '\'' +
					'}';
		}
	}

	//    @ConstructorBinding
	public static class ErrorTranslator {

		private final boolean disabled;


		private final HttpStatus httpStatus;

		private final boolean debugInfoSupported;

		private final int debugInfoMaxStackTraceCount;

		public ErrorTranslator(
				Boolean disabled,
				HttpStatus httpStatus,
				Boolean debugInfoSupported,
				Integer debugInfoMaxStackTraceCount
		) {
			this.disabled = disabled != null ? disabled : false;
			this.httpStatus = httpStatus != null ? httpStatus : new HttpStatus();
			this.debugInfoSupported = debugInfoSupported != null ? debugInfoSupported : false;
			this.debugInfoMaxStackTraceCount = debugInfoMaxStackTraceCount != null ?
					debugInfoMaxStackTraceCount :
					Integer.MAX_VALUE;
		}

		public boolean isDisabled() {
			return disabled;
		}


		public HttpStatus getHttpStatus() {
			return httpStatus;
		}

		public boolean isDebugInfoSupported() {
			return debugInfoSupported;
		}

		public int getDebugInfoMaxStackTraceCount() {
			return debugInfoMaxStackTraceCount;
		}

		@Override
		public String toString() {
			return "ErrorTranslator{" +
					"httpStatus=" + httpStatus +
					", debugInfoSupported=" + debugInfoSupported +
					'}';
		}
	}

	//    @ConstructorBinding
	public static class Client {


		private final TypeScript ts;


		private final JavaFeign javaFeign;

		public Client(TypeScript ts, JavaFeign javaFeign) {
			if (ts == null) {
				this.ts = new TypeScript(null, "Api", 4, false);
			} else {
				this.ts = ts;
			}
			if (javaFeign == null) {
				this.javaFeign = new JavaFeign(null, "", 4, "");
			} else {
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
			return "Client{" +
					"ts=" + ts +
					", javaFeign=" + javaFeign +
					'}';
		}

		//        @ConstructorBinding
		public static class TypeScript {


			private final String path;


			private final String apiName;

			private final int indent;

			private final boolean anonymous;

			public TypeScript(String path, String apiName, int indent, boolean anonymous) {
				if (path == null || path.isEmpty()) {
					this.path = null;
				} else {
					if (!path.startsWith("/")) {
						throw new IllegalArgumentException("`jimmer.client.ts.path` must start with \"/\"");
					}
					this.path = path;
				}
				if (apiName == null || apiName.isEmpty()) {
					this.apiName = "Api";
				} else {
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

			public int getIndent() {
				return indent;
			}

			public boolean isAnonymous() {
				return anonymous;
			}

			@Override
			public String toString() {
				return "TypeScript{" +
						"path='" + path + '\'' +
						", anonymous=" + anonymous +
						'}';
			}
		}

		//        @ConstructorBinding
		public static class JavaFeign {


			private final String path;


			private final String apiName;

			private final int indent;

			private final String basePackage;

			public JavaFeign(String path, String apiName, int indent, String basePackage) {
				if (path == null || path.isEmpty()) {
					this.path = null;
				} else {
					if (!path.startsWith("/")) {
						throw new IllegalArgumentException("`jimmer.client.ts.path` must start with \"/\"");
					}
					this.path = path;
				}
				if (apiName == null || apiName.isEmpty()) {
					this.apiName = "Api";
				} else {
					this.apiName = apiName;
				}
				this.indent = indent != 0 ? Math.max(indent, 2) : 4;
				if (basePackage == null || basePackage.isEmpty()) {
					this.basePackage = "";
				} else {
					this.basePackage = basePackage;
				}
			}


			public String getPath() {
				return path;
			}


			public String getApiName() {
				return apiName;
			}

			public int getIndent() {
				return indent;
			}

			public String getBasePackage() {
				return basePackage;
			}

			@Override
            public String toString() {
                return "JavaFeign{" +
                        "path='" + path + '\'' +
                        ", clientName='" + apiName + '\'' +
                        ", indent=" + indent +
                        ", basePackage=" + basePackage +
                        '}';
            }
        }
    }
}
