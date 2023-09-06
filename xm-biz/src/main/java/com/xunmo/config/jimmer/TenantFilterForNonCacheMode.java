package com.xunmo.config.jimmer;

import com.xunmo.common.base.TenantEntityProps;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.filter.FilterArgs;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/*
 * see JSqlClient.Builder.addFilters
 *
 * This bean is only be used when cache is NOT used.
 */
@Component
public class TenantFilterForNonCacheMode implements Filter<TenantEntityProps> {

	@Inject
	protected TenantProvider tenantProvider;

	@Override
	public void filter(FilterArgs<TenantEntityProps> args) {
		String tenant = tenantProvider.get();
		if (tenant != null) {
			args.where(args.getTable().tenantId().eq(tenant));
		}
	}

}
