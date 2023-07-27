package com.xunmo.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.xunmo.jackson.desensitize.Desensitize;

import java.lang.annotation.*;

/**
 * Created by EalenXie on 2021/10/8 11:30 电话脱敏 注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Desensitize(desensitization = ThousandDesensitization.class)
@Documented
public @interface ThousandDesensitize {

}
