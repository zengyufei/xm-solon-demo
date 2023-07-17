//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.utils;

import com.xunmo.config.RedissonCodec;
import org.noear.solon.Utils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Properties;

public class XmRedissonBuilder {
	public XmRedissonBuilder() {
	}

	public static RedissonClient build(Properties prop, RedissonCodec redissonCodec) {
		String server_str = prop.getProperty("server");
		String db_str = prop.getProperty("db");
		String user_str = prop.getProperty("user");
		String password_str = prop.getProperty("password");
		int db = 0;
		if (Utils.isNotEmpty(db_str)) {
			db = Integer.parseInt(db_str);
		}

		Config config = new Config();
		config.setCodec(redissonCodec);
		String[] address;
		if (server_str.contains(",")) {
			ClusterServersConfig serverConfig = config.useClusterServers();
			Utils.injectProperties(serverConfig, prop);
			address = resolveServers(server_str.split(","));
			serverConfig.addNodeAddress(address).setUsername(user_str).setPassword(password_str);
		} else {
			SingleServerConfig serverConfig = config.useSingleServer();
			Utils.injectProperties(serverConfig, prop);
			address = resolveServers(server_str);
			serverConfig.setAddress(address[0]).setUsername(user_str).setPassword(password_str).setDatabase(db);
		}

		return Redisson.create(config);
	}

	private static String[] resolveServers(String... servers) {
		String[] uris = new String[servers.length];

		for (int i = 0; i < servers.length; ++i) {
			String sev = servers[i];
			if (sev.contains("://")) {
				uris[i] = sev;
			} else {
				uris[i] = "redis://" + sev;
			}
		}

		return uris;
	}
}
