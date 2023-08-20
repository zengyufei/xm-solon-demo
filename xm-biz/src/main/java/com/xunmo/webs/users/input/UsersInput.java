package com.xunmo.webs.users.input;

import com.xunmo.webs.users.entity.Users;
import lombok.Data;
import org.babyfish.jimmer.Input;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 用户表(User)输入类
 *
 * @author zengyufei
 * @since 2023-06-29 11:07:50
 */
@Data
public class UsersInput implements Input<Users> {

	private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 用户名
	 */
	private String userName;

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
	 * 创建人用户名
	 */
	private String createName;

	/**
	 * 修改人用户名
	 */
	private String updateName;

	/**
	 * 审批状态
	 */
	private String approvalStatus;

	/**
	 * 审批人id
	 */
	private String approverId;

	/**
	 * 审批人姓名
	 */
	private String approverName;

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
	public Users toEntity() {
		return CONVERTER.toUsers(this);
	}

	@Mapper
	interface Converter {

		@BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
		Users toUsers(UsersInput input);

	}

}
