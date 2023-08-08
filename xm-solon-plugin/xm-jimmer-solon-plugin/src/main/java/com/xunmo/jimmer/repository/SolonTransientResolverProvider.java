package com.xunmo.jimmer.repository;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.TransientResolver;
import org.babyfish.jimmer.sql.runtime.TransientResolverProvider;
import org.noear.solon.core.AopContext;

public final class SolonTransientResolverProvider implements TransientResolverProvider {

	private final AopContext ctx;

	public SolonTransientResolverProvider(AopContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public TransientResolver<?, ?> get(Class<TransientResolver<?, ?>> resolverType, JSqlClient sqlClient)
			throws Exception {
		return ctx.getBean(resolverType);
	}

	@Override
	public TransientResolver<?, ?> get(String ref, JSqlClient sqlClient) throws Exception {
		return ctx.getBean(ref);
	}

}
