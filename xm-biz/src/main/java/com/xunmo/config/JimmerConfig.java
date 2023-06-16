package com.xunmo.config;

import com.xunmo.webs.Gender;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.Executor;
import org.babyfish.jimmer.sql.runtime.ScalarProvider;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class JimmerConfig {


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
                .addScalarProvider(
                        ScalarProvider.enumProviderByString(Gender.class, it -> {
                            it.map(Gender.MALE, "M");
                            it.map(Gender.FEMALE, "F");
                        })
                )
                .build();
    }

}
