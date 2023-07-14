package com.xunmo.core;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Ajax发生异常时，直接抛出此异常即可 （比AjaxException更先进的版本）
 *
 * @author kong
 */
public class AjaxError extends RuntimeException {

	// ========================= 定义属性 =========================

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_ERROR_CODE = 500;

	private int code = DEFAULT_ERROR_CODE; // 底层code码

	/**
	 * @return 获取code码
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return 写入code码 ，连缀风格
	 */
	public AjaxError setCode(int code) {
		this.code = code;
		return this;
	}

	// ========================= 构造方法 =========================

	public AjaxError(int code, String message) {
		super(message);
		setCode(code);
	}

	public AjaxError(String message) {
		super(message);
	}

	public AjaxError(Throwable e) {
		super(e);
	}

	public AjaxError(String message, Throwable e) {
		super(message, e);
	}

	// ========================= 获取相关 =========================

	/**
	 * 获得一个异常AjaxError
	 */
	public static AjaxError get(String errorMsg) {
		return new AjaxError(errorMsg);
	}

	/**
	 * 获得一个异常AjaxError
	 */
	public static AjaxError get(int code, String errorMsg) {
		return new AjaxError(code, errorMsg);
	}

	/**
	 * 获得一个异常AjaxError
	 */
	public static AjaxError get(Throwable e) {
		return new AjaxError(e);
	}

	// ========================= 获取并抛出 =========================

	/**
	 * 获得一个异常，并直接抛出
	 */
	public static void getAndThrow(String errorMsg) {
		throw new AjaxError(errorMsg);
	}

	/**
	 * 获得一个异常，并直接抛出
	 */
	public static void getAndThrow(int code, String errorMsg) {
		throw new AjaxError(code, errorMsg);
	}

	public static void getAndThrow(String errorMsg, Object... val) {
		throw new AjaxError(StrUtil.format(errorMsg, val));
	}

	/**
	 * 如果条件为true，则抛出异常
	 */
	public static void throwBy(boolean bo, int code, String errorMsg) {
		if (bo) {
			throw get(code, errorMsg);
		}
	}

	/**
	 * 如果条件为true，则抛出异常
	 */
	public static void throwBy(boolean bo, String errorMsg) {
		if (bo) {
			getAndThrow(errorMsg);
		}
	}

	/**
	 * 如果条件为true，则抛出异常
	 */
	public static void throwBy(boolean bo, Supplier<String> errorMsg) {
		if (bo) {
			getAndThrow(errorMsg.get());
		}
	}

	/**
	 * 如果条件为true，则抛出异常
	 */
	public static void throwBy(boolean bo) {
		if (bo) {
			getAndThrow("error");
		}
	}

	/**
	 * 根据受影响行数的(大于0通过，小于等于0抛出error)
	 */
	public static void throwByLine(int line, int code, String errorMsg) {
		if (line <= 0) {
			getAndThrow(code, errorMsg);
		}
	}

	/**
	 * 根据受影响行数的(大于0通过，小于等于0抛出error)
	 */
	public static void throwByLine(int line, String errorMsg) {
		if (line <= 0) {
			getAndThrow(errorMsg);
		}
	}

	/**
	 * 根据受影响行数的(大于0通过，小于等于0抛出error)
	 */
	public static void throwByLine(int line) {
		if (line <= 0) {
			getAndThrow("受影响行数：0");
		}
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, String errorMsg) {
		throwByIsNull(value, null, errorMsg);
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Supplier<String> errorMsg) {
		throwByIsNull(value, null, errorMsg.get());
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value) {
		throwByIsNull(value, null, null);
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Integer code, String errorMsg) {
		if (isNull(value)) {
			if (code == null) {
				throw get(StrUtil.blankToDefault(errorMsg, "不能为空"));
			}
			else {
				throw get(code, StrUtil.blankToDefault(errorMsg, "不能为空"));
			}
		}
	}

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

}
