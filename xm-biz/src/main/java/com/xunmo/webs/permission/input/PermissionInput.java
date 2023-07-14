package com.xunmo.webs.permission.input;

import com.xunmo.webs.permission.entity.Permission;
import lombok.Data;
import org.babyfish.jimmer.Input;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 权限表(Permission)输入类
 *
 * @author zengyufei
 * @since 2023-06-29 15:28:09
 */
@Data
public class PermissionInput implements Input<Permission> {

	private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

	/**
	 * 权限ID
	 */
	private String permissionId;

	/**
	 * 权限名称
	 */
	private String permissionName;

	/**
	 * 权限类型
	 */
	private String permissionType;

	/**
	 * 父权限ID
	 */
	private String parentPermissionId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	private LocalDateTime beginCreateTime;

	private LocalDateTime endCreateTime;

	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;

	private LocalDateTime beginUpdateTime;

	private LocalDateTime endUpdateTime;

	/**
	 * 创建人ID
	 */
	private String createId;

	/**
	 * 修改人ID
	 */
	private String updateId;

	/**
	 * 审批状态
	 */
	private String approvalStatus;

	/**
	 * 审批人id
	 */
	private String approverId;

	/**
	 * 审批意见
	 */
	private String approvalComment;

	/**
	 * 审批时间
	 */
	private LocalDateTime approvalTime;

	private LocalDateTime beginApprovalTime;

	private LocalDateTime endApprovalTime;

	/**
	 * 是否导入
	 */
	private Integer isImported;

	/**
	 * 导入时间
	 */
	private LocalDateTime importTime;

	private LocalDateTime beginImportTime;

	private LocalDateTime endImportTime;

	/**
	 * 是否系统默认
	 */
	private Integer isSystemDefault;

	/**
	 * 租户id
	 */
	private String tenantId;

	/**
	 * 乐观锁版本号
	 */
	private Integer version;

	/**
	 * 状态
	 */
	private String status;

	// ---------- 转换方法 ----------

	@Override
	public Permission toEntity() {
		return CONVERTER.toPermission(this);
	}

	@Mapper
	interface Converter {

		@BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
		Permission toPermission(PermissionInput input);

	}

}
