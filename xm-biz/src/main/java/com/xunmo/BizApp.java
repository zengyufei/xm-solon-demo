package com.xunmo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.core.ChainManager;
import org.noear.solon.core.handle.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.extend.quartz.EnableQuartz;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Slf4j
@EnableQuartz
public class BizApp {

    public static void main(String[] args) throws NoSuchFieldException {

        final Class<ChainManager> chainManagerClass = ChainManager.class;
        final Field executeHandlers = chainManagerClass.getDeclaredField("executeHandlers");
        executeHandlers.setAccessible(true);

        Solon.start(BizApp.class, args, app -> {

            //转发日志到 Slf4j 接口
            LogUtil.globalSet(new LogUtilToSlf4j());  //v1.10.11 后支持

            app.get("/", ctx -> {
                //                ctx.forward("/railway-bureau-test/index.html");
                ctx.redirect("/dict/view/tree");
                //                ctx.redirect("/employee_info/view/index");
            });

            //2.添加资源路径
            //StaticMappings.add("/railway-bureau-test", false, new ClassPathStaticRepository("railway-bureau-test"));
            //3.添加扩展目录：${solon.extend}/static/
            //            StaticMappings.add("/railway-bureau-test", relativePath -> {
            //                String location;
            //                String path = Solon.cfg().sourceLocation().getPath();
            //                if (path == null) {
            //                    throw new IllegalStateException("No extension directory exists");
            //                } else {
            //                    location = path + "railway-bureau-test";
            //                }
            //                File file = new File(location , relativePath);
            //                return file.exists() ? file.toURI().toURL() : null;
            //            });
            // 自动添加 multipart
            //            app.filter(-1, (ctx, chain) -> {
            //                if (ctx.path().startsWith("/upload")) {
            //                    ctx.autoMultipart(true); //给需要的路径加 autoMultipart
            //                }
            //                chain.doFilter(ctx);
            //            });


            // 向外提供钩子
            //            app.before(9999999, ctx -> {
            //                final PageRequest pageRequest = ctx.paramAsBean(PageRequest.class);
            //                ctx.paramSet("pageRequest", JSONUtil.toJsonStr(pageRequest));
            //                System.out.println(JSONUtil.toJsonPrettyStr(pageRequest));
            //            });


            //            // 向外提供钩子
            app.context().beanOnloaded(aopContext -> {
                final ChainManager chainManager = app.chainManager();
                try {
                    Map<Class<?>, ActionExecuteHandlerDefault> map = (Map<Class<?>, ActionExecuteHandlerDefault>) executeHandlers.get(chainManager);
                    final Set<Class<?>> classes = map.keySet();
                    System.out.println("使用Json框架:");
                    for (Class<?> aClass : classes) {
                        System.out.println(aClass.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            app.chainManager()
                    .addExecuteHandler(new JacksonActionExecutor() {
                        @Override
                        protected Object changeBody(Context ctx) throws Exception {
                            final ObjectNode changeBody = (ObjectNode) super.changeBody(ctx);
                            ctx.paramMap().forEach(changeBody::put);
                            return changeBody;
                        }
                    });
        });
    }
}
