package com.xunmo;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmP6spyPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_P6SPY + ".yml");

//        final SolonApp app = Solon.app();
        
//        EventBus.subscribe(MybatisConfiguration.class, cfg -> {
//            cfg.setLogImpl(Slf4jImpl.class);
////            cfg.setLogImpl(NoLoggingImpl.class);
////            cfg.setLogImpl(XmMybatisStdOutImpl.class);
//        });

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_P6SPY);
        }else {
            System.out.println(XmPackageConstants.XM_P6SPY + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_P6SPY);
        }else {
            System.out.println(XmPackageConstants.XM_P6SPY + " 插件关闭!");
        }
    }
}
