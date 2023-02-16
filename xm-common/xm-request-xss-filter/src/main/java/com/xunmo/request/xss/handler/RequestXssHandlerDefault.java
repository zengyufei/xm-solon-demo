package com.xunmo.request.xss.handler;

import cn.hutool.core.util.StrUtil;
import com.xunmo.request.xss.utils.XssUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class RequestXssHandlerDefault implements RequestXssHandlerExt {

    @Override
    public void before(Context ctx) throws IOException {
        //请求头
        for (Map.Entry<String, String> kv : ctx.headerMap().entrySet()) {
            String val = kv.getValue();
            //处理Val
            kv.setValue(cleanXss(val));
        }

        //请求参数
        for (Map.Entry<String, String> kv : ctx.paramMap().entrySet()) {
            String val = kv.getValue();
            //处理val
            kv.setValue(cleanXss(val));
        }
        //请示主体
        if (ctx.contentType() != null && ctx.contentType().contains("json")) {
            String val = ctx.body();
            //处理vaL
            ctx.bodyNew(cleanXss(val));
        }
    }

    @Override
    public void after(Context ctx) {
    }

    private String cleanXss(String input) {
        if (StrUtil.isBlankOrUndefined(input)) {
            return input;
        }

        input = XssUtil.removeEvent(input);
        input = XssUtil.removeScript(input);
        input = XssUtil.removeEval(input);
        input = XssUtil.swapJavascript(input);
        input = XssUtil.swapVbscript(input);
        input = XssUtil.swapLivescript(input);
        input = XssUtil.encode(input);

        return input;
    }

}
