package com.xunmo.core.utils;

import com.xunmo.common.SFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XmMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = -5260635176470805065L;

	private static Map<Class, SerializedLambda> CLASS_LAMDBA_CACHE = new ConcurrentHashMap<>();

	public <T> boolean containsKey(SFunction<T, ?> key) {
		return super.containsKey((K) XmMap.getField(key));
	}

	public <T> V remove(SFunction<T, ?> key) {
		return super.remove(key);
	}

	public <T> V putIfAbsent(SFunction<T, ?> key, V v) {
		return super.putIfAbsent((K) XmMap.getField(key), v);
	}

	public <T> V get(SFunction<T, ?> key) {
		return super.get(XmMap.getField(key));
	}

	public <T> V put(SFunction<T, ?> key, V v) {
		return super.put((K) XmMap.getField(key), v);
	}

	/**
	 * 将bean的属性的get方法，作为lambda表达式传入时，获取get方法对应的属性Field
	 * @param fn lambda表达式，bean的属性的get方法
	 * @param <T> 泛型
	 * @return 属性对象
	 */
	public static <T> String getField(SFunction<T, ?> fn) {
		// 从序列化方法取出序列化的lambda信息
		SerializedLambda serializedLambda = getSerializedLambda(fn);
		// 获取方法名
		String implMethodName = serializedLambda.getImplMethodName();
		String prefix = null;
		if (implMethodName.startsWith("get")) {
			prefix = "get";
		}
		else if (implMethodName.startsWith("is")) {
			prefix = "is";
		}
		if (prefix == null) {
			throw new RuntimeException("get方法名称: " + implMethodName + ", 不符合java bean规范");
		}
		// 截取get/is之后的字符串并转换首字母为小写
		return toLowerCaseFirstOne(implMethodName.replace(prefix, ""));
	}

	/**
	 * 关键在于这个方法
	 */
	private static SerializedLambda getSerializedLambda(Serializable fn) {
		final Class<? extends Serializable> fnClass = fn.getClass();
		SerializedLambda lambda = CLASS_LAMDBA_CACHE.get(fnClass);
		// 先检查缓存中是否已存在
		if (lambda == null) {
			try {
				// 提取SerializedLambda并缓存
				Method method = fnClass.getDeclaredMethod("writeReplace");
				boolean isAccessible = method.isAccessible();
				method.setAccessible(Boolean.TRUE);
				lambda = (SerializedLambda) method.invoke(fn);
				method.setAccessible(isAccessible);
				CLASS_LAMDBA_CACHE.put(fnClass, lambda);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lambda;
	}

	/**
	 * 首字母转小写
	 * @param s s
	 * @return string
	 */
	private static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		}
		else {
			return Character.toLowerCase(s.charAt(0)) + s.substring(1);
		}
	}

}
