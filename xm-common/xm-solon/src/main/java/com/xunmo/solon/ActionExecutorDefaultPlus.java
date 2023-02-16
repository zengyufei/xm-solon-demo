package com.xunmo.solon;

import cn.hutool.core.util.ArrayUtil;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Map;

/**
 * 默认的ActionExecutor实现
 *
 * @author noear
 * @since 1.0Plus
 * */
public class ActionExecutorDefaultPlus extends ActionExecutorDefault {


    /**
     * 尝试将值按类型转换
     */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        String pn = p.getName();        //参数名
        String pv = p.getValue(ctx);    //参数值
        Object tv = null;               //目标值

        if (pv == null) {
            pv = p.defaultValue();
        }

        if (pv == null) {
            //
            // 没有从 ctx.param 直接找到值
            //
            if (UploadedFile.class == pt) {
                //1.如果是 UploadedFile 类型
                tv = ctx.file(pn);
            } else {
                //$name 的变量，从attr里找
                if (pn.startsWith("$")) {
                    tv = ctx.attr(pn);
                } else {
                    boolean isDone = false;
                    if (pt.getName().equals("java.lang.Object")) {
                        try {
                            final Object controller = ctx.attr("controller");
                            // GenericTypeUtil.getSuperClassGenericType(controller.getClass(), controller.getClass().getInterfaces()[0], 1);
                            final Class<?>[] resolveTypeArguments = GenericUtil.resolveTypeArguments(controller.getClass(), Map.class);
                            if (ArrayUtil.isNotEmpty(resolveTypeArguments)) {
                                ClassWrap clzW = ClassWrap.get(resolveTypeArguments[1]);
                                Map<String, String> map = ctx.paramMap();

                                tv = clzW.newBy(map::get, ctx);
                                isDone = true;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    if (!isDone) {
                        if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
                            //如果是java基础类型，则为null（后面统一地 isPrimitive 做处理）
                            //
                            tv = null;
                        } else {
                            //尝试转为实体
                            tv = changeEntityDo(ctx, pn, pt);
                        }
                    }
                }
            }
        } else {
            //如果拿到了具体的参数值，则开始转换
            tv = ConvertUtil.to(p.getParameter(), pt, pn, pv, ctx);
        }

        return tv;
    }

    /**
     * 尝试将值转换为实体
     */
    private Object changeEntityDo(Context ctx, String name, Class<?> type) throws Exception {
        ClassWrap clzW = ClassWrap.get(type);
        Map<String, String> map = ctx.paramMap();

        return clzW.newBy(map::get, ctx);
    }
}
