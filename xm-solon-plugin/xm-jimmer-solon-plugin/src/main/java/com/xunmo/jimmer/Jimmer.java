package com.xunmo.jimmer;

import com.xunmo.jimmer.integration.JimmerAdapterManager;

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
	static JimmerAdapter use(String name) {
		return JimmerAdapterManager.getOnly(name);
	}

}
