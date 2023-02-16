package com.xunmo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.dynamicds.DynamicDataSource;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean(value = "db", typed = true)
    public DataSource db(@Inject("${test.db}") DynamicDataSource ds) {
        return ds;
    }

}
