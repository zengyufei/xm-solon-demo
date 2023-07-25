package com.xunmo.request.times.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;

@Slf4j
// @Component
public class RequestTimesConsoleHandlerDefault implements RequestTimesConsoleHandler {

	@Override
	public void before(Context ctx) {
		final TimeInterval timer = DateUtil.timer();
		ctx.attrSet("timer", timer);

		if (ctx.action() == null) {
			return;
		}
		final String reqMethod = ctx.method();
		final String path = ctx.pathNew();
		// 1.开始计时（用于计算响应时长）
		if (log.isInfoEnabled()) {
			final Action action = ctx.action();
			final Class<?> clz = action.controller().clz();
			log.info("请求接口: {} {}       定位: {}.{}({}.java:1)", reqMethod, path, clz.getName(),
					action.method().getName(), clz.getSimpleName());
			// log.info("请求接口 {} {}", reqMethod, path);
		}
	}

	@Override
	public void after(Context ctx) {
//		if (ctx.action() == null) {
//			return;
//		}
		final TimeInterval timer = ctx.attr("timer");
		final String path = ctx.pathNew();
		// 2.获得接口响应时长
		if (log.isInfoEnabled()) {
			log.info("完成请求 {} {} 用时：{} 毫秒", ctx.method(), path, timer.intervalMs());
		}
	}

}
