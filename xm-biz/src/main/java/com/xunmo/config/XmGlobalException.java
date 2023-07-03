package com.xunmo.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.XmConstants;
import com.xunmo.annotations.ExceptionHandler;
import com.xunmo.common.CustomException;
import com.xunmo.common.ISystemStatus;
import com.xunmo.utils.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ExceptionHandler(CustomException.class)
    public AjaxJson handlerCustomException(Context ctx, CustomException e) {
        log.error(ExceptionUtil.stacktraceToString(e));

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
        log.error(ExceptionUtil.stacktraceToString(e));

        // 打印请求和异常信息
        printRequestInfo(ctx, e);

        final AjaxJson error = AjaxJson.getError(e.getMessage());
        error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
        return error;
    }

    @ExceptionHandler(ArithmeticException.class)
    public AjaxJson handlerArithmeticException(Context ctx, Exception e) {
        log.error(ExceptionUtil.stacktraceToString(e));

        // 打印请求和异常信息
        printRequestInfo(ctx, e);

        final AjaxJson error = AjaxJson.getError(e.getMessage());
        error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
        return error;
    }

    @ExceptionHandler
    public AjaxJson handlerThrowable(Context ctx, Throwable e) {
        log.error(ExceptionUtil.stacktraceToString(e));

        // 打印请求和异常信息
        printRequestInfo(ctx, e);

        final AjaxJson error = AjaxJson.getError(e.getMessage());
        error.set(XmConstants.REQ_ID, ctx.param(XmConstants.REQ_ID));
        return error;
    }


    /**
     * 打印请求信息
     *
     * @param ctx
     * @param throwable
     */
    public void printRequestInfo(Context ctx, Throwable throwable) {
        printRequestInfo(ctx, throwable, true);
    }


    /**
     * 打印请求信息
     *
     * @param ctx
     * @param throwable
     */
    public void printRequestInfo(Context ctx, Throwable throwable, boolean needRecord) {
        String uri = null;
        String method = null;
        Map<String, List<String>> params = null;
        String ip = null;
        try {
            uri = ctx.uri().toString();
            method = ctx.method().toUpperCase();
            params = ctx.paramsMap();
            ip = ctx.ip();

        } catch (Exception e) {
            log.warn("打印请求信息时异常", e);
            uri = "";
            method = "";
            params = new HashMap<>();
            ip = "";
        }

        log.warn("请求信息：ip: {}, method: {}, uri: {}\nparams={}", ip, method, uri, params);

//        // 需要记录
//        try{
//            if(needRecord && Objects.nonNull(this.enable) && this.enable){
//                String stackTrace = ExceptionUtil.stacktraceToString(throwable);
//                String userId = "admin";
//                ExceptionRecord record = new ExceptionRecord()
//                        .setUri(uri)
//                        .setMethod(method)
//                        .setParams(Objects.isNull(params)?null: JSONUtil.toJsonStr(params))
//                        .setIp(ip)
//                        .setUserId(userId)
//                        .setHappenTime(System.currentTimeMillis())
//                        .setStackTrace(stackTrace);
//                SenderExceptionRecord.send(record);
//            }
//        }catch (Exception e){
//
//        }

    }

    /**
     * 获取异常堆栈消息
     *
     * @param throwable 异常
     * @return 堆栈消息
     */
    public String getExceptionStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
            return throwable.getMessage();
        } finally {
            pw.close();
        }
    }
}
