package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.JimmerAdapterFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配管理器
 *
 * @author noear
 * @since 1.1
 */
public class JimmerAdapterManager {

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
	public static JimmerAdapter get(BeanWrap bw) {
		JimmerAdapter db = dbMap.get(bw.name());

		if (db == null) {
			synchronized (bw.name().intern()) {
				db = dbMap.get(bw.name());
				if (db == null) {
					db = buildAdapter(bw);

					dbMap.put(bw.name(), db);

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
	public static void register(BeanWrap bw) {
		get(bw);
	}

	/**
	 * 构建适配器
	 */
	private static JimmerAdapter buildAdapter(BeanWrap bw) {
		JimmerAdapter adapter;
		if (Utils.isEmpty(bw.name())) {
			adapter = adapterFactory.create(bw);
		}
		else {
			adapter = adapterFactory.create(bw, Solon.cfg().getProp("jimmer." + bw.name()));
		}

		return adapter;
	}

}
