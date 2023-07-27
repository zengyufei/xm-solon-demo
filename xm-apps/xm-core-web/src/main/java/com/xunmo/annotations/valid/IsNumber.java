package com.xunmo.annotations.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER }) // 只让它作用到参数，不管作用在哪，最终都是对Context的校验
@Retention(RetentionPolicy.RUNTIME)
public @interface IsNumber {

	String message() default "不是数字, 请输入正确的数字";

}
