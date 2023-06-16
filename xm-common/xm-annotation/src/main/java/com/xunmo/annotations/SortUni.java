package com.xunmo.annotations;


import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SortUni {

    boolean isNull() default false;
    String defaultValue() default "";
}
