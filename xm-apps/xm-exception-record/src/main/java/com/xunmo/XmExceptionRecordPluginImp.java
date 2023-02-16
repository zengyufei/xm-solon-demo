package com.xunmo;

import com.xunmo.mq.exceptionRecord.ConsumerExceptionRecord;
import com.xunmo.mq.exceptionRecord.SenderExceptionRecord;
import lombok.extern.slf4j.Slf4j;
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
            SenderExceptionRecord senderExceptionRecord = new SenderExceptionRecord();
            //可以进行手动字段注入
            context.beanInject(senderExceptionRecord);

            final ConsumerExceptionRecord consumerExceptionRecord = new ConsumerExceptionRecord();
            //可以进行手动字段注入
            context.beanInject(consumerExceptionRecord);

            EventBus.subscribe(AppLoadEndEvent.class, consumerExceptionRecord);
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
