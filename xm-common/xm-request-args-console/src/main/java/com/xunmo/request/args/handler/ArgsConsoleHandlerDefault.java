package com.xunmo.request.args.handler;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;

@Slf4j
@Component
public class ArgsConsoleHandlerDefault implements ArgsConsoleHandlerExt {

    @Override
    public void before(Context ctx) {
        final Action action = ctx.action();
        if (action == null) {
            return;
        }
        final MethodWrap methodWrap = action.method();
        final Method method = methodWrap.getMethod();
        if (log.isInfoEnabled()) {
            log.info("Method: {} Args: {}", method.getName(), ctx.paramsMap());
        }
    }

    @Override
    public void after(Context ctx) {
    }

    @Override
    public boolean isOpenAfter() {
        return false;
    }
}
