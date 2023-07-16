package com.xunmo.core.utils;

import cn.hutool.core.lang.SimpleCache;
import com.xunmo.common.XmFunction;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.*;
import java.lang.reflect.Method;

@Slf4j
public class XmFunctionUtil {

	private static final SimpleCache<Method, XmFunction<?, ?>> mfCache = new SimpleCache<>();

	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

	/**
	 * 创建方法引用 根据方法名对象和类
	 * @param clazz 类
	 * @param method 方法名对象
	 * @param <T> 泛型
	 * @return XmFunction
	 */
	@SuppressWarnings("unchecked")
	public static <T> XmFunction<T, ?> create(Class<T> clazz, Method method) {
		XmFunction<?, ?> xmFunction = mfCache.get(method);
		if (xmFunction == null) {
			try {
				final MethodHandle getMethodHandle = lookup.unreflect(method);
				// 动态调用点
				final CallSite getCallSite = LambdaMetafactory.altMetafactory(lookup, "apply",
						MethodType.methodType(XmFunction.class), MethodType.methodType(Object.class, Object.class),
						getMethodHandle, MethodType.methodType(Object.class, clazz),
						LambdaMetafactory.FLAG_SERIALIZABLE, Serializable.class);
				xmFunction = (XmFunction<T, ?>) getCallSite.getTarget().invokeExact();
				mfCache.put(method, xmFunction);
			}
			catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
			log.error("XmFunction 创建失败! {},{}", clazz, method);
		}
		return (XmFunction<T, ?>) xmFunction;
	}

}
