package com.xunmo.utils;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 提供事务后执行方法, TranAfterUtil 工具类;
 *
 * @author zengyufei
 * @date 2023/02/01
 */
public class TranAfterUtil {

	public interface AfterFunction {

		void test();

	}

	final static private ThreadLocal<List<AfterFunction>> AFTER_METHODS = new ThreadLocal<>();

	final static private ThreadLocal<List<AfterFunction>> AFTER_SYNC_METHODS = new ThreadLocal<>();

	public static void execute(AfterFunction supplier) {
		List<AfterFunction> consumers = AFTER_METHODS.get();
		if (CollUtil.isEmpty(consumers)) {
			consumers = new ArrayList<>();
			AFTER_METHODS.set(consumers);
		}
		consumers.add(supplier);
	}

	public static void executeSync(AfterFunction supplier) {
		List<AfterFunction> consumers = AFTER_SYNC_METHODS.get();
		if (CollUtil.isEmpty(consumers)) {
			consumers = new ArrayList<>();
			AFTER_SYNC_METHODS.set(consumers);
		}
		consumers.add(supplier);
	}

	public static List<AfterFunction> getAfterMethods() {
		final List<AfterFunction> consumers = AFTER_METHODS.get();
		if (CollUtil.isEmpty(consumers)) {
			return Collections.emptyList();
		}
		return consumers;
	}

	public static List<AfterFunction> getAfterSyncMethods() {
		final List<AfterFunction> consumers = AFTER_SYNC_METHODS.get();
		if (CollUtil.isEmpty(consumers)) {
			return Collections.emptyList();
		}
		return consumers;
	}

	public static void clear() {
		AFTER_METHODS.remove();
		AFTER_SYNC_METHODS.remove();
	}

}
