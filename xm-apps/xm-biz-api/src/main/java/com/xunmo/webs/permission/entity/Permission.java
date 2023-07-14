package com.xunmo.webs.permission.entity;

import com.xunmo.common.base.BaseEntity;
import com.xunmo.common.base.ProcessEntity;
import com.xunmo.common.base.TenantEntity;
import com.xunmo.common.base.VersionEntity;
import com.xunmo.config.jimmer.SnowflakeIdGenerator;
import com.xunmo.webs.role.entity.Role;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * 权限表(Permission)实体类
 *
 * @author zengyufei
 * @since 2023-06-29 15:28:09
 */
@Entity
@Table(name = Permission.TABLE_NAME)
public interface Permission extends BaseEntity, ProcessEntity, TenantEntity, VersionEntity {

	/**
	 * 表名
	 */
	String TABLE_NAME = "permission";

	/**
	 * 权限ID
	 */
	@Id
	@GeneratedValue(generatorType = SnowflakeIdGenerator.class)
	String permissionId();

	/**
	 * 权限名称
	 */
	@Nullable
	@Column(name = Columns.permissionName)
	String permissionName();

	// ---------- 表中字段 ----------

	/**
	 * 权限类型
	 */
	@Nullable
	@Column(name = Columns.permissionType)
	String permissionType();

	/**
	 * 父权限ID
	 */
	@Nullable
	@Column(name = Columns.parentPermissionId)
	String parentPermissionId();

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

	@ManyToOne
	@JoinTable(name = "role_permission")
	@Nullable
	Role role();

	/**
	 * 表字段名
	 */
	interface Columns {

		String permissionId = "permission_id"; // 权限ID

		String permissionName = "permission_name"; // 权限名称

		String permissionType = "permission_type"; // 权限类型

		String parentPermissionId = "parent_permission_id"; // 父权限ID

		String createTime = "create_time"; // 创建时间

		String updateTime = "update_time"; // 修改时间

		String createId = "create_id"; // 创建人ID

		String updateId = "update_id"; // 修改人ID

		String approvalStatus = "approval_status"; // 审批状态

		String approverId = "approver_id"; // 审批人id

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

		String permissionId = "permissionId"; // 权限ID

		String permissionName = "permissionName"; // 权限名称

		String permissionType = "permissionType"; // 权限类型

		String parentPermissionId = "parentPermissionId"; // 父权限ID

		String createTime = "createTime"; // 创建时间

		String updateTime = "updateTime"; // 修改时间

		String createId = "createId"; // 创建人ID

		String updateId = "updateId"; // 修改人ID

		String approvalStatus = "approvalStatus"; // 审批状态

		String approverId = "approverId"; // 审批人id

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
