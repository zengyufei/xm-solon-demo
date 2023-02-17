package com.xunmo;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.dynamicds.DynamicDataSource;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean(value = "db", typed = true) //typed 表示可类型注入 //即默认
    public DataSource db(@Inject("${xm.datasource.db}") DynamicDataSource ds) {
        return ds;
    }


    @Bean(value = "filedb", typed = true) //typed 表示可类型注入 //即默认
    public DataSource getSecondDb(@Inject("${xm.datasource.filedb}") HikariDataSource ds) {
        return ds;
    }


}
