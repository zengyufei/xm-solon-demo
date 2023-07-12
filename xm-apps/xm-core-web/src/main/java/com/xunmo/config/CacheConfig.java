//package com.xunmo.config;
//
//import org.noear.solon.annotation.Bean;
//import org.noear.solon.annotation.Configuration;
//import org.noear.solon.annotation.Inject;
//import org.noear.solon.cache.redisson.RedissonCacheService;
//import org.noear.solon.data.cache.CacheService;
//
//@Configuration
//public class CacheConfig {
//
//    @Bean(typed = true) //typed 表示可类型注入 //即默认
//    public CacheService cache(@Inject("${xm.web.cache}") RedissonCacheService cache) {
//        return cache;
//    }
//}
