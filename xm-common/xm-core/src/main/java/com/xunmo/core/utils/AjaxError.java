package com.xunmo.core.utils;

import cn.hutool.core.util.StrUtil;

/**
 * Ajax发生异常时，直接抛出此异常即可   （比AjaxException更先进的版本）
 *
 * @author kong
 */
public class AjaxError extends RuntimeException {

    // ========================= 定义属性 =========================

    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_ERROR_CODE = 500;

    private int code = DEFAULT_ERROR_CODE;        // 底层code码

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
            throw get(errorMsg);
        }
    }

    /**
     * 如果条件为true，则抛出异常
     */
    public static void throwBy(boolean bo) {
        if (bo) {
            throw get("error");
        }
    }


    /**
     * 根据受影响行数的(大于0通过，小于等于0抛出error)
     */
    public static void throwByLine(int line, int code, String errorMsg) {
        if (line <= 0) {
            throw get(code, errorMsg);
        }
    }

    /**
     * 根据受影响行数的(大于0通过，小于等于0抛出error)
     */
    public static void throwByLine(int line, String errorMsg) {
        if (line <= 0) {
            throw get(errorMsg);
        }
    }

    /**
     * 根据受影响行数的(大于0通过，小于等于0抛出error)
     */
    public static void throwByLine(int line) {
        if (line <= 0) {
            throw get("受影响行数：0");
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
            } else {
                throw get(code, StrUtil.blankToDefault(errorMsg, "不能为空"));
            }
        }
    }

    /**
     * 指定值是否为以下其一：null、""、0、"0"
     */
    public static boolean isNull(Object value) {
        return CheckUtil.isNull(value);
    }

}
