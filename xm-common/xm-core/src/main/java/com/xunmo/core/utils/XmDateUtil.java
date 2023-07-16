package com.xunmo.core.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.function.Consumer;

public class XmDateUtil {

	private static final String[] DATE_FORMAT_STRS = new String[] { "yyyy-M-dd", "yyyy/M/dd", "yyyy.M.dd",

			"yyyy-M-d", "yyyy/M/d", "yyyy.M.d",

			"yyyy-MM-d", "yyyy/MM/d", "yyyy.MM.d",

			"yyyy年M月dd日", "yyyy年M月d日", "yyyy年MM月d日",

			"yyyyMdd", "yyyyMd", "yyyyMMd",

			"yyyy-M", "yyyy/M", "yyyy.M",

			"yyyy-M", "yyyy/M", "yyyy.M",

			"yyyy-MM", "yyyy/MM", "yyyy.MM",

			"yyyy年M月", "yyyy年M月", "yyyy年MM月",

			"yyyyM", "yyyyM", "yyyyMM", };

	public static String formatToDateStr(String parameter) {
		try {
			final DateTime parse = DateUtil.parse(parameter);
			parameter = parse.toDateStr();
		}
		catch (Exception ignored) {
			for (String dateFormatStr : DATE_FORMAT_STRS) {
				try {
					final DateTime parse = DateUtil.parse(parameter, dateFormatStr);
					parameter = parse.toDateStr();
					break;
				}
				catch (Exception ignored2) {
				}
			}
		}
		return parameter;
	}

	public static DateTime checkDateStr(String parameter, Consumer<String> errorMsg) {
		DateTime parse = null;
		try {
			parse = DateUtil.parse(parameter);
		}
		catch (Exception ignored) {
			for (String dateFormatStr : DATE_FORMAT_STRS) {
				try {
					parse = DateUtil.parse(parameter, dateFormatStr);
				}
				catch (Exception ignored2) {
				}
			}
		}
		if (parse == null) {
			errorMsg.accept(parameter);
		}
		return null;
	}

}
