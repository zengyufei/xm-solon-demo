package com.xunmo.jimmer.repository.support;

import com.xunmo.jimmer.repository.JRepository;
import com.xunmo.jimmer.repository.bytecode.ClassCodeWriter;
import com.xunmo.jimmer.repository.bytecode.JavaClassCodeWriter;
import com.xunmo.jimmer.repository.bytecode.JavaClasses;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.kt.KSqlClient;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public class JimmerRepositoryFactory {

	private final Object sqlClient;

	public JimmerRepositoryFactory(Object sqlClient) {
		this.sqlClient = sqlClient;
	}

	@NotNull
	public Object getTargetRepository(Class<?> repositoryInterface, Class<?> domainType) {
		boolean jRepository = JRepository.class.isAssignableFrom(repositoryInterface);
		if (sqlClient instanceof JSqlClient) {
			if (!jRepository) {
				throw new IllegalStateException("The type of current sqlClient object is \""
						+ JSqlClient.class.getName() + "\", but repository interface \"" + repositoryInterface.getName()
						+ "\" does not extend  \"" + JRepository.class.getName() + "\"");
			}
		}
		else {
			throw new IllegalStateException("Illegal repository interface \"" + repositoryInterface.getName()
					+ "\", it is neither \"" + JRepository.class.getName() + "\"");
		}
		return getObject(repositoryInterface, domainType, jRepository);
	}

	@NotNull
	private Object getObject(Class<?> repositoryInterface, Class<?> domainType, boolean jRepository) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(ClassCodeWriter.implementationClassName(repositoryInterface), true,
					repositoryInterface.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// Do nothing
		}
		if (clazz == null) {
			ClassCodeWriter writer = new JavaClassCodeWriter(repositoryInterface, domainType);
			byte[] bytecode = writer.write();
			clazz = JavaClasses.define(bytecode, repositoryInterface);
		}
		try {
			return clazz.getConstructor(jRepository ? JSqlClient.class : KSqlClient.class).newInstance(sqlClient);
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
			throw new AssertionError("Internal bug", ex);
		}
		catch (InvocationTargetException ex) {
			Throwable targetException = ex.getTargetException();
			if (targetException instanceof RuntimeException) {
				throw (RuntimeException) targetException;
			}
			if (targetException instanceof Error) {
				throw (Error) targetException;
			}
			throw new UndeclaredThrowableException(ex.getTargetException(), "Failed to create repository");
		}
	}

}
