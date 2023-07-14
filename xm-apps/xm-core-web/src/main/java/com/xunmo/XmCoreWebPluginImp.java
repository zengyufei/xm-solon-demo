package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.core.*;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.around.CacheInterceptor;
import org.noear.solon.data.around.CachePutInterceptor;
import org.noear.solon.data.around.CacheRemoveInterceptor;
import org.noear.solon.data.cache.CacheLib;
import org.noear.solon.data.cache.CacheService;
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
			// 解决 cros 跨域 问题;
			app.before(new CrossHandler().allowedOrigins("*"));
		}
		if (isEnableArgsTrim) {
			// 处理参数左右空白, 空字符串入参设置为null;
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
							paramMap.put(key, StrUtil.trim(value));
						}
					}
				}
			});
		}

		// 手动注册缓存
		final boolean isEnabled = props.getBool("xm.web.cache.enable", true);
		if (isEnabled) {
			app.enableCaching(false);
			ThreadUtil.execute(() -> {
				RedissonCacheService redisCacheService = props.getBean("xm.web.cache", RedissonCacheService.class);
				if (redisCacheService != null) {
					// 可以进行手动字段注入
					context.beanInject(redisCacheService);
					// 添加缓存控制支持
					CacheLib.cacheServiceAdd("", redisCacheService);
					// 包装Bean（指定类型的）
					// 以类型注册
					context.putWrap(RedissonClient.class,
							new BeanWrap(context, RedissonClient.class, redisCacheService.client(), null, true));
					context.putWrap(RedissonCacheService.class,
							new BeanWrap(context, RedissonCacheService.class, redisCacheService, null, true));
					context.putWrap(CacheService.class,
							new BeanWrap(context, CacheService.class, redisCacheService, null, true));
				}
			});
			context.beanAroundAdd(CachePut.class, new CachePutInterceptor(), 110);
			context.beanAroundAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
			context.beanAroundAdd(Cache.class, new CacheInterceptor(), 111);
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_CORE_WEB);
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
