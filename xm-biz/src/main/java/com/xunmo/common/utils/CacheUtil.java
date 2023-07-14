package com.xunmo.common.utils;

import cn.hutool.cache.impl.TimedCache;

import java.util.List;

public class CacheUtil {

	public static final TimedCache<String, List<String>> timedCache = new TimedCache<>(30 * 1000);
	static {
		// 启动定时任务，每5秒检查一次过期
		timedCache.schedulePrune(5 * 1000);
	}

}
