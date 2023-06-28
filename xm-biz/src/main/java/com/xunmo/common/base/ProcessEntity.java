package com.xunmo.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.MappedSuperclass;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/*
 * 流程基类
 */
@MappedSuperclass
public interface ProcessEntity {

    /**
     * 审批状态
     */
    @Nullable
    @Column(name = Columns.approvalStatus)
    String approvalStatus();

    /**
     * 审批人id
     */
    @Nullable
    @Column(name = Columns.approverId)
    String approverId();

    /**
     * 审批人姓名
     */
    @Nullable
    @Column(name = Columns.approverName)
    String approverName();

    /**
     * 审批意见
     */
    @Nullable
    @Column(name = Columns.approvalComment)
    String approvalComment();

    /**
     * 审批时间
     */
    @Nullable
    @Column(name = Columns.approvalTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime approvalTime();

    interface Columns {
        String approvalStatus = "approval_status"; // 审批状态
        String approverId = "approver_id"; // 审批人id
        String approverName = "approver_name"; // 审批人姓名
        String approvalComment = "approval_comment"; // 审批意见
        String approvalTime = "approval_time"; // 审批时间
    }

    interface FieldNames {
        String approvalStatus = "approvalStatus"; // 审批状态
        String approverId = "approverId"; // 审批人id
        String approverName = "approverName"; // 审批人姓名
        String approvalComment = "approvalComment"; // 审批意见
        String approvalTime = "approvalTime"; // 审批时间
    }


}
