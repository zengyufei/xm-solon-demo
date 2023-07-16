package com.xunmo;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HealthAppLoadEndEventListener implements EventListener<AppLoadEndEvent> {

	@Override
	public void onEvent(AppLoadEndEvent event) throws Throwable {

		ThreadUtil.execute(() -> {
			final SolonProps cfg = Solon.cfg();
			final String appName = cfg.appName();
			final String env = cfg.env();
			final String port = cfg.get("server.port");

			List<String> ipList = getIps();

			final StringBuilder stringBuilder = new StringBuilder("\t健康检查: --\n");
			for (String ip : ipList) {
				stringBuilder.append("\t\t- 访问: http://").append(ip).append(":").append(port).append("/healthz\n");
			}
			System.out.println(stringBuilder);
		});
	}

	private static List<String> getIps() {
		List<String> ipList = new ArrayList<>();
		for (String localIpv4 : NetUtil.localIpv4s()) {
			ipList.add(localIpv4);
		}
		return ipList;
	}

}
