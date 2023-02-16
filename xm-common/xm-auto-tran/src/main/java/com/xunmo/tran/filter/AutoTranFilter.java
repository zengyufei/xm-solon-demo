package com.xunmo.tran.filter;

import com.xunmo.utils.TranAfterUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

@Component(index = 1)
public class AutoTranFilter implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (mainHandler != null) {
            try {
                chain.doIntercept(ctx, mainHandler);
            } finally {
                // 拦截器拦截粒度太小，在这里完整业务清除
                TranAfterUtil.clear();
            }
        } else {
            // 处理这种写法
            // Solon.app().get("/XXX", ctx -> {});
            chain.doIntercept(ctx, mainHandler);
        }

    }
}
