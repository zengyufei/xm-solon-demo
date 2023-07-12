package com.xunmo;


import cn.hutool.core.util.StrUtil;
import com.xunmo.request.args.filter.ArgsConsoleFilter;
import com.xunmo.request.args.filter.ArgsConsoleFilterDefault;
import com.xunmo.request.args.handler.ArgsConsoleHandler;
import com.xunmo.request.args.handler.ArgsConsoleHandlerDefault;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmRequestArgsConsolePluginImp implements Plugin {

    final int defaultIndex = 200;
    final String defaultType = "handler";

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageNameConstants.XM_REQUEST_ARGS_CONSOLE + ".yml");

        final boolean isEnabled = props.getBool(XmPluginPropertiesConstants.xmWebConsoleArgsEnable, true);
        // type = handler(default) or filter
        final String type = props.get(XmPluginPropertiesConstants.xmWebConsoleArgsType, defaultType);

        if (isEnabled) {
            if (StrUtil.equalsIgnoreCase(type, defaultType)) {
                HandlerExtUtil.toBuildExtRequestHandler(context, defaultIndex, ArgsConsoleHandler.class, ArgsConsoleHandlerDefault.class);
            } else {
                HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, ArgsConsoleFilter.class, ArgsConsoleFilterDefault.class);
            }
        }
        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageNameConstants.XM_REQUEST_ARGS_CONSOLE);
        } else {
            System.out.println(XmPackageNameConstants.XM_REQUEST_ARGS_CONSOLE + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageNameConstants.XM_REQUEST_ARGS_CONSOLE);
        } else {
            System.out.println(XmPackageNameConstants.XM_REQUEST_ARGS_CONSOLE + " 插件关闭!");
        }
    }

}
