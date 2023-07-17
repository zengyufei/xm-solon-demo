package com.xunmo.jimmer;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * 用户表(User)实体类
 *
 * @author zengyufei
 * @since 2023-06-29 11:07:51
 */
@Entity
@Table(name = User.TABLE_NAME)
public interface User {

	/**
	 * 表名
	 */
	String TABLE_NAME = "user";

	// ---------- 表中字段 ----------

	/**
	 * 用户ID
	 */
	@Id
	String userId();

	/**
	 * 用户名
	 */
	@Nullable
	@Column(name = Columns.userName)
	String userName();

	/**
	 * 是否导入
	 */
	@Nullable
	@Column(name = Columns.isImported)
	Integer isImported();

	/**
	 * 导入时间
	 */
	@Nullable
	@Column(name = Columns.importTime)
	LocalDateTime importTime();

	/**
	 * 是否系统默认
	 */
	@Nullable
	@Column(name = Columns.isSystemDefault)
	Integer isSystemDefault();

	/**
	 * 状态
	 */
	@Nullable
	@Column(name = Columns.status)
	String status();

	/**
	 * 表字段名
	 */
	interface Columns {

		String userId = "user_id"; // 用户ID

		String userName = "user_name"; // 用户名

		String createTime = "create_time"; // 创建时间

		String updateTime = "update_time"; // 修改时间

		String createId = "create_id"; // 创建人ID

		String updateId = "update_id"; // 修改人ID

		String createName = "create_name"; // 创建人用户名

		String updateName = "update_name"; // 修改人用户名

		String approvalStatus = "approval_status"; // 审批状态

		String approverId = "approver_id"; // 审批人id

		String approverName = "approver_name"; // 审批人姓名

		String approvalComment = "approval_comment"; // 审批意见

		String approvalTime = "approval_time"; // 审批时间

		String isImported = "is_imported"; // 是否导入

		String importTime = "import_time"; // 导入时间

		String isSystemDefault = "is_system_default"; // 是否系统默认

		String tenantId = "tenant_id"; // 租户id

		String version = "version"; // 乐观锁版本号

		String status = "status"; // 状态

	}

	/**
	 * 实体字段名
	 */
	interface FieldNames {

		String userId = "userId"; // 用户ID

		String userName = "userName"; // 用户名

		String createTime = "createTime"; // 创建时间

		String updateTime = "updateTime"; // 修改时间

		String createId = "createId"; // 创建人ID

		String updateId = "updateId"; // 修改人ID

		String createName = "createName"; // 创建人用户名

		String updateName = "updateName"; // 修改人用户名

		String approvalStatus = "approvalStatus"; // 审批状态

		String approverId = "approverId"; // 审批人id

		String approverName = "approverName"; // 审批人姓名

		String approvalComment = "approvalComment"; // 审批意见

		String approvalTime = "approvalTime"; // 审批时间

		String isImported = "isImported"; // 是否导入

		String importTime = "importTime"; // 导入时间

		String isSystemDefault = "isSystemDefault"; // 是否系统默认

		String tenantId = "tenantId"; // 租户id

		String version = "version"; // 乐观锁版本号

		String status = "status"; // 状态

	}

}
