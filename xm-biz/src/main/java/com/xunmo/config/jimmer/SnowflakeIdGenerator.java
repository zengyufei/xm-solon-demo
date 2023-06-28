package com.xunmo.config.jimmer;

import cn.hutool.core.util.IdUtil;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class SnowflakeIdGenerator implements UserIdGenerator<String> {
    @Override
    public String generate(Class<?> entityType) {
        return IdUtil.getSnowflake().nextIdStr();
    }
}
