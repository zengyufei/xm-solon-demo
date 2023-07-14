package com.xunmo.core.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

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
			}
			else {
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
			}
			else {
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
	 * @param scale 精度
	 */
	public static BigDecimal div(Object value1, Object value2, int scale) {
		return div(value1, value2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 精确除法
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
	 * @param scale 小数点后保留几位
	 */
	public static BigDecimal round(Double v, int scale) {
		return div(v, 1d, scale);
	}

	/**
	 * 四舍五入
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
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
			return this;
		}

		/**
		 * 加
		 */
		public BigDecimalChain add(float v1, float v2) {
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
			return this;
		}

		/**
		 * 加
		 */
		public BigDecimalChain add(float v1, double v2) {
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
			return this;
		}

		/**
		 * 加
		 */
		public BigDecimalChain add(double v1, float v2) {
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
			return this;
		}

		/**
		 * 加
		 */
		public BigDecimalChain add(double v1, double v2) {
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
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
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
			return this;
		}

		/**
		 * 加
		 */
		public BigDecimalChain add(Double v1, Double v2) {
			this.result = BigDecimalUtil.add(this.result, NumberUtil.add(new Number[] { v1, v2 }));
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

	public static BigDecimal cal(String expression, int scale) {
		return NiBoLan.cal(expression, scale);
	}

}

/**
 * <pre>
 * 完整版的逆波兰计算器，功能包括
 * 支持 + - * / ( )
 * 多位数，支持小数,
 * 兼容处理, 过滤任何空白字符，包括空格、制表符、换页符
 *
 * 逆波兰计算器完整版考虑的因素较多，下面给出完整版代码供同学们学习，其基本思路和前面一样，也是使用到：中缀表达式转后缀表达式。
 * </pre>
 */
class NiBoLan {

	/**
	 * 匹配 + - * / ( ) 运算符
	 */
	static final String SYMBOL = "[+\\-*/()]";

	static final int scale = 32;
	static final String LEFT = "(";
	static final String RIGHT = ")";
	static final String ADD = "+";
	static final String MINUS = "-";
	static final String TIMES = "*";
	static final String DIVISION = "/";

	/**
	 * 加減 + -
	 */
	static final int LEVEL_01 = 1;

	/**
	 * 乘除 * /
	 */
	static final int LEVEL_02 = 2;

	/**
	 * 括号
	 */
	static final int LEVEL_HIGH = Integer.MAX_VALUE;

	static Stack<String> stack = new Stack<>();
	static List<String> data = Collections.synchronizedList(new ArrayList<>());

	/**
	 * 去除所有空白符
	 * @param s 表达式
	 * @return 无空白符表达式
	 */
	private static String replaceAllBlank(String s) {
		// \\s+ 匹配任何空白字符，包括空格、制表符、换页符等等, 等价于[ \f\n\r\t\v]
		return s.replaceAll("\\s+", "");
	}

	/**
	 * 判断是不是数字 int double long float
	 * @param s 值
	 * @return 判断结果
	 */
	private static boolean isNumber(String s) {
		Pattern pattern = Pattern.compile("^[-+]?[.\\d]*$");
		return pattern.matcher(s).matches();
	}

	/**
	 * 判断是不是运算符
	 * @param s 运算符
	 * @return 判断结果
	 */
	private static boolean isSymbol(String s) {
		return s.matches(SYMBOL);
	}

	/**
	 * 匹配运算等级
	 * @param s 表达式
	 * @return 顺序
	 */
	private static int calcLevel(String s) {
		if ("+".equals(s) || "-".equals(s)) {
			return LEVEL_01;
		}
		else if ("*".equals(s) || "/".equals(s)) {
			return LEVEL_02;
		}
		return LEVEL_HIGH;
	}

	/**
	 * 匹配
	 * @param s 表达式
	 */
	public static List<String> doMatch(String s) {
		if (s == null || "".equals(s.trim())) {
			throw new RuntimeException("data is empty");
		}
		if (!isNumber(s.charAt(0) + "")) {
			throw new RuntimeException("data illegal,start not with a number");
		}

		s = replaceAllBlank(s);

		String each;
		int start = 0;

		for (int i = 0; i < s.length(); i++) {
			if (isSymbol(s.charAt(i) + "")) {
				each = s.charAt(i) + "";
				// 栈为空，(操作符，或者 操作符优先级大于栈顶优先级 && 操作符优先级不是( )的优先级 及是 ) 不能直接入栈
				if (stack.isEmpty() || LEFT.equals(each)
						|| ((calcLevel(each) > calcLevel(stack.peek())) && calcLevel(each) < LEVEL_HIGH)) {
					stack.push(each);
				}
				else if (!stack.isEmpty() && calcLevel(each) <= calcLevel(stack.peek())) {
					// 栈非空，操作符优先级小于等于栈顶优先级时出栈入列，直到栈为空，或者遇到了(，最后操作符入栈
					while (!stack.isEmpty() && calcLevel(each) <= calcLevel(stack.peek())) {
						if (calcLevel(stack.peek()) == LEVEL_HIGH) {
							break;
						}
						data.add(stack.pop());
					}
					stack.push(each);
				}
				else if (RIGHT.equals(each)) {
					// ) 操作符，依次出栈入列直到空栈或者遇到了第一个)操作符，此时)出栈
					while (!stack.isEmpty()) {
						calcLevel(stack.peek());
						if (LEVEL_HIGH == calcLevel(stack.peek())) {
							stack.pop();
							break;
						}
						data.add(stack.pop());
					}
				}
				start = i; // 前一个运算符的位置
			}
			else if (i == s.length() - 1 || isSymbol(s.charAt(i + 1) + "")) {
				each = start == 0 ? s.substring(start, i + 1) : s.substring(start + 1, i + 1);
				if (isNumber(each)) {
					data.add(each);
					continue;
				}
				throw new RuntimeException("data not match number");
			}
		}
		// 如果栈里还有元素，此时元素需要依次出栈入列，可以想象栈里剩下栈顶为/，栈底为+，应该依次出栈入列，可以直接翻转整个stack 添加到队列
		Collections.reverse(stack);
		data.addAll(new ArrayList<>(stack));

		return data;
	}

	/**
	 * 算出结果
	 * @param list 数值、表达式分组
	 * @return 运算结果
	 */
	private static BigDecimal doCalc(List<String> list) {
		BigDecimal d;
		if (list == null || list.isEmpty()) {
			return BigDecimal.ZERO;
		}
		if (list.size() == 1) {
			final String s = list.get(0);
			d = new BigDecimal(s);
			return d;
		}
		ArrayList<String> list1 = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			list1.add(list.get(i));
			if (isSymbol(list.get(i))) {
				BigDecimal d1 = doTheMath(list.get(i - 2), list.get(i - 1), list.get(i));
				list1.remove(i);
				list1.remove(i - 1);
				list1.set(i - 2, d1.toPlainString());
				list1.addAll(list.subList(i + 1, list.size()));
				break;
			}
		}
		return doCalc(list1);
	}

	/**
	 * 运算
	 * @param s1 左边
	 * @param s2 右边
	 * @param symbol 表达式
	 * @return 运算结果
	 */
	private static BigDecimal doTheMath(String s1, String s2, String symbol) {
		BigDecimal result;
		switch (symbol) {
			case ADD:
				result = new BigDecimal(s1).add(new BigDecimal(s2));
				break;
			case MINUS:
				result = new BigDecimal(s1).subtract(new BigDecimal(s2));
				break;
			case TIMES:
				result = new BigDecimal(s1).multiply(new BigDecimal(s2));
				break;
			case DIVISION:
				result = new BigDecimal(s1).divide(new BigDecimal(s2), scale, RoundingMode.HALF_UP);
				break;
			default:
				result = null;
		}
		return result;

	}

	public static BigDecimal cal(String expression) {
		final List<String> list = doMatch(expression);
		final BigDecimal bigDecimal = doCalc(list);
		return bigDecimal.setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal cal(String expression, int scale) {
		final List<String> list = doMatch(expression);
		final BigDecimal bigDecimal = doCalc(list);
		return bigDecimal.setScale(scale, RoundingMode.HALF_UP);
	}

}
