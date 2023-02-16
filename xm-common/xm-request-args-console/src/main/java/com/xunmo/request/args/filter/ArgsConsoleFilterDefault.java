package com.xunmo.request.args.filter;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

@Slf4j
@Component
public class ArgsConsoleFilterDefault implements ArgsConsoleFilterExt {

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (mainHandler != null) {
            final Action action = ctx.action();
            if (action == null) {
                return;
            }
            final MethodWrap methodWrap = action.method();
            final Method method = methodWrap.getMethod();
            if (log.isInfoEnabled()) {
                log.info("Method: {} Args: {}", method.getName(), ctx.paramsMap());
            }
        } else {
            // 处理这种写法
            // Solon.app().get("/XXX", ctx -> {});
        }
        chain.doIntercept(ctx, mainHandler);
    }
}
