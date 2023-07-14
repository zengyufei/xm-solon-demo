package com.xunmo.annotations;

import org.javers.core.Changes;

@FunctionalInterface
public interface AuditFunc {

	/**
	 * 审计
	 * @param changes 变化
	 */
	void audit(Changes changes);

}
