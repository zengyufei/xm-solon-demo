package com.xunmo.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志输出工具类
 */
@Slf4j
public class XmLog {

	public static void info(String str) {
		if (log.isInfoEnabled()) {
			log.info(str);
		}
	}

	public static void debug(String str) {
		if (log.isDebugEnabled()) {
			log.debug(str);
		}
	}

}
