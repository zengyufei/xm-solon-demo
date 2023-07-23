package com.xunmo.jimmer.annotation;

import java.lang.annotation.*;

/**
 * 数据工厂注解
 * <p>
 * 例：
 *
 * @Db("db1") SqlSessionFactory factory; @Db("db1") SqlSession session; @Db("db1") Mapper
 * mapper;
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Db {

	/**
	 * sqlSessionFactory bean name
	 */
	String value() default "";

}
