package com.xunmo.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.utils.TranAfterUtil;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务aop拦截器
 *
 * @author zengyufei
 * @date 2022/11/19
 */
public class TranAfterInterceptor implements Interceptor {

	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		final Method method = inv.method().getMethod();

		final Tran tranAnno = method.getAnnotation(Tran.class);
		if (tranAnno != null) {
			System.out.println(method.getName() + " 拦截事务处理");
			AtomicReference<Object> rst = new AtomicReference<>();
			TranUtils.execute(tranAnno, () -> rst.set(inv.invoke()));
			final Object o = rst.get();
			final List<TranAfterUtil.AfterFunction> afterMethods = TranAfterUtil.getAfterMethods();
			if (CollUtil.isNotEmpty(afterMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterMethods) {
					consumer.test();
				}
			}
			final List<TranAfterUtil.AfterFunction> afterSyncMethods = TranAfterUtil.getAfterSyncMethods();
			if (CollUtil.isNotEmpty(afterSyncMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterSyncMethods) {
					ThreadUtil.execute(consumer::test);
				}
			}
			return o;
		}
		return inv.invoke();
	}

}
