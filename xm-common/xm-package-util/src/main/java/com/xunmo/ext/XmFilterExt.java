package com.xunmo.ext;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;

public interface XmFilterExt {

	void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable;

}
