package com.xunmo.request.times.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;

@Slf4j
//@Component
public class RequestTimesFilterDefault implements RequestTimesConsoleFilter {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (mainHandler != null) {
            final String path = ctx.pathNew();
            if (log.isInfoEnabled()) {
                log.info("请求接口 {} {}", ctx.method(), path);
            }
            //1.开始计时（用于计算响应时长）
            final TimeInterval timer = DateUtil.timer();
            chain.doIntercept(ctx, mainHandler);
            //2.获得接口响应时长
            if (log.isInfoEnabled()) {
                log.info("完成请求 {} {} 用时：{} 毫秒", ctx.method(), path, timer.intervalMs());
            }
        } else {
            // 处理这种写法
            // Solon.app().get("/XXX", ctx -> {});
            chain.doIntercept(ctx, mainHandler);
        }
    }
}
