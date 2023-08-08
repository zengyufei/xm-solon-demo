package com.xunmo.config.jimmer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.jimmer.cache.CaffeineBinder;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.cache.AbstractCacheFactory;
import org.babyfish.jimmer.sql.cache.Cache;
import org.babyfish.jimmer.sql.cache.CacheFactory;
import org.babyfish.jimmer.sql.cache.chain.ChainCacheBuilder;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.time.Duration;
import java.util.List;


@Configuration
public class CacheConfig {

	@Bean(typed = true)
	public CacheFactory cacheFactory(
			@Inject ObjectMapper objectMapper
	) {
		return new AbstractCacheFactory() {

			// Id -> Object
			@Override
			public Cache<?, ?> createObjectCache(ImmutableType type) {
				return new ChainCacheBuilder<>()
						.add(new CaffeineBinder<>(512, Duration.ofSeconds(10)))
						.build();
			}

			// Id -> TargetId, for one-to-one/many-to-one
			@Override
			public Cache<?, ?> createAssociatedIdCache(ImmutableProp prop) {
				return createPropCache(
						getFilterState().isAffected(prop.getTargetType()),
						prop,
						objectMapper,
						Duration.ofMinutes(5)
				);
			}

			// Id -> TargetId list, for one-to-many/many-to-many
			@Override
			public Cache<?, List<?>> createAssociatedIdListCache(ImmutableProp prop) {
				return createPropCache(
						getFilterState().isAffected(prop.getTargetType()),
						prop,
						objectMapper,
						Duration.ofMinutes(5)
				);
			}

			// Id -> computed value, for transient properties with resolver
			@Override
			public Cache<?, ?> createResolverCache(ImmutableProp prop) {
				return createPropCache(
						getFilterState().isAffected(prop.getTargetType()),
						prop,
						objectMapper,
						Duration.ofHours(1)
				);
			}
		};
	}

	private static <K, V> Cache<K, V> createPropCache(
			boolean isMultiView,
			ImmutableProp prop,
			ObjectMapper objectMapper,
			Duration redisDuration
	) {
		return new ChainCacheBuilder<K, V>()
				.add(new CaffeineBinder<>(512, Duration.ofSeconds(10)))
				.build();
	}
}
