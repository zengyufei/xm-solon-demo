package com.xunmo.biz;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class IndexController {
    @Mapping("/")
    public void home(Context ctx) {
        //通过302方式，通知客户端跳转到 /login （浏览器会发生2次请求，地址会变成/login）
        ctx.redirect("/index.html");

        //在服务端重新路由到 /login （浏览器发生1次请求，地址不会变）
//        ctx.forward("/login");
    }
}    
