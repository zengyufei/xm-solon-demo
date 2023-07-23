package com.xunmo.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xunmo.XmConstants;
import com.xunmo.annotations.ExceptionHandler;
import com.xunmo.common.CustomException;
import com.xunmo.common.ISystemStatus;
import com.xunmo.mq.exceptionRecord.MqSendService;
import com.xunmo.utils.AjaxJson;
import com.xunmo.webs.exceptionRecord.input.ExceptionRecordInput;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 统一异常处理器
 *
 * @author zengyufei
 * @date 2022/12/14
 */
@Slf4j
@Component(index = 0)
public class XmGlobalException {

	// 是否开启异常记录
	@Inject("${xm.exception.enable:false}")
	private Boolean enable;

	// 是否开启异常记录
	@Inject
	private MqSendService mqSendService;

	@ExceptionHandler(CustomException.class)
	public AjaxJson handlerCustomException(Context ctx, CustomException e) {
		log.error("", e);

		// 打印请求和异常信息
		printRequestInfo(ctx, e);

		String msg = (String) e.getParams();
		if (StrUtil.isBlank(msg)) {
			final ISystemStatus status = e.getStatus();
			msg = status.getMsg();
		}
		final AjaxJson error = AjaxJson.getError(msg);
		error.setData(Convert.toStr(e.getField()));
		error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
		return error;
	}

	@ExceptionHandler(NullPointerException.class)
	public AjaxJson handlerNullPointerException(Context ctx, Exception e) {
		log.error("", e);

		// 打印请求和异常信息
		printRequestInfo(ctx, e);

		final AjaxJson error = AjaxJson.getError(e.getMessage());
		error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
		return error;
	}

	@ExceptionHandler(ArithmeticException.class)
	public AjaxJson handlerArithmeticException(Context ctx, Exception e) {
		log.error("", e);

		// 打印请求和异常信息
		printRequestInfo(ctx, e);

		final AjaxJson error = AjaxJson.getError(e.getMessage());
		error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
		return error;
	}

	@ExceptionHandler
	public AjaxJson handlerThrowable(Context ctx, Throwable e) {
		log.error("", e);

		// 打印请求和异常信息
		printRequestInfo(ctx, e);

		final AjaxJson error = AjaxJson.getError(e.getMessage());
		error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
		return error;
	}

	/**
	 * 打印请求信息
	 * @param ctx
	 * @param throwable
	 */
	public void printRequestInfo(Context ctx, Throwable throwable) {
		printRequestInfo(ctx, throwable, true);
	}

	/**
	 * 打印请求信息
	 * @param ctx
	 * @param throwable
	 */
	public void printRequestInfo(Context ctx, Throwable throwable, boolean needRecord) {
		String uri = null;
		String method = null;
		NvMap params = null;
		String ip = null;
		try {
			uri = ctx.uri().toString();
			method = ctx.method().toUpperCase();
			params = ctx.paramMap();
			ip = ctx.ip();

		}
		catch (Exception e) {
			log.warn("打印请求信息时异常", e);
			uri = "";
			method = "";
			params = new NvMap();
			ip = "";
		}

		log.warn("请求信息：ip: {}, method: {}, uri: {}\nparams={}", ip, method, uri, params);

		// 需要记录
		try {
			if (needRecord && Objects.nonNull(this.enable) && this.enable) {
				String stackTrace = ExceptionUtil.stacktraceToString(throwable);
				String userId = "admin";
				ExceptionRecordInput record = ExceptionRecordInput.of()
					.uri(uri)
					.method(method)
					.params(Objects.isNull(params) ? null : JSONUtil.toJsonStr(params))
					.ip(ip)
					.userId(userId)
					.happenTime(LocalDateTime.now())
					.stackTrace(stackTrace)
					.build();
				mqSendService.send(record);
			}
		}
		catch (Exception e) {

		}

	}

	/**
	 * 获取异常堆栈消息
	 * @param throwable 异常
	 * @return 堆栈消息
	 */
	public String getExceptionStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		try {
			throwable.printStackTrace(pw);
			return sw.toString();
		}
		catch (Exception e) {
			return throwable.getMessage();
		}
		finally {
			pw.close();
		}
	}

}
