package com.xunmo.exceptions;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component(index = 0)
public class GlobalException implements RouterInterceptor {

	public static final Map<Class<? extends Throwable>, XmExceptionEntity> errorByXmExceptionEntity = new HashMap<>();

	@Override
	public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
		// if (mainHandler != null) {
		// } else {
		// // 处理这种写法
		// // Solon.app().get("/XXX", ctx -> {});
		// }
		try {
			chain.doIntercept(ctx, mainHandler);
		}
		catch (Throwable e) {
			// 获取异常类型
			final Class<? extends Throwable> errorClass = e.getClass();
			// 获取异常处理函数定义
			XmExceptionEntity xmExceptionEntity = errorByXmExceptionEntity.get(errorClass);
			if (xmExceptionEntity == null) {
				xmExceptionEntity = errorByXmExceptionEntity.get(Throwable.class);
			}
			if (xmExceptionEntity == null) {
				xmExceptionEntity = errorByXmExceptionEntity.get(Exception.class);
			}
			final Method method = xmExceptionEntity.method;

			// 获取异常处理函数参数列表
			final Class<?>[] parameterTypes = method.getParameterTypes();
			final Object[] objects = new Object[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				final Class<?> parameterType = parameterTypes[i];
				if (parameterType == Context.class) {
					// 上下文塞入
					objects[i] = ctx;
				}
				else if (errorClass == xmExceptionEntity.errorClass) {
					// 完全对等异常处理
					objects[i] = e;
				}
				else if (Throwable.class.isAssignableFrom(errorClass)) {
					// 找不到异常处理方法是，使用默认的处理函数
					objects[i] = e;
				}
			}

			// 执行异常函数
			final Object invoke = method.invoke(xmExceptionEntity.object, objects);
			ctx.render(invoke);
		}
	}

}
