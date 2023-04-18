package com.xunmo.core.utils;

import cn.hutool.core.lang.SimpleCache;
import com.xunmo.ext.SFunction;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

@Slf4j
public class SFunctionUtil {

    private static final SimpleCache<Method, SFunction<?, ?>> mfCache = new SimpleCache<>();

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    /**
     * 创建方法引用
     * 根据方法名对象和类
     *
     * @param clazz  类
     * @param method 方法名对象
     * @param <T>    泛型
     * @return SFunction
     */
    @SuppressWarnings("unchecked")
    public static <T> SFunction<T, ?> create(Class<T> clazz, Method method) {
        SFunction<?, ?> sFunction = mfCache.get(method);
        if (sFunction == null) {
            try {
                final MethodHandle getMethodHandle = lookup.unreflect(method);
                //动态调用点
                final CallSite getCallSite = LambdaMetafactory.altMetafactory(
                        lookup
                        , "apply"
                        , MethodType.methodType(SFunction.class)
                        , MethodType.methodType(Object.class, Object.class)
                        , getMethodHandle
                        , MethodType.methodType(Object.class, clazz)
                        , LambdaMetafactory.FLAG_SERIALIZABLE
                        , Serializable.class
                );
                sFunction = (SFunction<T, ?>) getCallSite.getTarget().invokeExact();
                mfCache.put(method, sFunction);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            log.error("SFunction 创建失败! {},{}", clazz, method);
        }
        return (SFunction<T, ?>) sFunction;
    }

}
