package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Result;
import org.noear.solon.health.HealthChecker;

@Slf4j
public class XmHealthPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_HEALTH + ".yml");
		// final SolonApp app = Solon.app();

		final boolean isEnabled = props.getBool("xm.health.enable", true);

		if (isEnabled) {
			ThreadUtil.execute(() -> {
				HealthChecker.addIndicator("preflight", Result::succeed);
			});
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_HEALTH);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_HEALTH + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_HEALTH);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_HEALTH + " 插件关闭!");
		}
	}

}
