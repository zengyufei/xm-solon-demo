package com.xunmo;

import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.request.trace.filter.TraceIdFilter;
import com.xunmo.request.trace.filter.TraceIdFilterDefault;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.slf4j.TtlMDCAdapter;

@Slf4j
public class XmTraceIdPluginImp implements Plugin {

	final int defaultIndex = 1;

	@Override
	public void start(AopContext context) {
		final Props props = context.cfg();
		props.loadAddIfAbsent(XmPackageNameConstants.XM_REQUEST_TRACE_ID + ".yml");
		// final SolonApp app = Solon.app();

		final boolean isEnabledTraceId = props.getBool(XmPluginPropertiesConstants.xmLogTraceIdEnable, true);
		final boolean isEnabledThreadTraceId = props.getBool(XmPluginPropertiesConstants.xmLogTraceidThreadEnable,
				true);

		if (isEnabledTraceId) {

			ThreadUtil.execute(() -> {
				HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, TraceIdFilter.class,
						TraceIdFilterDefault.class);
				if (isEnabledThreadTraceId) {
					// 设置一个 reqId 到 MDC 类中, 可传给子线程(特殊情况下无法传递);
					TtlMDCAdapter.getInstance();
				}
			});
		}

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_REQUEST_TRACE_ID);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_REQUEST_TRACE_ID + " 包加载完毕!");
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_REQUEST_TRACE_ID);
		}
		else {
			System.out.println(XmPackageNameConstants.XM_REQUEST_TRACE_ID + " 插件关闭!");
		}
	}

}
