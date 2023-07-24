package com.xunmo.jimmer.interceptor;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.cache.CacheLib;

/**
 * 缓存更新拦截器
 *
 * @author noear
 * @since 1.0
 */
public class XmCachePutInterceptor implements Interceptor {

	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		Object tmp = inv.invoke();

		if (CacheLib.cacheServiceMap().isEmpty()) {
			return tmp;
		}
		CachePut anno = inv.method().getAnnotation(CachePut.class);
		CacheExecutorImp.global.cachePut(anno, inv, tmp);

		return tmp;
	}

}
