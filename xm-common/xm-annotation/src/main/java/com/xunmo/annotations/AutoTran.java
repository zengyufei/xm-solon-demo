package com.xunmo.annotations;

import java.lang.annotation.*;

/**
 * 此注解会使用asm代理机制
 *
 * @author noear
 * @since 1.1
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited // 声明注解具有继承性
public @interface AutoTran {

}
