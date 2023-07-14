package com.xunmo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.ChainManager;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.extend.quartz.EnableQuartz;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;
import org.noear.solon.serialization.jackson.JacksonSerializer;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@EnableQuartz
public class BizApp {

	public static void main(String[] args) throws NoSuchFieldException {
		Solon.start(BizApp.class, args, app -> {

			// 转发日志到 Slf4j 接口
			LogUtil.globalSet(new LogUtilToSlf4j()); // v1.10.11 后支持

			app.get("/", ctx -> {
				// ctx.forward("/railway-bureau-test/index.html");
				ctx.redirect("/dict/view/tree");
				// ctx.redirect("/employee_info/view/index");
			});

			// 异步订阅方式，根据bean type获取Bean（已存在或产生时，会通知回调；否则，一直不回调）
			Solon.context().getBeanAsync(CacheService.class, bean -> {
				// bean 获取后，可以做些后续处理。。。
				System.out.println("异步订阅 CacheService, 执行初始化缓存动作");
			});

			// 异步订阅方式，根据bean type获取Bean（已存在或产生时，会通知回调；否则，一直不回调）
			// Solon.context().getBeanAsync(RedissonCacheService.class, bean -> {
			// //bean 获取后，可以做些后续处理。。。
			// System.out.println("异步订阅 RedissonCacheService, 执行初始化缓存动作");
			// });

			// 2.添加资源路径
			// StaticMappings.add("/railway-bureau-test", false, new
			// ClassPathStaticRepository("railway-bureau-test"));
			// 3.添加扩展目录：${solon.extend}/static/
			// StaticMappings.add("/railway-bureau-test", relativePath -> {
			// String location;
			// String path = Solon.cfg().sourceLocation().getPath();
			// if (path == null) {
			// throw new IllegalStateException("No extension directory exists");
			// } else {
			// location = path + "railway-bureau-test";
			// }
			// File file = new File(location , relativePath);
			// return file.exists() ? file.toURI().toURL() : null;
			// });
			// 自动添加 multipart
			// app.filter(-1, (ctx, chain) -> {
			// if (ctx.path().startsWith("/upload")) {
			// ctx.autoMultipart(true); //给需要的路径加 autoMultipart
			// }
			// chain.doFilter(ctx);
			// });

			// 向外提供钩子
			// app.before(9999999, ctx -> {
			// final PageRequest pageRequest = ctx.paramAsBean(PageRequest.class);
			// ctx.paramSet("pageRequest", JSONUtil.toJsonStr(pageRequest));
			// System.out.println(JSONUtil.toJsonPrettyStr(pageRequest));
			// });

			initJackson(app);

		});
	}

	private static void initJackson(SolonApp app) {
		// 给 body 塞入 arg 参数
		app.context().beanOnloaded(aopContext -> {
			final ChainManager chainManager = app.chainManager();
			chainManager.removeExecuteHandler(JacksonActionExecutor.class);
			final JacksonActionExecutor jacksonActionExecutor = new JacksonActionExecutor() {
				@Override
				protected Object changeBody(Context ctx) throws Exception {
					final Object o = super.changeBody(ctx);
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
			// 不设置为null会自动加入 @type 打印 json
			objectMapper.setDefaultTyping(null);
			final ImmutableModule immutableModule = new ImmutableModule();
			initModule(immutableModule);
			objectMapper.registerModule(immutableModule);

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

			chainManager.addExecuteHandler(jacksonActionExecutor);
		});
	}

	public static void initModule(ImmutableModule immutableModule) {
		// ======================= 时间序列化规则 ===============================
		// yyyy-MM-dd HH:mm:ss
		immutableModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
		// yyyy-MM-dd
		immutableModule.addSerializer(LocalDate.class,
				new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
		// HH:mm:ss
		immutableModule.addSerializer(LocalTime.class,
				new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
		// yyyy
		immutableModule.addSerializer(Year.class, new YearSerializer(DateTimeFormatter.ofPattern("yyyy")));
		// MM
		immutableModule.addSerializer(YearMonth.class, new YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")));

		// Instant 类型序列化
		immutableModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);

		// ======================= 时间反序列化规则 ==============================
		// yyyy-MM-dd HH:mm:ss
		immutableModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
		// yyyy-MM-dd
		immutableModule.addDeserializer(LocalDate.class,
				new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
		// HH:mm:ss
		immutableModule.addDeserializer(LocalTime.class,
				new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
		// yyyy
		immutableModule.addDeserializer(Year.class, new YearDeserializer(DateTimeFormatter.ofPattern("yyyy")));
		// MM
		immutableModule.addDeserializer(YearMonth.class,
				new YearMonthDeserializer(DateTimeFormatter.ofPattern("yyyy-MM")));

		// Instant 反序列化
		immutableModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);

		immutableModule.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			@Override
			public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				if (StrUtil.isBlank(jsonParser.getValueAsString())) {
					return null;
				}
				return StrUtil.trim(jsonParser.getValueAsString());
			}
		});
		immutableModule.addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			private final String[] DATE_FORMAT_STRS = new String[] { "yyyy",

					"yyyy-M", "yyyy/M", "yyyy.M",

					"yyyy-M", "yyyy/M", "yyyy.M",

					"yyyy-MM", "yyyy/MM", "yyyy.MM",

					"yyyy年M月", "yyyy年M月", "yyyy年MM月",

					"yyyyM", "yyyyM", "yyyyMM",

					"yyyy-M-dd", "yyyy/M/dd", "yyyy.M.dd",

					"yyyy-M-d", "yyyy/M/d", "yyyy.M.d",

					"yyyy-MM-d", "yyyy/MM/d", "yyyy.MM.d",

					"yyyy年M月dd日", "yyyy年M月d日", "yyyy年MM月d日",

					"yyyyMdd", "yyyyMd", "yyyyMMd", };

			@Override
			public Date deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				String text = jsonParser.getText();
				if (StrUtil.isBlank(text)) {
					return null;
				}
				return formatToDate(text);
			}

			public Date formatToDate(String parameter) {
				try {
					return DateUtil.parse(parameter);
				}
				catch (Exception e) {
					for (String dateFormatStr : DATE_FORMAT_STRS) {
						try {
							return DateUtil.parse(parameter, dateFormatStr);
						}
						catch (Exception ignored2) {
						}
					}
					throw e;
				}
			}
		});
	}

}
