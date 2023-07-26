package com.xunmo;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmStopPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_STOP + ".yml");
		// final SolonApp app = Solon.app();

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_STOP);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_STOP + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_STOP);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_STOP + " 插件关闭!");
		}
	}

}
