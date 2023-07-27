package com.xunmo.test;

import cn.hutool.json.JSONUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DbConfig {

	@Bean(value = "db", typed = true) // typed 表示可类型注入 //即默认
	public DataSource db(@Inject("${test.h2}") HikariDataSource ds) {
		try (final Connection connection = ds.getConnection()) {
			connection.prepareStatement(ResourceUtil.getResourceAsString("init.sql")).execute();
			connection.prepareStatement(ResourceUtil.getResourceAsString("data.sql")).execute();

			final PreparedStatement preparedStatement = connection.prepareStatement("select * from organization");
			final ResultSetMetaData metaData = preparedStatement.getMetaData();
			final int columnCount = metaData.getColumnCount();
			final ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				List<Object> row = new ArrayList<>();
				for (int i = 1; i <= columnCount; i++) {
					final Object object = resultSet.getObject(i);
					row.add(object);
				}
				System.out.println(JSONUtil.toJsonStr(row));
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ds;
	}

}
