package com.xunmo;

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
        props.loadAddIfAbsent(XmPackageConstants.XM_HEALTH + ".yml");
//            final SolonApp app = Solon.app();

        final boolean isEnabled = props.getBool("xm.health.enable", true);

        if (isEnabled) {
            HealthChecker.addIndicator("preflight", Result::succeed);
        }

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_HEALTH);
        }else {
            System.out.println(XmPackageConstants.XM_HEALTH + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_HEALTH);
        }else {
            System.out.println(XmPackageConstants.XM_HEALTH + " 插件关闭!");
        }
    }
}
