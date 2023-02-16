package com.xunmo;

import cn.hutool.core.util.StrUtil;
import com.xunmo.request.times.filter.RequestTimesConsoleFilterExt;
import com.xunmo.request.times.filter.RequestTimesFilterDefault;
import com.xunmo.request.times.handler.RequestTimesConsoleHandlerDefault;
import com.xunmo.request.times.handler.RequestTimesConsoleHandlerExt;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmRequestTimesConsolePluginImp implements Plugin {

    final int defaultIndex = 100;
    final String defaultType = "handler";

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_REQUEST_TIMES_CONSOLE + ".yml");

        final boolean isEnabled = props.getBool(XmPluginConfig.xmWebConsoleRequestTimesEnable, true);
        // type = handler(default) or filter
        final String type = props.get(XmPluginConfig.xmWebConsoleRequestTimesType, defaultType);

        if (isEnabled) {
            if (StrUtil.equalsIgnoreCase(type, defaultType)) {
                HandlerExtUtil.toBuildExtRequestHandler(context, defaultIndex, RequestTimesConsoleHandlerExt.class, RequestTimesConsoleHandlerDefault.class);
            } else {
                HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, RequestTimesConsoleFilterExt.class, RequestTimesFilterDefault.class);
            }
        }

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_REQUEST_TIMES_CONSOLE);
        }else {
            System.out.println(XmPackageConstants.XM_REQUEST_TIMES_CONSOLE + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_REQUEST_TIMES_CONSOLE);
        }else {
            System.out.println(XmPackageConstants.XM_REQUEST_TIMES_CONSOLE + " 插件关闭!");
        }
    }

}
