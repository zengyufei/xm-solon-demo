package com.xunmo;


import com.xunmo.annotations.AutoTran;
import com.xunmo.config.TranAfterInterceptor;
import com.xunmo.config.TranAopInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.data.annotation.Tran;


@Slf4j
public class XmAutoTranPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageNameConstants.XM_AUTO_TRAN + ".yml");
//        final SolonApp app = Solon.app();

        // 自动给符合的方法名自动进入事务;
        if (props.getBool(XmPluginPropertiesConstants.xmTranEnable, false)) {
            Solon.context().beanAroundAdd(AutoTran.class, new TranAopInterceptor());
        }
        // 提供事务后执行方法, TranAfterUtil 工具类;
        Solon.context().beanAroundAdd(Tran.class, new TranAfterInterceptor());
//        Solon.context().beanAroundAdd(TranAnno.class, new TranAfterInterceptor());

        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageNameConstants.XM_AUTO_TRAN);
        } else {
            System.out.println(XmPackageNameConstants.XM_AUTO_TRAN + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageNameConstants.XM_AUTO_TRAN);
        } else {
            System.out.println(XmPackageNameConstants.XM_AUTO_TRAN + " 插件关闭!");
        }
    }
}
