package com.xunmo.config;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

@Slf4j
@Component
public class AppLoadEndEventListener implements EventListener<AppLoadEndEvent> {
//    @Inject("${solon.env}")
//    private String env;

    @Override
    public void onEvent(AppLoadEndEvent event) throws Throwable {
        //Solon.app(); //获取应用对象

        log.info("启动完毕...");
//        if (StrUtil.isNotBlank(env)) {
//            log.info("当前启动环境: "+ env);
//        }
        //Solon.cfg().argx();  //获取应用启动参数
    }
}
