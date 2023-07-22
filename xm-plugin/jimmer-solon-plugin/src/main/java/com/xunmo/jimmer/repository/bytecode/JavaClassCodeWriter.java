package com.xunmo.jimmer.repository.bytecode;

import com.xunmo.jimmer.repository.support.JRepositoryImpl;
import org.babyfish.jimmer.sql.JSqlClient;

import java.lang.reflect.Method;

public class JavaClassCodeWriter extends ClassCodeWriter {

	public JavaClassCodeWriter(Class<?> repositoryInterface, Class<?> domainType) {
		super(repositoryInterface, domainType, JSqlClient.class, JRepositoryImpl.class);
	}

	@Override
	protected MethodCodeWriter createMethodCodeWriter(Method method, String id) {
		return new JavaMethodCodeWriter(this, method, id);
	}
}
