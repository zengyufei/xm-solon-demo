package com.xunmo.config.jimmer;

import cn.hutool.core.convert.Convert;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class SnowflakeIdGenerator implements UserIdGenerator<String> {

	private IdWorker idWorker = new IdWorker(1L);

	@Override
	public String generate(Class<?> entityType) {
		return Convert.toStr(idWorker.nextId());
	}

}
