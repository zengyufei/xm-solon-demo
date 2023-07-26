package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class XmFlywayPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_FLYWAY + ".yml");
		// final SolonApp app = Solon.app();

		final boolean isEnabled = props.getBool("flyway.enable", true);

		if (isEnabled) {
			context.getBeanAsync(DataSource.class, dataSource -> {
				ThreadUtil.execute(() -> {
					final Properties properties = new Properties();
					final Props flayWayProps = Solon.cfg().getProp("flyway");
					for (Map.Entry<Object, Object> entry : flayWayProps.entrySet()) {
						final String key = (String) entry.getKey();
						final Object value = entry.getValue();

						if (StrUtil.equalsIgnoreCase(key, "enable")) {
							continue;
						}

						final String newKey = StrUtil.toCamelCase(StrUtil.replace(key, "-", "_"));
						properties.put("flyway." + newKey, value);
					}
					final FluentConfiguration fluentConfiguration = new FluentConfiguration();
					fluentConfiguration.configuration(properties);
					final Flyway flyway = fluentConfiguration
							.dataSource(dataSource)
							.load();
					flyway.migrate();
				});
			});
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_FLYWAY);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_FLYWAY + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_FLYWAY);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_FLYWAY + " 插件关闭!");
		}
	}

}
