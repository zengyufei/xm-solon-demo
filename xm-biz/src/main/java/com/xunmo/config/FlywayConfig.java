package com.xunmo.config;

import cn.hutool.core.util.StrUtil;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Configuration
public class FlywayConfig {

	@Bean
	public void flyway(@Inject DataSource dataSource) {
		final Properties properties = new Properties();
		final Props props = Solon.cfg().getProp("flyway");
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			final Object key = entry.getKey();
			final Object value = entry.getValue();

			final String newKey = StrUtil.toCamelCase(StrUtil.replace((CharSequence) key, "-", "_"));
			properties.put("flyway." + newKey, value);
		}
		final FluentConfiguration fluentConfiguration = new FluentConfiguration();
		fluentConfiguration.configuration(properties);
		CompletableFuture.supplyAsync(() -> fluentConfiguration
						.dataSource(dataSource)
						.load())
				.thenAcceptAsync(Flyway::migrate);
	}

}
