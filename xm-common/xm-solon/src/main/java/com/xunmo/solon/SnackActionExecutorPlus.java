package com.xunmo.solon;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

/**
 * Json 动作执行器
 *
 * @author noear
 * @since 1.0
 * */
public class SnackActionExecutorPlus extends ActionExecutorDefaultPlus {
    public static final SnackActionExecutorPlus global = new SnackActionExecutorPlus();

    private static final String label = "/json";

    private final Options config = Options.def();

    /**
     * 反序列化配置
     * */
    public Options config(){
        return config;
    }

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.contains(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx) throws Exception {
        String json = ctx.bodyNew();

        if (Utils.isNotEmpty(json)) {
            return ONode.loadStr(json, config);
        } else {
            return null;
        }
    }

    /**
     * @since 1.11 增加 requireBody 支持
     * */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {

        if (p.requireBody() == false && p.requireBody() == false && ctx.paramMap().containsKey(p.getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        ONode tmp = (ONode) bodyObj;

        if (tmp.isObject()) {
            if (p.requireBody() == false) {
                //
                //如果没有 body 要求；尝试找按属性找
                //
                if (tmp.contains(p.getName())) {
                    //支持泛型的转换
                    ParameterizedType gp = p.getGenericType();
                    if (gp != null) {
                        return tmp.get(p.getName()).toObject(gp);
                    }

                    return tmp.get(p.getName()).toObject(pt);
                }
            }


            //尝试 body 转换
            if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if (List.class.isAssignableFrom(p.getType())) {
                    return null;
                }

                if (p.getType().isArray()) {
                    return null;
                }

                //支持泛型的转换 如：Map<T>
                ParameterizedType gp = p.getGenericType();
                if (gp != null) {
                    return tmp.toObject(gp);
                }

                return tmp.toObject(pt);
            }
        }

        if (tmp.isArray()) {
            //如果参数是非集合类型
            if (!Collection.class.isAssignableFrom(pt)) {
                return null;
            }

            //集合类型转换
            ParameterizedType gp = p.getGenericType();
            if (gp != null) {
                //转换带泛型的集合
                return tmp.toObject(gp);
            }

            //不仅可以转换为List 还可以转换成Set
            return tmp.toObject(p.getType());
        }

        return tmp.val().getRaw();
    }
}
