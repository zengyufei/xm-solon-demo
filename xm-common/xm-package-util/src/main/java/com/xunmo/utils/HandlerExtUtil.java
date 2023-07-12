package com.xunmo.utils;

import cn.hutool.core.collection.CollUtil;
import com.xunmo.ext.XmFilterExt;
import com.xunmo.ext.XmHandlerExt;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerExtUtil {

    public static <T extends XmHandlerExt, S extends XmHandlerExt> void toBuildExtRequestHandler(AopContext context, int defaultIndex, Class<T> superClass, Class<S> defaultClass) {
        final SolonApp app = Solon.app();
        AtomicInteger index = new AtomicInteger(defaultIndex);
        // 向外提供钩子
        context.beanOnloaded(aopContext -> {
            final List<T> handlerExts = aopContext.getBeansOfType(superClass);
            if (CollUtil.isNotEmpty(handlerExts)) {
                if (handlerExts.size() == 1) {
                    final T handlerExt = handlerExts.get(0);
                    if (handlerExt.isOpenBefore()) {
                        app.before(index.get(), handlerExt::before);
                    }
                    if (handlerExt.isOpenAfter()) {
                        app.after(handlerExt::after);
                    }
                } else {
                    for (T handlerExt : handlerExts) {
                        if (handlerExt.getClass() == defaultClass) {
                            continue;
                        }
                        if (handlerExt.isOpenBefore()) {
                            app.before(index.getAndIncrement(), handlerExt::before);
                        }
                        if (handlerExt.isOpenAfter()) {
                            app.after(handlerExt::after);
                        }
                    }
                }
            } else {
                try {
                    final S s = defaultClass.newInstance();
                    if (s.isOpenBefore()) {
                        app.before(index.getAndIncrement(), s::before);
                    }
                    if (s.isOpenAfter()) {
                        app.after(s::after);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static <T extends XmFilterExt, S> void toBuildExtRequestFilter(AopContext context, int defaultIndex, Class<T> superClass, Class<S> defaultClass) {
        final SolonApp app = Solon.app();
        AtomicInteger index = new AtomicInteger(defaultIndex);
        // 向外提供钩子
        context.beanOnloaded(aopContext -> {
            final List<T> filterExts = aopContext.getBeansOfType(superClass);
            if (CollUtil.isNotEmpty(filterExts)) {
                if (filterExts.size() == 1) {
                    app.routerInterceptor(index.get(), filterExts.get(0)::doIntercept);
                } else {
                    for (T filterExt : filterExts) {
                        if (filterExt.getClass() == defaultClass) {
                            continue;
                        }
                        app.routerInterceptor(index.getAndIncrement(), filterExt::doIntercept);
                    }
                }
            }
        });
    }

}
