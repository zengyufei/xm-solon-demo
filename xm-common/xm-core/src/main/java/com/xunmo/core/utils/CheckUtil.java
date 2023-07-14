package com.xunmo.core.utils;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class CheckUtil {

	public static boolean isNull(Object value) {
		if (value instanceof CharSequence) {
			return StrUtil.isBlankOrUndefined((CharSequence) value);
		}
		else if (ArrayUtil.isArray(value)) {
			return ArrayUtil.isEmpty(value);
		}
		else if (value instanceof Iterable) {
			return IterUtil.isEmpty((Iterable<?>) value);
		}
		else if (value instanceof Iterator) {
			return IterUtil.isEmpty((Iterator<?>) value);
		}
		else if (value instanceof Map) {
			return MapUtil.isEmpty((Map<?, ?>) value);
		}
		else {
			return ObjectUtil.isNull(value);
		}
	}

	public static boolean isNotNull(Object obj) {
		return !isNull(obj);
	}

	public static boolean isBlank(Object obj) {
		return isNull(obj);
	}

	public static boolean isAllBlank(Object... obj) {
		if (isNull(obj)) {
			return true;
		}
		return Stream.of(obj).allMatch(CheckUtil::isNull);
	}

	public static boolean isNotBlank(Object obj) {
		return !isNull(obj);
	}

	public static <E> boolean isEmpty(Collection<E> obj) {
		return isNull(obj);
	}

	public static <E> boolean isNotEmpty(Collection<E> obj) {
		return !isNull(obj);
	}

	public static boolean isNotEmptyArray(Object[] obj) {
		return !isNull(obj);
	}

	public static boolean isEmptyArray(Object[] obj) {
		return isNull(obj);
	}

	public static boolean isNumer(String str) {
		return NumberUtil.isNumber(str);
	}

}
