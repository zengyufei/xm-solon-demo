package com.xunmo.common.base;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.jetbrains.annotations.Nullable;

/*
 * 乐观锁基类
 */
@MappedSuperclass
public interface VersionEntity {

	/**
	 * 乐观锁版本号
	 */
	@Nullable
	@Column(name = Columns.version)
	String version();

	/**
	 * 表字段名
	 */
	interface Columns {

		String version = "version"; // 乐观锁版本号

	}

	/**
	 * 实体字段名
	 */
	interface FieldNames {

		String version = "version"; // 乐观锁版本号

	}

}
