package com.xunmo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.proxy.ProxyUtil;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;
import org.noear.solon.web.cors.CrossHandler;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class XmWebPluginImp implements Plugin {

    //初始化json定制（需要在插件运行前定制）
    //    private static void initMvcJsonCustom(SnackRenderFactory factory) {
    //        //示例1：通过转换器，做简单类型的定制
    //        factory.addConvertor(Date.class, s -> DateUtil.date(s).toString());
    //        factory.addConvertor(LocalDateTime.class, s -> DateUtil.date(s).toString());
    //        factory.addConvertor(LocalDate.class, s -> DateUtil.date(s).toString("yyyy-MM-dd"));
    ////        factory.addConvertor(Double.class, String::valueOf);
    //    }

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
        //        if (isEnableDecodeDatetime) {
        //            // 定制化 json 对日期类型的处理;
        //            app.onEvent(SnackRenderFactory.class, XmWebPluginImp::initMvcJsonCustom);
        //        }
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
            //            Solon.context().beanBuilderAdd(Controller.class, (clz, bw, anno) -> {
            //                if (clz.isInterface()) {
            //                    return;
            //                }
            //                new HandlerLoaderPlus(bw).load(app);
            //            });
        }

        if (isEnableJsonTrim) {
            // 处理 json 字段左右空白, 空字符串入参设置为null;
            app.onEvent(JacksonActionExecutor.class, executor -> {

                ProxyUtil.attach(app.context(), JacksonActionExecutor.class, executor, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        final String methodName = method.getName();
                        System.out.println("调用方法 " + methodName);
                        return method.invoke(o, objects);
                    }
                });

//                executor.config().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                final SimpleModule module = new SimpleModule();
//                final EduJavaTimeModule module = new EduJavaTimeModule();
                module.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
                    private static final long serialVersionUID = -2186517763342421483L;

                    @Override
                    public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                        if (StrUtil.isBlank(jsonParser.getValueAsString())) {
                            return null;
                        }

                        return StrUtil.trim(jsonParser.getValueAsString());
                    }
                });
                module.addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
                    private static final long serialVersionUID = -2186517763342421483L;

                    @Override
                    public Date deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                        if (StrUtil.isBlank(jsonParser.getValueAsString())) {
                            return null;
                        }

                        final String trim = StrUtil.trim(jsonParser.getValueAsString());
                        return DateUtil.parse(trim);
                    }
                });
                executor.config().registerModule(module);
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
        } else {
            System.out.println(XmPackageConstants.XM_WEB + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_WEB);
        } else {
            System.out.println(XmPackageConstants.XM_WEB + " 插件关闭!");
        }
    }
}
