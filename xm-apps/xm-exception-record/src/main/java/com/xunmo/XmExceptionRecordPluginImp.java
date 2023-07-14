package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.mq.exceptionRecord.MqConsumerService;
import com.xunmo.mq.exceptionRecord.MqSendService;
import com.xunmo.utils.MqHelper;
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
		props.loadAddIfAbsent(XmPackageNameConstants.XM_EXCEPTION_RECORD + ".yml");

		final boolean isExceptionEnable = props.getBool("xm.exception.enable", true);

		if (isExceptionEnable) {
			ThreadUtil.execute(() -> {
				MqHelper.initFromSolon();

				MqSendService mqSendService = new MqSendService();
				mqSendService.init();
				// 可以进行手动字段注入
				context.beanInject(mqSendService);
				// 生成普通的Bean（只是注册，不会做别的处理；身上的注解会被乎略掉）
				Solon.context().wrapAndPut(MqSendService.class, mqSendService);

				final MqConsumerService mqConsumerService = new MqConsumerService();
				mqConsumerService.init();
				// 可以进行手动字段注入
				context.beanInject(mqConsumerService);
				// 生成普通的Bean（只是注册，不会做别的处理；身上的注解会被乎略掉）
				Solon.context().wrapAndPut(MqConsumerService.class, mqConsumerService);

				EventBus.subscribe(AppLoadEndEvent.class, mqConsumerService);
			});
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_EXCEPTION_RECORD);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_EXCEPTION_RECORD + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_EXCEPTION_RECORD);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_EXCEPTION_RECORD + " 插件关闭!");
		}
	}

}
