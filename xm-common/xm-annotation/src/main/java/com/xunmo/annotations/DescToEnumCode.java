package com.xunmo.annotations;


import com.xunmo.ext.ICodeEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DescToEnumCode {

    Class<? extends ICodeEnum<?>> value();
}
