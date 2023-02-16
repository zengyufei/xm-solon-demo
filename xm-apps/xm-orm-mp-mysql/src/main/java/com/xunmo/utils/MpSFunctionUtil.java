package com.xunmo.utils;

import cn.hutool.core.lang.SimpleCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.xunmo.core.utils.LamUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class MpSFunctionUtil {

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
                log.error("SFunction 创建失败! {},{}", clazz, method);
                throw new RuntimeException(throwable);
            }
        }
        return (SFunction<T, ?>) sFunction;
    }


    /**
     * 获取方法名
     *
     * @param sFunction 方法引用
     * @param <T>       泛型
     * @return 方法名
     */
    public static <T> String getMethodName(SFunction<T, ?> sFunction) {
        Class<?> clazz = sFunction.getClass();
        try {
            Method writeReplace = clazz.getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(sFunction);
            return lambda.getImplMethodName();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        log.error("SFunction 解析失败! {},{}", clazz, sFunction);
        return null;
    }


    public static <T> SFunction<T, ?> getSFunction(Class<T> clazz, com.xunmo.ext.SFunction<T, Object> sFunction) {
        final Method disabledMethod = LamUtil.getGetter(clazz, sFunction);
        return MpSFunctionUtil.create(clazz, disabledMethod);
    }
}
