package com.xunmo;

import com.xunmo.mq.exceptionRecord.MqConsumerService;
import com.xunmo.mq.exceptionRecord.MqSendService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;

@Slf4j
public class XmExceptionRecordPluginImp implements Plugin {

    @Override
    public void start(AopContext context) throws Throwable {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_EXCEPTION_RECORD + ".yml");

        final boolean isEnable = props.getBool("xm.exception.enable", true);

        if (isEnable) {
//            final SolonApp app = Solon.app();
            MqSendService mqSendService = new MqSendService();
            mqSendService.init();
            //可以进行手动字段注入
            context.beanInject(mqSendService);

            //生成普通的Bean（只是注册，不会做别的处理；身上的注解会被乎略掉）
//            context.wrapAndPut(UserService.class, new UserServiceImpl());

            //生成Bean，并触发身上的注解处理（比如类上有 @Controller 注解；则会执行 @Controller 对应的处理）
//            context.beanMake(UserServiceImpl.class);

            final MqConsumerService mqConsumerService = new MqConsumerService();
            mqConsumerService.init();
            //可以进行手动字段注入
            context.beanInject(mqConsumerService);

            EventBus.subscribe(AppLoadEndEvent.class, mqConsumerService);
        }

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_EXCEPTION_RECORD);
        }else {
            System.out.println(XmPackageConstants.XM_EXCEPTION_RECORD + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_EXCEPTION_RECORD);
        }else {
            System.out.println(XmPackageConstants.XM_EXCEPTION_RECORD + " 插件关闭!");
        }
    }

}
