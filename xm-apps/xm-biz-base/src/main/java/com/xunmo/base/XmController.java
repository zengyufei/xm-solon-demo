package com.xunmo.base;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.annotation.Valid;

//这个注解可继承，用于支持子类的验证
//
@Slf4j
@Valid
public class XmController<M extends XmService<T>, T> /*implements Render*/ {

    @Inject
    public M service;

    /*@Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (obj == null) {
            return;
        }

        if (obj instanceof String) {
            //普通字符串，直接输出
            ctx.output((String) obj);
        } else if (obj instanceof ModelAndView) {
            //视图模型，直接渲染
            ctx.render(obj);
        } else {
            if (obj instanceof AjaxJson) {
            } else if (obj instanceof Throwable) {
                //此处是重点，把异常进行标准化转换
                //
                Throwable err = (Throwable) obj;
                log.error(ExceptionUtil.stacktraceToString(err));
                obj = AjaxJson.getError(err.getMessage());
            } else if (!(obj instanceof AjaxJson)) {
                //否则做为成功数据，格式化为 Result 结构
                //
                obj = AjaxJson.getSuccessData(obj);
            }

            ctx.render(obj);
            //或者调用特定接口直接输出：
//             ctx.outputAsJson(JSON.toJson(obj));
//             ctx.outputAsJson(MyJacksonRenderFactory.global.config().writeValueAsString(obj));
        }
    }*/
}
