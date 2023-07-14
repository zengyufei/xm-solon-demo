package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.request.times.filter.RequestTimesConsoleFilter;
import com.xunmo.request.times.filter.RequestTimesFilterDefault;
import com.xunmo.request.times.handler.RequestTimesConsoleHandler;
import com.xunmo.request.times.handler.RequestTimesConsoleHandlerDefault;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmRequestTimesConsolePluginImp implements Plugin {

	final int defaultIndex = 100;

	final String defaultType = "handler";

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_REQUEST_TIMES_CONSOLE + ".yml");

		final boolean isEnabled = props.getBool(XmPluginPropertiesConstants.xmWebConsoleRequestTimesEnable, true);
		// type = handler(default) or filter
		final String type = props.get(XmPluginPropertiesConstants.xmWebConsoleRequestTimesType, defaultType);

		if (isEnabled) {
			ThreadUtil.execute(() -> {
				if (StrUtil.equalsIgnoreCase(type, defaultType)) {
					HandlerExtUtil.toBuildExtRequestHandler(context, defaultIndex, RequestTimesConsoleHandler.class,
							RequestTimesConsoleHandlerDefault.class);
				}
				else {
					HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, RequestTimesConsoleFilter.class,
							RequestTimesFilterDefault.class);
				}
			});
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_REQUEST_TIMES_CONSOLE);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_REQUEST_TIMES_CONSOLE + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_REQUEST_TIMES_CONSOLE);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_REQUEST_TIMES_CONSOLE + " 插件关闭!");
		}
	}

}
