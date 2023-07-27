package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xunmo.annotations.valid.IsDate;
import com.xunmo.annotations.valid.IsEnum;
import com.xunmo.annotations.valid.IsNumber;
import com.xunmo.config.RedissonCodec;
import com.xunmo.jackson.DoubleDeserializer;
import com.xunmo.jackson.IntegerDeserializer;
import com.xunmo.jackson.LongDeserializer;
import com.xunmo.utils.XmRedissonBuilder;
import com.xunmo.valid.IsDateValidator;
import com.xunmo.valid.IsEnumValidator;
import com.xunmo.valid.IsNumberValidator;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.core.*;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.validation.ValidatorManager;
import org.noear.solon.web.cors.CrossHandler;
import org.redisson.api.RedissonClient;

import java.util.Iterator;
import java.util.Map;

@Slf4j
public class XmCoreWebPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_CORE_WEB + ".yml");
		final SolonApp app = Solon.app();

		final boolean isEnableCors = props.getBool(XmPluginPropertiesConstants.xmWebCorsEnable, true);
		final boolean isEnableArgsTrim = props.getBool(XmPluginPropertiesConstants.xmWebArgsTrimEnable, true);

		if (isEnableCors) {
			// 解决 cros 跨域 问题
			app.before(new CrossHandler().allowedOrigins("*"));
		}
		if (isEnableArgsTrim) {
			// 处理参数左右空白, 空字符串入参设置为null
			app.before(ctx -> {
				final NvMap paramMap = ctx.paramMap();
				if (!paramMap.isEmpty()) {
					final Iterator<Map.Entry<String, String>> entryIterator = paramMap.entrySet().iterator();
					while (entryIterator.hasNext()) {
						final Map.Entry<String, String> entry = entryIterator.next();
						final String key = entry.getKey();
						final String value = entry.getValue();
						if (StrUtil.isBlankOrUndefined(value)) {
							entryIterator.remove();
						}
						else {
							final String val = StrUtil.trim(EscapeUtil.unescapeHtml4(value));
							paramMap.put(key, val);
						}
					}
				}
			});
		}

		// 手动注册缓存
		final boolean isEnabled = props.getBool("xm.web.cache.enable", true);
		if (isEnabled) {
			// 异步订阅方式，根据bean type获取Bean（已存在或产生时，会通知回调；否则，一直不回调）
			Solon.context().getBeanAsync(ObjectMapper.class, objectMapper -> {
				// bean 获取后，可以做些后续处理。。。
				log.info("{} 异步订阅 ObjectMapper, 执行 手动注册缓存 动作", XmPackageNameConstants.XM_CORE_WEB);

				ThreadUtil.execute(() -> {
					final RedissonClient redissonClient = XmRedissonBuilder.build(props.getProp("xm.web.cache"),
							new RedissonCodec(objectMapper, false));
					RedissonCacheService redisCacheService = new RedissonCacheService(redissonClient, 30);
					// 可以进行手动字段注入
					// context.beanInject(redisCacheService);
					// 包装Bean（指定类型的）
					// 以类型注册
					context.putWrap(CacheService.class,
							new BeanWrap(context, CacheService.class, redisCacheService, null, true));
					context.wrapAndPut(RedissonClient.class, redissonClient);

				});

			});
		}

		// 反序列化超长处理
		Solon.context().getBeanAsync(ObjectMapper.class, objectMapper -> {
			// bean 获取后，可以做些后续处理。。。
			log.info("{} 异步订阅 ObjectMapper, 执行 反序列化超长处理 动作", XmPackageNameConstants.XM_CORE_WEB);

			final SimpleModule simpleModule = new SimpleModule();
			simpleModule.addDeserializer(Double.class, new DoubleDeserializer());
			simpleModule.addDeserializer(Long.class, new LongDeserializer());
			simpleModule.addDeserializer(Integer.class, new IntegerDeserializer());
			objectMapper.registerModule(simpleModule);
		});

		// 注册自定义验证器
		ValidatorManager.register(IsDate.class, IsDateValidator.instance);
		ValidatorManager.register(IsNumber.class, IsNumberValidator.instance);
		ValidatorManager.register(IsEnum.class, IsEnumValidator.instance);

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件包加载完毕!", XmPackageNameConstants.XM_CORE_WEB);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_CORE_WEB + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_CORE_WEB);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_CORE_WEB + " 插件关闭!");
		}
	}

}
