package com.xunmo.config;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceSupplier;

@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheService getRedisCacheService(@Inject(value = "${xm.web.cache.enable}", required = false) boolean isCache) {
        if (isCache) {
            return Solon.cfg().getBean("xm.web.cache", RedisCacheService.class);
        } else {
            return null;
        }
    }

    @Bean
    public CacheService cache(@Inject(value="${xm.web.cache.enable}", required = false) boolean isCache,
                              @Inject("${xm.web.cache}") CacheServiceSupplier supplier) {
        if (isCache) {
            return supplier.get();
        } else {
            return null;
        }
    }
}
