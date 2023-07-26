package com.xunmo.exceptions;

import java.lang.reflect.Method;

public class XmExceptionEntity {

	Class<?> errorClass;

	Object object;

	Method method;

	public XmExceptionEntity(Class<?> errorClass, Object object, Method method) {
		this.errorClass = errorClass;
		this.object = object;
		this.method = method;
	}

}
