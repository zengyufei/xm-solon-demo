package com.xunmo.request.trace.filter;

import cn.hutool.core.util.IdUtil;
import com.xunmo.XmConstants;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.slf4j.MDC;

@Slf4j
// @Component
public class TraceIdFilterDefault implements TraceIdFilter {

	public static void removeMdc() {
		MDC.remove(XmConstants.REQ_ID);
	}

	public static void putMdc(String reqId) {
		MDC.put(XmConstants.REQ_ID, reqId);
	}

	public static void putMdc() {
		MDC.put(XmConstants.REQ_ID, getReqId());
	}

	public static String getReqId() {
		return IdUtil.fastSimpleUUID();
	}

	@Override
	public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
		// if (mainHandler != null) {
		// } else {
		// // 处理这种写法
		// // Solon.app().get("/XXX", ctx -> {});
		// }
		final String reqId = getReqId();

		// 在 param/attr/header 均能获取到一个 "reqId" 的 uuid 值;
		ctx.paramSet(XmConstants.REQ_ID, reqId);
		ctx.attrSet(XmConstants.REQ_ID, reqId);
		ctx.headerAdd(XmConstants.REQ_ID, reqId);

		putMdc(reqId);
		try {
			chain.doIntercept(ctx, mainHandler);
		}
		finally {
			removeMdc();
		}
	}

}
