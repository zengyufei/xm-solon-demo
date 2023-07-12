package com.xunmo;


import com.xunmo.annotations.ExceptionHandler;
import com.xunmo.exceptions.GlobalException;
import com.xunmo.exceptions.XmExceptionEntity;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;

@Slf4j
public class XmExceptionPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageNameConstants.XM_EXCEPTION + ".yml");

        // 统一异常处理
        context.beanExtractorAdd(ExceptionHandler.class, (bw, method, anno) -> {
            final Class<? extends Throwable> errorClass = anno.value();
            final Object object = bw.raw();
            if (errorClass == Throwable.class) {
                GlobalException.errorByXmExceptionEntity.put(Throwable.class, new XmExceptionEntity(errorClass, object, method));
            } else if (errorClass == Exception.class) {
                GlobalException.errorByXmExceptionEntity.put(Exception.class, new XmExceptionEntity(errorClass, object, method));
            } else {
                GlobalException.errorByXmExceptionEntity.put(errorClass, new XmExceptionEntity(errorClass, object, method));
            }
        });


        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageNameConstants.XM_EXCEPTION);
        } else {
            System.out.println(XmPackageNameConstants.XM_EXCEPTION + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageNameConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageNameConstants.XM_EXCEPTION);
        } else {
            System.out.println(XmPackageNameConstants.XM_EXCEPTION + " 插件关闭!");
        }
    }

}
