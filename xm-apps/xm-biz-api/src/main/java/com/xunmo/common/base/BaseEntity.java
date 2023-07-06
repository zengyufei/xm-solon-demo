package com.xunmo.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunmo.webs.user.entity.User;
import org.babyfish.jimmer.sql.*;
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
    @IdView
    String createId();

    @ManyToOne
    @JoinColumn(name = Columns.createId)
    User create();

    /**
     * 修改人ID
     *
     * @return {@link String}
     */
    @IdView
    @Nullable
    String updateId();

    @ManyToOne
    @Nullable
    @JoinColumn(name = Columns.updateId)
    User update();

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
    }

    interface FieldNames {
        String createId = "createId"; // 创建人ID
        String updateId = "updateId"; // 修改人ID
        String createTime = "createTime"; // 创建时间
        String updateTime = "updateTime"; // 修改时间
    }
}
