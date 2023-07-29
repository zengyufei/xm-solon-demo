package com.xunmo.jimmer.integration;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xunmo.XmPackageNameConstants;
import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.annotation.Db;
import com.xunmo.jimmer.interceptor.XmCacheInterceptor;
import com.xunmo.jimmer.interceptor.XmCachePutInterceptor;
import com.xunmo.jimmer.interceptor.XmCacheRemoveInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceWrapConsumer;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;
import org.noear.solon.serialization.jackson.JacksonSerializer;

import javax.sql.DataSource;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

@Slf4j
public class XmJimmerPluginImp implements Plugin {

	private static void initJackson(SolonApp app, AopContext context) {
		// 给 body 塞入 arg 参数
		context.beanOnloaded(aopContext -> {
			final ChainManager chainManager = app.chainManager();
			chainManager.removeExecuteHandler(JacksonActionExecutor.class);
			final JacksonActionExecutor jacksonActionExecutor = new JacksonActionExecutor() {
				@Override
				protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
					final Object o = super.changeBody(ctx, mWrap);
					if (o instanceof ObjectNode) {
						final ObjectNode changeBody = (ObjectNode) o;
						ctx.paramMap().forEach((key, value) -> {
							if (!changeBody.has(key)) {
								changeBody.put(key, value);
							}
						});
					}
					return o;
				}
			};
			final ObjectMapper objectMapper = jacksonActionExecutor.config();
			getObjectMapper(objectMapper);

			// 框架默认 -99
			aopContext.lifecycle(-199, () -> {
				final StringSerializerRender render = new StringSerializerRender(false,
						new JacksonSerializer(objectMapper));
				RenderManager.mapping("@json", render);
				RenderManager.mapping("@type_json", render);
			});

			// 支持 json 内容类型执行
			aopContext.wrapAndPut(JacksonActionExecutor.class, jacksonActionExecutor);
			EventBus.push(jacksonActionExecutor);
			aopContext.wrapAndPut(ObjectMapper.class, objectMapper);
			EventBus.push(objectMapper);

			chainManager.addExecuteHandler(jacksonActionExecutor);
		});

	}

	private static void getObjectMapper(ObjectMapper objectMapper) {
		objectMapper.registerModule(new ImmutableModule());
		objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTypingAsProperty(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
		objectMapper.registerModule(new JavaTimeModule());
		// 允许使用未带引号的字段名
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许使用单引号
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.setDefaultTyping(null);

		// 启用 transient 关键字
		objectMapper.configure(PROPAGATE_TRANSIENT_MARKER, true);
		// 启用排序（即使用 LinkedHashMap）
		objectMapper.configure(SORT_PROPERTIES_ALPHABETICALLY, true);
		// 是否识别不带引号的key
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 是否识别单引号的key
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 浮点数默认类型（dubbod 转 BigDecimal）
		objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

		// 反序列化时候遇到不匹配的属性并不抛出异常
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 序列化时候遇到空对象不抛出异常
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 反序列化的时候如果是无效子类型,不抛出异常
		objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
	}

	@Override
	public void start(AopContext context) {
		final SolonApp app = Solon.app();
		app.enableCaching(false);
		initJackson(app, context);

		// context.getBeanAsync(ObjectMapper.class, bean -> {
		// // bean 获取后，可以做些后续处理。。。
		// log.info("{} 异步订阅 ObjectMapper, 执行 jimmer 初始化动作",
		// XmPackageNameConstants.XM_JIMMER);
		// final ImmutableModule immutableModule = new ImmutableModule();
		// bean.registerModule(immutableModule);
		// EventBus.push(immutableModule);
		// });

		context.subWrapsOfType(DataSource.class, bw -> {
			JimmerAdapterManager.add(bw);
		});

		context.lifecycle(-99, () -> {
			JimmerAdapterManager.register();
		});

		// for new
		context.beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
			builderAddDo(clz, wrap, anno.value());
		});

		context.beanInjectorAdd(Db.class, (varH, anno) -> {
			injectorAddDo(varH, anno.value());
		});

		Solon.context().getBeanAsync(CacheService.class, cacheService -> {
			log.info("{} 异步订阅 CacheService, 执行 jimmer 动作", XmPackageNameConstants.XM_JIMMER);
			Solon.context().subWrapsOfType(CacheService.class, new CacheServiceWrapConsumer());
		});

		context.beanAroundAdd(CachePut.class, new XmCachePutInterceptor(), 110);
		context.beanAroundAdd(CacheRemove.class, new XmCacheRemoveInterceptor(), 110);
		context.beanAroundAdd(Cache.class, new XmCacheInterceptor(), 111);

		log.info("{} 包加载完毕!", XmPackageNameConstants.XM_JIMMER);
	}

	private void builderAddDo(Class<?> clz, BeanWrap wrap, String annoValue) {
		if (!clz.isInterface()) {
			return;
		}

		if (Utils.isEmpty(annoValue)) {
			wrap.context().getWrapAsync(JimmerAdapter.class, (dsBw) -> {
				final JimmerAdapter adapter = dsBw.raw();
				final BeanWrap dsWrap = adapter.getDsWrap();
				final String name = dsWrap.name();
				if (dsWrap.typed()) {
					create0(clz, dsWrap);
				}
			});
		} else {
			wrap.context().getWrapAsync(JimmerAdapter.class, (dsBw) -> {
				final JimmerAdapter adapter = dsBw.raw();
				final BeanWrap dsWrap = adapter.getDsWrap();
				final String name = dsWrap.name();
				if (!dsWrap.typed() && StrUtil.equalsIgnoreCase(name, annoValue)) {
					create0(clz, dsWrap);
				}
			});
		}
	}

	private void injectorAddDo(VarHolder varH, String annoValue) {
		if (Utils.isEmpty(annoValue)) {
			varH.context().getWrapAsync(JimmerAdapter.class, (dsBw) -> {
				final JimmerAdapter adapter = dsBw.raw();
				final BeanWrap dsWrap = adapter.getDsWrap();
				if (dsWrap.typed()) {
					inject0(varH, dsWrap);
				}
			});
		} else {
			varH.context().getWrapAsync(JimmerAdapter.class, (dsBw) -> {
				final JimmerAdapter adapter = dsBw.raw();
				final BeanWrap dsWrap = adapter.getDsWrap();
				final String name = dsWrap.name();
				if (!dsWrap.typed() && StrUtil.equalsIgnoreCase(name, annoValue)) {
					inject0(varH, dsWrap);
				}
			});
		}
	}

	private void create0(Class<?> clz, BeanWrap dsBw) {
		JimmerAdapter raw = JimmerAdapterManager.get(dsBw);
		// 进入容器，用于 @Inject 注入
		dsBw.context().wrapAndPut(clz, raw.getRepository(clz));
	}

	private void inject0(VarHolder varH, BeanWrap dsBw) {
		JimmerAdapter jimmerAdapter = JimmerAdapterManager.get(dsBw);

		if (jimmerAdapter != null) {
			jimmerAdapter.injectTo(varH, dsBw);
		}
	}

	@Override
	public void stop() throws Throwable {
		log.info("{} 插件关闭!", XmPackageNameConstants.XM_JIMMER);
	}

}
