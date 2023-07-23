package com.xunmo.jimmer;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

	@Bean(value = "mydb", typed = true) // typed 表示可类型注入 //即默认
	public DataSource mydb(@Inject("${xm.db}") HikariDataSource dataSource) {
		return dataSource;
	}

}
