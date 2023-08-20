package com.xunmo.webs.role.entity;

import com.xunmo.common.base.BaseEntity;
import com.xunmo.common.base.ProcessEntity;
import com.xunmo.common.base.TenantEntity;
import com.xunmo.common.base.VersionEntity;
import com.xunmo.config.jimmer.SnowflakeIdGenerator;
import com.xunmo.webs.permission.entity.Permission;
import com.xunmo.webs.users.entity.Users;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色表(Role)实体类
 *
 * @author zengyufei
 * @since 2023-06-29 14:51:29
 */
@Entity
@Table(name = Role.TABLE_NAME)
public interface Role extends BaseEntity, ProcessEntity, TenantEntity, VersionEntity {

	/**
	 * 表名
	 */
	String TABLE_NAME = "role";

	/**
	 * 角色ID
	 */
	@Id
	@GeneratedValue(generatorType = SnowflakeIdGenerator.class)
	String roleId();

	/**
	 * 角色名称
	 */
	@Nullable
	@Column(name = Columns.roleName)
	String roleName();

	// ---------- 表中字段 ----------

	/**
	 * 父角色ID
	 */
	@Nullable
	@Column(name = Columns.parentRoleId)
	String parentRoleId();

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

	@ManyToMany(mappedBy = "roles")
	List<Users> users();

	@OneToMany(mappedBy = "role")
	List<Permission> permissions();

	/**
	 * 表字段名
	 */
	interface Columns {

		String roleId = "role_id"; // 角色ID

		String roleName = "role_name"; // 角色名称

		String parentRoleId = "parent_role_id"; // 父角色ID

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

		String roleId = "roleId"; // 角色ID

		String roleName = "roleName"; // 角色名称

		String parentRoleId = "parentRoleId"; // 父角色ID

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
