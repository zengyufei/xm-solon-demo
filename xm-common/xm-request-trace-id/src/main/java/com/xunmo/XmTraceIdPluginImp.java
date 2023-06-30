package com.xunmo;


import com.xunmo.request.trace.filter.TraceIdFilterDefault;
import com.xunmo.request.trace.filter.TraceIdFilterExt;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.slf4j.TtlMDCAdapter;

@Slf4j
public class XmTraceIdPluginImp implements Plugin {

    final int defaultIndex = 1;

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_REQUEST_TRACE_ID + ".yml");
//        final SolonApp app = Solon.app();

        final boolean isEnabledTraceId = props.getBool(XmPluginConfig.xmLogTraceIdEnable, true);
        final boolean isEnabledThreadTraceId = props.getBool(XmPluginConfig.xmLogTraceidThreadEnable, true);

        if (isEnabledTraceId) {
            HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, TraceIdFilterExt.class, TraceIdFilterDefault.class);
            if (isEnabledThreadTraceId) {
                // 设置一个 reqId 到 MDC 类中, 可传给子线程(特殊情况下无法传递);
                TtlMDCAdapter.getInstance();
            }
        }

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_REQUEST_TRACE_ID);
        }else {
            System.out.println(XmPackageConstants.XM_REQUEST_TRACE_ID + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_REQUEST_TRACE_ID);
        }else {
            System.out.println(XmPackageConstants.XM_REQUEST_TRACE_ID + " 插件关闭!");
        }
    }

}
