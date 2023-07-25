package com.xunmo.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

@Configuration
public class FlywayConfig {

	@Bean
	public void flyway(@Inject DataSource dataSource) {
		final FluentConfiguration fluentConfiguration = Solon.cfg().getBean("flyway", FluentConfiguration.class);
		CompletableFuture.supplyAsync(() -> fluentConfiguration
						.dataSource(dataSource)
						.load())
				.thenAcceptAsync(Flyway::migrate);
	}

}
