package com.xunmo.config.jimmer;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

@Component
public class TenantProvider {

	public String get() {
		final Context current = Context.current();
		if (current != null) {
			String tenant = current.header("tenant");
			return "".equals(tenant) ? null : tenant;
		}
		return null;
	}
}
