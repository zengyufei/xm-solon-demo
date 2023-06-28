package com.xunmo.common.base;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.jetbrains.annotations.Nullable;

/*
 * 租户基类
 */
@MappedSuperclass
public interface TenantEntity {

    /**
     * 租户id
     */
    @Nullable
    @Column(name = Columns.tenantId)
    String tenantId();

    /**
     * 表字段名
     */
    interface Columns {
        String tenantId = "tenant_id"; // 租户id
    }

    /**
     * 实体字段名
     */
    interface FieldNames {
        String tenantId = "tenantId"; // 租户id
    }


}
