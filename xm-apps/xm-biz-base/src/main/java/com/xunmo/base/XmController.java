package com.xunmo.base;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.annotation.Valid;

//这个注解可继承，用于支持子类的验证
//
@Slf4j
@Valid
public class XmController<M extends XmService<T>, T> {

    @Inject
    public M service;
}
