package com.xunmo.webs.organization.input;

import com.xunmo.webs.organization.entity.Organization;
import lombok.Data;
import org.babyfish.jimmer.Input;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 组织表(Organization)输入类
 *
 * @author zengyufei
 * @since 2023-06-29 13:37:42
 */
@Data
public class OrganizationInput implements Input<Organization> {

	private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

	/**
	 * 组织ID
	 */
	private String organizationId;

	/**
	 * 组织名称
	 */
	private String organizationName;

	/**
	 * 父组织ID
	 */
	private String parentOrganizationId;

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
	public Organization toEntity() {
		return CONVERTER.toOrganization(this);
	}

	@Mapper
	interface Converter {

		@BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
		Organization toOrganization(OrganizationInput input);

	}

}
