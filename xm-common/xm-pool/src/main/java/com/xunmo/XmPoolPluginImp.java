package com.xunmo;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmPoolPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_POOL + ".yml");
//        final SolonApp app = Solon.app();

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_POOL);
        } else {
            System.out.println(XmPackageConstants.XM_POOL + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_POOL);
        } else {
            System.out.println(XmPackageConstants.XM_POOL + " 插件关闭!");
        }
    }


}
