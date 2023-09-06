package com.xunmo.config.jimmer;

import com.xunmo.common.base.TenantEntityProps;
import org.babyfish.jimmer.sql.event.EntityEvent;
import org.babyfish.jimmer.sql.filter.CacheableFilter;
import org.noear.solon.annotation.Component;

import java.util.SortedMap;
import java.util.TreeMap;

// -----------------------------
// If you are a beginner, please ignore this class
// and view its super class `TenantFilterForNonCacheMode`,
// for non-cache mode, this class will never be used.
//
// When the global filter generates additional query
// conditions for `Book`, different users will see different data.
//
// In this case, all associated properties and calculated properties
// related to `Book` will be affected, such as `BookStore.books`,
// `Author.books`, `BookStore.avgPrice`, `BookStore.newestBooks`.
//
// Even when running the program with caching enabled, all affected
// properties will not use the cache, unless multi-view caching is enabled
// for those affected properties.
//
// This class cooperates with `RedisHashBinder` in `CacheConfig`
// to support multi-view caching together.
// -----------------------------
@Component
public class TenantFilterForCacheMode extends TenantFilterForNonCacheMode
		implements CacheableFilter<TenantEntityProps> {

	@Override
	public SortedMap<String, Object> getParameters() {
		String tenant = tenantProvider.get();
		if (tenant == null) {
			return null;
		}
		SortedMap<String, Object> map = new TreeMap<>();
		map.put("tenant", tenant);
		return map;
	}

	@Override
	public boolean isAffectedBy(EntityEvent<?> e) {
		return e.isChanged(TenantEntityProps.TENANT_ID);
	}

}
