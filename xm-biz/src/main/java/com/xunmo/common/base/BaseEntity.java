package com.xunmo.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/*
 * 实体基类
 */
@MappedSuperclass
public interface BaseEntity {

    /**
     * 创建人ID
     *
     * @return {@link String}
     */
    @Column(name = Columns.createId)
    String createId();

    /**
     * 修改人ID
     *
     * @return {@link String}
     */
    @Nullable
    @Column(name = Columns.updateId)
    String updateId();

    /**
     * 创建人姓名
     *
     * @return {@link String}
     */
    @Column(name = Columns.createName)
    String createName();

    /**
     * 修改人姓名
     *
     * @return {@link String}
     */
    @Nullable
    @Column(name = Columns.updateName)
    String updateName();

    /**
     * 创建时间
     *
     * @return {@link LocalDateTime}
     */
    @Column(name = Columns.createTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime();

    /**
     * 修改时间
     *
     * @return {@link LocalDateTime}
     */
    @Nullable
    @Column(name = Columns.updateTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime();

    interface Columns {
        String createTime = "create_time"; // 创建时间
        String updateTime = "update_time"; // 修改时间
        String createId = "create_id"; // 创建人ID
        String updateId = "update_id"; // 修改人ID
        String createName = "create_name"; // 创建人用户名
        String updateName = "update_name"; // 修改人用户名
    }

    interface FieldNames {
        String createId = "createId"; // 创建人ID
        String updateId = "updateId"; // 修改人ID
        String createTime = "createTime"; // 创建时间
        String updateTime = "updateTime"; // 修改时间
        String createName = "createName"; // 创建人用户名
        String updateName = "updateName"; // 修改人用户名
    }
}
