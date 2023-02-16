package com.xunmo.core.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author MCJ
 * @date 2018年7月30日
 * @description 提供精确的浮点数运算(包括加 、 减 、 乘 、 除 、 四舍五入)工具类
 */
public class BigDecimalUtil {

    /**
     * 除法运算默认精度
     */
    private static final int DEF_DIV_SCALE = 2;

    private BigDecimalUtil() {
    }

    /**
     * 精确加法
     */
    public static BigDecimal add(Object... values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Object value : values) {
            BigDecimal temp = toBigDecimal(value);
            sum = sum.add(temp);
        }
        return sum;
    }

    /**
     * 精确减法
     */
    public static BigDecimal sub(Object... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = null;
        for (Object value : values) {
            if (sum == null) {
                sum = toBigDecimal(value);
            } else {
                BigDecimal temp = toBigDecimal(value);
                sum = sum.subtract(temp);
            }
        }
        return sum;
    }

    /**
     * 精确乘法
     */
    public static BigDecimal mul(Object... values) {
        if (ArrayUtil.isEmpty(values)) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = null;
        for (Object value : values) {
            if (sum == null) {
                sum = toBigDecimal(value);
            } else {
                BigDecimal temp = toBigDecimal(value);
                sum = sum.multiply(temp);
            }
        }
        return sum;
    }

    /**
     * 精确除法 使用默认精度
     */
    public static BigDecimal div(Object value1, Object value2) {
        return div(value1, value2, DEF_DIV_SCALE);
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static BigDecimal div(Object value1, Object value2, int scale) {
        return div(value1, value2, scale, RoundingMode.HALF_UP);
    }


    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static BigDecimal div(Object value1, Object value2, int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            throw new NumberFormatException("精确度不能小于0");
        }
        BigDecimal b1 = toBigDecimal(value1);
        if (equalTo(b1, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        if (value2 == null || toBigDecimal(value2).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal b2 = toBigDecimal(value2);
        return b1.divide(b2, scale, roundingMode);
    }

    /**
     * 精确除法
     *
     * @param scale 精度
     */
    public static BigDecimal div(Object value1, Object value2, int scale, int roundingMode) {
        if (scale < 0) {
            throw new NumberFormatException("精确度不能小于0");
        }
        BigDecimal b1 = toBigDecimal(value1);
        if (equalTo(b1, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        if (value2 == null || toBigDecimal(value2).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal b2 = toBigDecimal(value2);
        return b1.divide(b2, scale, RoundingMode.valueOf(roundingMode));
    }

    /**
     * 四舍五入
     *
     * @param scale 小数点后保留几位
     */
    public static BigDecimal round(Double v, int scale) {
        return div(v, 1d, scale);
    }

    /**
     * 四舍五入
     *
     * @param scale 小数点后保留几位
     */
    public static BigDecimal round(String v, int scale) {
        return div(v, "1", scale);
    }

    /**
     * 比较大小
     */
    public static boolean equalTo(BigDecimal b1, BigDecimal b2) {
        if (b1 == null || b2 == null) {
            return false;
        }
        return 0 == b1.compareTo(b2);
    }

    /**
     * 将Object转换为BigDecimal
     */
    public static BigDecimal toBigDecimal(Object obj) {
        return Convert.toBigDecimal(obj, BigDecimal.ZERO);
    }

    /**
     * 判断是否数字
     */
    private static void isNumber(String v) {
        if (!NumberUtil.isNumber(v)) {
            throw new NumberFormatException("非数字入参，请输入有效的数字");
        }
    }

    /**
     * 功能描述: 构造链 <br/>
     */
    public static BigDecimalChain chain(BigDecimal bd) {
        return BigDecimalChain.chain(bd);
    }

    /**
     * 功能描述: 构造链 <br/>
     */
    public static BigDecimalChain chain(String bd) {
        return BigDecimalChain.chain(bd);
    }

    /**
     * 功能描述: 构造链 <br/>
     */
    public static BigDecimalChain chain(long bd) {
        return BigDecimalChain.chain(bd);
    }

    /**
     * 功能描述: 构造链 <br/>
     */
    public static BigDecimalChain chain(double bd) {
        return BigDecimalChain.chain(bd);
    }


    /**
     * 功能描述: BigDecimal的链式计算 <br/>
     * 依赖hutool 的 NumberUtil
     */
    public static class BigDecimalChain {

        /**
         * 返回结果
         */
        private BigDecimal result;

        private BigDecimalChain(BigDecimal result) {
            this.result = result;
        }

        /**
         * 功能描述: 构造链 <br/>
         */
        public static BigDecimalChain chain(BigDecimal bd) {
            return new BigDecimalChain(bd);
        }

        /**
         * 功能描述: 构造链 <br/>
         */
        public static BigDecimalChain chain(String bd) {
            return new BigDecimalChain(toBigDecimal(bd));
        }

        /**
         * 功能描述: 构造链 <br/>
         */
        public static BigDecimalChain chain(long bd) {
            return new BigDecimalChain(toBigDecimal(bd));
        }

        /**
         * 功能描述: 构造链 <br/>
         */
        public static BigDecimalChain chain(double bd) {
            return new BigDecimalChain(toBigDecimal(bd));
        }

        /**
         * 加
         */
        public BigDecimalChain add(BigDecimal v) {
            this.result = BigDecimalUtil.add(this.result, v);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(String v) {
            this.result = BigDecimalUtil.add(this.result, v);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(String... vs) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(vs));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(BigDecimalChain... vs) {
            for (BigDecimalChain chain : vs) {
                this.result = BigDecimalUtil.add(this.result, chain.get());
            }
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(int v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(float v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(double v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(int v1, int v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(float v1, float v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(float v1, double v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(double v1, float v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(double v1, double v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Integer v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Float v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Double v1) {
            this.result = BigDecimalUtil.add(this.result, v1);
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Float v1, Float v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Double v1, Double v2) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[]{v1, v2}));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Integer... vs) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(vs));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Float... vs) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(vs));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Double... vs) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(vs));
            return this;
        }

        /**
         * 加
         */
        public BigDecimalChain add(Number... vs) {
            this.result = BigDecimalUtil.add(this.result, NumberUtil.add(vs));
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(BigDecimal v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(String v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(int v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(float v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(double v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(Integer v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(Float v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(Double v) {
            this.result = BigDecimalUtil.sub(this.result, v);
            return this;
        }

        /**
         * 减
         */
        public BigDecimalChain sub(BigDecimalChain... vs) {
            for (BigDecimalChain chain : vs) {
                this.result = BigDecimalUtil.sub(this.result, chain.get());
            }
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(BigDecimal v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(String v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(int v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(float v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(double v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(Integer v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(Float v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(Double v) {
            this.result = BigDecimalUtil.mul(this.result, v);
            return this;
        }

        /**
         * 乘
         */
        public BigDecimalChain mul(BigDecimalChain... vs) {
            for (BigDecimalChain chain : vs) {
                this.result = BigDecimalUtil.mul(this.result, chain.get());
            }
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(BigDecimal v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(String v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(int v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(float v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(double v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(Integer v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(Float v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(Double v) {
            this.result = BigDecimalUtil.div(this.result, v, DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除
         */
        public BigDecimalChain div(BigDecimalChain chain) {
            this.result = BigDecimalUtil.div(this.result, chain.get(), DEF_DIV_SCALE, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(BigDecimal v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(String v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(int v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(float v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(double v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Integer v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Float v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Double v, int scale) {
            this.result = BigDecimalUtil.div(this.result, v, scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(String v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(int v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(float v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(double v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Integer v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Float v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(Double v, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, v, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 四舍五入
         */
        public BigDecimalChain div(BigDecimalChain chain, int scale) {
            this.result = BigDecimalUtil.div(this.result, chain.get(), scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 除 带精度 保留小数模式
         */
        public BigDecimalChain div(BigDecimal div, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, div, scale, roundingMode);
            return this;
        }

        /**
         * 除 带精度 保留小数模式
         */
        public BigDecimalChain div(BigDecimalChain chain, int scale, RoundingMode roundingMode) {
            this.result = BigDecimalUtil.div(this.result, chain.get(), scale, roundingMode);
            return this;
        }

        /**
         * 精度
         */
        public BigDecimalChain scale(int scale) {
            this.result = result.setScale(scale, RoundingMode.HALF_UP);
            return this;
        }

        /**
         * 精度 保留小数模式
         */
        public BigDecimalChain scale(int scale, RoundingMode roundingMode) {
            this.result = result.setScale(scale, roundingMode);
            return this;
        }

        /**
         * 获取最终结果
         */
        public BigDecimal get() {
            return result;
        }

        /**
         * 获取最终结果
         */
        @Override
        public String toString() {
            return result.toPlainString();
        }


    }

}
