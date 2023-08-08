package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.JimmerAdapterFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配管理器
 *
 * @author noear
 * @since 1.1
 */
public class JimmerAdapterManager {

	
	private static List<BeanWrap> dsWraps = new ArrayList<>();

	private static JimmerAdapterFactory adapterFactory = new JimmerAdapterFactoryDefault();

	/**
	 * 缓存适配器
	 */
	private static Map<String, JimmerAdapter> dbMap = new ConcurrentHashMap<>();

	public static JimmerAdapter getOnly(String name) {
		return dbMap.get(name);
	}

	/**
	 * 获取适配器
	 */
	public synchronized static JimmerAdapter get(BeanWrap bw) {
		final String named = bw.name();
		JimmerAdapter db = dbMap.get(named);

		if (db == null) {
			synchronized (named.intern()) {
				db = dbMap.get(named);
				if (db == null) {
					db = buildAdapter(bw);

					dbMap.put(named, db);

					if (bw.typed()) {
						dbMap.put("", db);
					}
				}
			}

		}

		return db;
	}

	/**
	 * 注册数据源，并生成适配器
	 * @param bw 数据源的BW
	 */
	public static void add(BeanWrap bw) {
		dsWraps.add(bw);
	}

	/**
	 * 注册数据源，并生成适配器
	 * @param bw 数据源的BW
	 */
	public static void register(BeanWrap bw) {
		get(bw);
	}

	/**
	 * 注册数据源，并生成适配器
	 */
	public static void register() {
		for (BeanWrap dsWrap : dsWraps) {
			get(dsWrap);
		}
	}

	/**
	 * 构建适配器
	 */
	private static synchronized JimmerAdapter buildAdapter(BeanWrap bw) {
		JimmerAdapter adapter;
		if (Utils.isEmpty(bw.name())) {
			adapter = adapterFactory.create(bw);
		} else {
			adapter = adapterFactory.create(bw, Solon.cfg().getProp("jimmer." + bw.name()));
		}

		Solon.context().wrapAndPut(JimmerAdapter.class, adapter);
		return adapter;
	}

}
