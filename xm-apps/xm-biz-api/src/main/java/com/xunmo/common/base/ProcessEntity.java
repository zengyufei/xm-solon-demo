package com.xunmo.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xunmo.enums.ApprovalStatus;
import com.xunmo.webs.users.entity.Users;
import org.babyfish.jimmer.sql.*;
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
	ApprovalStatus approvalStatus();

	/**
	 * 审批人id
	 */
	@IdView
	@Nullable
	String approverId();

	@ManyToOne
	@Nullable
	@JoinColumn(name = Columns.approverId)
	Users approver();

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

		String approvalComment = "approval_comment"; // 审批意见

		String approvalTime = "approval_time"; // 审批时间

	}

	interface FieldNames {

		String approvalStatus = "approvalStatus"; // 审批状态

		String approverId = "approverId"; // 审批人id

		String approvalComment = "approvalComment"; // 审批意见

		String approvalTime = "approvalTime"; // 审批时间

	}

}
