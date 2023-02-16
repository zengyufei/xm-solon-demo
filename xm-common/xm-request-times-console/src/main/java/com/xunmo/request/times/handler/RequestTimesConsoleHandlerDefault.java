package com.xunmo.request.times.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

@Slf4j
@Component
public class RequestTimesConsoleHandlerDefault implements RequestTimesConsoleHandlerExt {

    @Override
    public void before(Context ctx) {
        final TimeInterval timer = DateUtil.timer();
        final String method = ctx.method();
        ctx.attrSet("timer", timer);

        final String path = ctx.pathNew();
        //1.开始计时（用于计算响应时长）
        if (log.isInfoEnabled()) {
            log.info("请求接口 {} {}", method, path);
        }
    }

    @Override
    public void after(Context ctx) {
        final TimeInterval timer = ctx.attr("timer");
        final String path = ctx.pathNew();
        //2.获得接口响应时长
        if (log.isInfoEnabled()) {
            log.info("完成请求 {} {} 用时：{} 毫秒", ctx.method(), path, timer.intervalMs());
        }
    }

}
