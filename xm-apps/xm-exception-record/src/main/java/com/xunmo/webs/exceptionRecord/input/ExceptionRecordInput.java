package com.xunmo.webs.exceptionRecord.input;

import com.xunmo.webs.exceptionRecord.entity.ExceptionRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.babyfish.jimmer.Input;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 异常记录表(ExceptionRecord)输入类
 *
 * @author zengyufei
 * @since 2023-07-07 07:02:16
 */
@Data
@Accessors
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
public class ExceptionRecordInput implements Input<ExceptionRecord>, Serializable {

	private static final long serialVersionUID = -4185267394446332219L;

	private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

	/**
	 * [PK]用户ID
	 */
	private String id;

	/**
	 * 请求地址
	 */
	private String uri;

	/**
	 * 请求方法
	 */
	private String method;

	/**
	 * 请求参数
	 */
	private String params;

	/**
	 * IP
	 */
	private String ip;

	/**
	 * 日志追踪id
	 */
	private String reqid;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 发生时间(时间戳)
	 */
	private LocalDateTime happenTime;

	private LocalDateTime beginHappenTime;

	private LocalDateTime endHappenTime;

	/**
	 * 异常堆栈消息
	 */
	private String stackTrace;

	/**
	 * 是否有效:0-有效 1-禁用
	 */
	private Integer disabled;

	/**
	 * 租户ID
	 */
	private String tenantId;

	/**
	 * appId
	 */
	private String appId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	private LocalDateTime beginCreateTime;

	private LocalDateTime endCreateTime;

	/**
	 * 创建人
	 */
	private String createUser;

	/**
	 * 创建人昵称
	 */
	private String createUserName;

	/**
	 * 更新时间
	 */
	private LocalDateTime lastUpdateTime;

	private LocalDateTime beginLastUpdateTime;

	private LocalDateTime endLastUpdateTime;

	/**
	 * 更新人
	 */
	private String lastUpdateUser;

	/**
	 * 更新人创建人昵称
	 */
	private String lastUpdateUserName;

	/**
	 * 数据来源
	 */
	private String sourceType;

	/**
	 * 说明
	 */
	private String remark;

	// ---------- 转换方法 ----------

	@Override
	public ExceptionRecord toEntity() {
		return CONVERTER.toExceptionRecord(this);
	}

	public ExceptionRecordInput toUpdate() {
		return CONVERTER.toUpdate(this);
	}

	@Mapper(imports = { LocalDateTime.class })
	interface Converter {

		@BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
		ExceptionRecord toExceptionRecord(ExceptionRecordInput input);

		@Mapping(target = ExceptionRecord.FieldNames.createUser, constant = "system")
		@Mapping(target = ExceptionRecord.FieldNames.createUserName, constant = "system")
		@Mapping(target = ExceptionRecord.FieldNames.createTime, expression = "java(LocalDateTime.now())")
		@Mapping(target = ExceptionRecord.FieldNames.disabled, constant = "0")
		@Mapping(target = ExceptionRecord.FieldNames.lastUpdateUser, constant = "system")
		@Mapping(target = ExceptionRecord.FieldNames.lastUpdateUserName, constant = "system")
		@Mapping(target = ExceptionRecord.FieldNames.lastUpdateTime, expression = "java(LocalDateTime.now())")
		@Mapping(target = ExceptionRecord.FieldNames.sourceType, constant = "system")
		@Mapping(target = ExceptionRecord.FieldNames.tenantId, constant = "-1")
		@Mapping(target = ExceptionRecord.FieldNames.appId, constant = "-1")
		ExceptionRecordInput toUpdate(ExceptionRecordInput input);

	}

}
