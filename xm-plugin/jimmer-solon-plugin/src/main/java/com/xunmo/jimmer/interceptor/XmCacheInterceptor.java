package com.xunmo.jimmer.interceptor;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.cache.CacheLib;

/**
 * 缓存拦截器
 *
 * @author noear
 * @since 1.0
 */
public class XmCacheInterceptor implements Interceptor {

	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		if (CacheLib.cacheServiceMap().isEmpty()) {
			return inv.invoke();
		}
		Cache anno = inv.method().getAnnotation(Cache.class);
		return CacheExecutorImp.global.cache(anno, inv, inv::invoke);
	}

}
