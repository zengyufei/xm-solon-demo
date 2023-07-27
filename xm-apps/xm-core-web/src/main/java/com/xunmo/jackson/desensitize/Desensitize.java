package com.xunmo.jackson.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * Created by EalenXie on 2021/10/8 11:30
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ObjectDesensitizeSerializer.class)
@Documented
public @interface Desensitize {

	/**
	 * 脱敏器实现
	 */
	@SuppressWarnings("all")
	Class<? extends Desensitization<?>> desensitization();

}
