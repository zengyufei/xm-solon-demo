package com.xunmo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.solon.ActionExecutorDefaultPlus;
import com.xunmo.solon.HandlerLoaderPlus;
import com.xunmo.solon.SnackActionExecutorPlus;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.serialization.snack3.SnackActionExecutor;
import org.noear.solon.serialization.snack3.SnackRenderFactory;
import org.noear.solon.web.cors.CrossHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class XmWebPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_WEB + ".yml");
        final SolonApp app = Solon.app();

        final boolean isEnableSuperController = props.getBool(XmPluginConfig.xmWebScanSuperControllerEnable, true);
        final boolean isEnableBind = props.getBool(XmPluginConfig.xmWebScanGenericBindEnable, true);
        final boolean isEnableCors = props.getBool(XmPluginConfig.xmWebCorsEnable, true);
        final boolean isEnableArgsTrim = props.getBool(XmPluginConfig.xmWebArgsTrimEnable, true);
        final boolean isEnableJsonTrim = props.getBool(XmPluginConfig.xmWebJsonTrimEnable, true);
        final boolean isEnableDecodeDatetime = props.getBool(XmPluginConfig.xmWebJsonDecodeDatetimeEnable, true);

        if (isEnableCors) {
            // 解决 cros 跨域 问题;
            app.before(new CrossHandler().allowedOrigins("*"));
        }
        if (isEnableDecodeDatetime) {
            // 定制化 json 对日期类型的处理;
            app.onEvent(SnackRenderFactory.class, XmWebPluginImp::initMvcJsonCustom);
        }
        if (isEnableArgsTrim) {
            // 处理参数左右空白, 空字符串入参设置为null;
            app.before(ctx -> {
                final NvMap paramMap = ctx.paramMap();
                if (!paramMap.isEmpty()) {
                    final Iterator<Map.Entry<String, String>> entryIterator = paramMap.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        final Map.Entry<String, String> entry = entryIterator.next();
                        final String key = entry.getKey();
                        final String value = entry.getValue();
                        if (StrUtil.isBlankOrUndefined(value)) {
                            entryIterator.remove();
                        } else {
                            paramMap.put(key, StrUtil.trim(value));
                        }
                    }
                }
            });
        }

        if (isEnableSuperController) {
            // 自动扫描父类接口
            Solon.context().beanBuilderAdd(Controller.class, (clz, bw, anno) -> {
                if (clz.isInterface()) {
                    return;
                }
                new HandlerLoaderPlus(bw).load(app);
            });
        }

        if (isEnableBind) {
            // 父类泛型参数被擦除， 手动进行参数绑定
            EventBus.subscribe(SnackActionExecutor.class, cfg -> {
                EventBus.push(SnackActionExecutorPlus.global);
                Bridge.actionExecutorRemove(SnackActionExecutor.class);
                Bridge.actionExecutorAdd(SnackActionExecutorPlus.global);
                Bridge.actionExecutorDefSet(new ActionExecutorDefaultPlus());

                if (isEnableJsonTrim) {
                    // 处理 json 字段左右空白, 空字符串入参设置为null;
                    SnackActionExecutorPlus.global.config().addDecoder(String.class, (node, type) -> {
                        if (StrUtil.isBlankOrUndefined(node.getString())) {
                            return null;
                        } else {
                            return StrUtil.trim(node.getString());
                        }
                    });
                }

            });
        } else if (isEnableJsonTrim) {
            // 处理 json 字段左右空白, 空字符串入参设置为null;
            app.onEvent(SnackActionExecutor.class, executor -> {
                executor.config().addDecoder(String.class, (node, type) -> {
                    if (StrUtil.isBlankOrUndefined(node.getString())) {
                        return null;
                    } else {
                        return StrUtil.trim(node.getString());
                    }
                });
            });
        }


        // 手动注册缓存
//        final boolean isEnabled = props.getBool("xm.cache.enable", true);
//        if (isEnabled) {
//            //获取bean（根据配置集合自动生成）
//            RedisCacheService redisCacheService = props.getBean("xm.cache", RedisCacheService.class);
//            if (redisCacheService != null) {
//                //可以进行手动字段注入
//                context.beanInject(redisCacheService);
//                //包装Bean（指定类型的）
//                BeanWrap beanWrap = new BeanWrap(context, CacheService.class, redisCacheService, null, true);
//                //以类型注册
//                context.putWrap(CacheService.class, beanWrap);
//            }
//        }


        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_WEB);
        }else {
            System.out.println(XmPackageConstants.XM_WEB + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_WEB);
        }else {
            System.out.println(XmPackageConstants.XM_WEB + " 插件关闭!");
        }
    }

    //初始化json定制（需要在插件运行前定制）
    private static void initMvcJsonCustom(SnackRenderFactory factory) {
        //示例1：通过转换器，做简单类型的定制
        factory.addConvertor(Date.class, s -> DateUtil.date(s).toString());
        factory.addConvertor(LocalDateTime.class, s -> DateUtil.date(s).toString());
        factory.addConvertor(LocalDate.class, s -> DateUtil.date(s).toString("yyyy-MM-dd"));
//        factory.addConvertor(Double.class, String::valueOf);
    }
}
