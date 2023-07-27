package com.xunmo.annotations.valid;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER }) // 只让它作用到参数，不管作用在哪，最终都是对Context的校验
@Retention(RetentionPolicy.RUNTIME)
public @interface IsDate {

	@Note("日期表达式, 默认为：yyyy-MM-dd 格式") // 用Note注解，是为了用时还能看到这个注释
	String value() default "";

	String message() default "日期格式不正确";

}
