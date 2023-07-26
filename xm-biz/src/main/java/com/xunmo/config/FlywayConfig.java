package com.xunmo.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

@Configuration
public class FlywayConfig {

	@Bean
	public void flyway(@Inject DataSource dataSource) {
		final Props props = Solon.cfg().getProp("flyway");
		final FluentConfiguration fluentConfiguration = new FluentConfiguration();
		CompletableFuture.supplyAsync(() -> fluentConfiguration
						.locations(props.get("locations"))
						.dataSource(dataSource)
						.baselineOnMigrate(true)
						.load())
				.thenAcceptAsync(Flyway::migrate);
	}

}
