package com.xunmo.jimmer.interceptor;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.cache.CacheLib;

/**
 * 缓存移除拦截器
 *
 * @author noear
 * @since 1.0
 */
public class XmCacheRemoveInterceptor implements Interceptor {
	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		Object tmp = inv.invoke();

		if (CacheLib.cacheServiceMap().isEmpty()) {
			return tmp;
		}
		CacheRemove anno = inv.method().getAnnotation(CacheRemove.class);
		CacheExecutorImp.global
				.cacheRemove(anno, inv, tmp);

		return tmp;
	}
}
