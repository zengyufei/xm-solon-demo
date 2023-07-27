package com.xunmo.annotations.valid;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER }) // 只让它作用到参数，不管作用在哪，最终都是对Context的校验
@Retention(RetentionPolicy.RUNTIME)
public @interface IsEnum {

	@Note("枚举类型") // 用Note注解，是为了用时还能看到这个注释
	Class<? extends Enum<?>> value();

	String message() default "不是系统枚举, 请输入正确的枚举值";

}
