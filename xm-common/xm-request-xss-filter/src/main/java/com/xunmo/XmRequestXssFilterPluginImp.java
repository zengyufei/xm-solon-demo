package com.xunmo;

import cn.hutool.core.util.StrUtil;
import com.xunmo.request.xss.filter.RequestXssFilter;
import com.xunmo.request.xss.filter.RequestXssFilterDefault;
import com.xunmo.request.xss.handler.RequestXssHandler;
import com.xunmo.request.xss.handler.RequestXssHandlerDefault;
import com.xunmo.utils.HandlerExtUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmRequestXssFilterPluginImp implements Plugin {

    final int defaultIndex = 100;
    final String defaultType = "handler";

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageNameConstants.XM_REQUEST_XSS_FILTER + ".yml");

        final boolean isEnabled = props.getBool(XmPluginPropertiesConstants.xmWebXssEnable, true);
        // type = handler(default) or filter
        final String type = props.get(XmPluginPropertiesConstants.xmWebXssType, defaultType);

        if (isEnabled) {
            if (StrUtil.equalsIgnoreCase(type, defaultType)) {
                HandlerExtUtil.toBuildExtRequestHandler(context, defaultIndex, RequestXssHandler.class, RequestXssHandlerDefault.class);
            } else {
                HandlerExtUtil.toBuildExtRequestFilter(context, defaultIndex, RequestXssFilter.class, RequestXssFilterDefault.class);
            }
        }


        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageNameConstants.XM_REQUEST_XSS_FILTER);
        } else {
            System.out.println(XmPackageNameConstants.XM_REQUEST_XSS_FILTER + " 包加载完毕!");
        }
    }


    @Override
    public void stop() throws Throwable {
        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageNameConstants.XM_REQUEST_XSS_FILTER);
        } else {
            System.out.println(XmPackageNameConstants.XM_REQUEST_XSS_FILTER + " 插件关闭!");
        }
    }


}
