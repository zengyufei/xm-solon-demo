package com.xunmo.request.args.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;

import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
@Component
public class ArgsConsoleHandlerDefault implements ArgsConsoleHandlerExt {

    @Override
    public void before(Context ctx) throws IOException {
        final Action action = ctx.action();
        if (action == null) {
            return;
        }
        final MethodWrap methodWrap = action.method();
        final Method method = methodWrap.getMethod();
        if (log.isInfoEnabled()) {
            final String reqMethod = ctx.method();
            if (StrUtil.equalsIgnoreCase(reqMethod, "post")) {
                log.info("Method: {} Args: {}  Body: {}", method.getName(), ctx.paramsMap(), ctx.body());
            } else {
                log.info("Method: {} Args: {}", method.getName(), ctx.paramsMap());
            }

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
