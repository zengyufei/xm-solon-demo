package com.xunmo.annotations;

import com.xunmo.common.BaseEnum;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DescToEnumCode {

	Class<? extends BaseEnum<?>> value();

}
