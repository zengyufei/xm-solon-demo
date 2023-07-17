package com.xunmo.jimmer;

import com.xunmo.jimmer.integration.JimmerAdapterManager;
import org.babyfish.jimmer.sql.JSqlClient;

/**
 * Jimmer 手动使用接口
 *
 * @author
 * @since 1.10
 */
public interface Jimmer {
	/**
	 * 获取源
	 */
	static JSqlClient use(String name) {
		return JimmerAdapterManager.getOnly(name);
	}
}
