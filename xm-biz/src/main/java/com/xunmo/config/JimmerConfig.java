package com.xunmo.config;

import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.Executor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;

import javax.sql.DataSource;

@Configuration
public class JimmerConfig {

    @Bean
    public void jsonInit(@Inject JacksonRenderFactory factory) {
        factory.config().registerModule(new ImmutableModule());
    }

    @Bean
    public JSqlClient sqlClient(@Inject DataSource dataSource) {
        return JSqlClient
                .newBuilder()
                .setConnectionManager(
                        ConnectionManager
                                .simpleConnectionManager(dataSource)
                )
                .setExecutor(Executor.log())
                .setDefaultBatchSize(256)
                .setDefaultListBatchSize(32)
                .build();
    }

}
