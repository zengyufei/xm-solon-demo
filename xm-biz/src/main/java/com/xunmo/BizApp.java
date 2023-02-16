package com.xunmo;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.extend.quartz.EnableQuartz;
import org.noear.solon.logging.utils.LogUtilToSlf4j;

@Slf4j
@EnableQuartz
public class BizApp {

    public static void main(String[] args) {
        Solon.start(BizApp.class, args, app -> {

            //转发日志到 Slf4j 接口
            LogUtil.globalSet(new LogUtilToSlf4j());  //v1.10.11 后支持

            app.get("/", ctx -> {
//                ctx.forward("/railway-bureau-test/index.html");
                ctx.redirect("/dict/view/list");
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
        });
    }
}
