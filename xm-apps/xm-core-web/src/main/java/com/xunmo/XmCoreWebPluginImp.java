package com.xunmo;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class XmCoreWebPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_CORE_WEB + ".yml");
//        final SolonApp app = Solon.app();


        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_CORE_WEB);
        }else {
            System.out.println(XmPackageConstants.XM_CORE_WEB + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_CORE_WEB);
        }else {
            System.out.println(XmPackageConstants.XM_CORE_WEB + " 插件关闭!");
        }
    }

    //初始化json定制（需要在插件运行前定制）
//    private static void initMvcJsonCustom(SnackRenderFactory factory) {
//        //示例1：通过转换器，做简单类型的定制
//        factory.addConvertor(Date.class, s -> DateUtil.date(s).toString());
//        factory.addConvertor(LocalDateTime.class, s -> DateUtil.date(s).toString());
//        factory.addConvertor(LocalDate.class, s -> DateUtil.date(s).toString("yyyy-MM-dd"));
////        factory.addConvertor(Double.class, String::valueOf);
//    }
}
