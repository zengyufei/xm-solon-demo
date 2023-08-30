package com.xunmo.webs.exceptionRecord.entity;

import com.xunmo.common.base.BaseEntity;
import com.xunmo.common.base.ProcessEntity;
import com.xunmo.common.base.TenantEntity;
import com.xunmo.common.base.VersionEntity;
import com.xunmo.config.jimmer.SnowflakeIdGenerator;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * 异常记录表(ExceptionRecord)实体类
 *
 * @author zengyufei
 * @since 2023-07-06 23:11:28
 */
@Entity
@Table(name = ExceptionRecord.TABLE_NAME)
public interface ExceptionRecord extends BaseEntity, ProcessEntity, TenantEntity, VersionEntity {

	/**
	 * 表名
	 */
	String TABLE_NAME = "t_exception_record";

	/**
	 * 表字段名
	 */
	interface Columns {

		String id = "id"; // [PK]用户ID

		String uri = "uri"; // 请求地址

		String method = "method"; // 请求方法

		String params = "params"; // 请求参数

		String ip = "ip"; // IP

		String reqid = "reqId"; // 日志追踪id

		String usersId = "user_id"; // 用户ID

		String happenTime = "happen_time"; // 发生时间(时间戳)

		String stackTrace = "stack_trace"; // 异常堆栈消息

		String disabled = "disabled"; // 是否有效:0-有效 1-禁用

		String tenantId = "tenant_id"; // 租户ID

		String appId = "app_id"; // appId

		String createTime = "create_time"; // 创建时间

		String createUser = "create_user"; // 创建人

		String createUserName = "create_user_name"; // 创建人昵称

		String lastUpdateTime = "last_update_time"; // 更新时间

		String lastUpdateUser = "last_update_user"; // 更新人

		String lastUpdateUserName = "last_update_user_name"; // 更新人创建人昵称

		String sourceType = "source_type"; // 数据来源

		String remark = "remark"; // 说明

	}

	/**
	 * 实体字段名
	 */
	interface FieldNames {

		String id = "id"; // [PK]用户ID

		String uri = "uri"; // 请求地址

		String method = "method"; // 请求方法

		String params = "params"; // 请求参数

		String ip = "ip"; // IP

		String reqid = "reqid"; // 日志追踪id

		String usersId = "usersId"; // 用户ID

		String happenTime = "happenTime"; // 发生时间(时间戳)

		String stackTrace = "stackTrace"; // 异常堆栈消息

		String disabled = "disabled"; // 是否有效:0-有效 1-禁用

		String tenantId = "tenantId"; // 租户ID

		String appId = "appId"; // appId

		String createTime = "createTime"; // 创建时间

		String createUser = "createUser"; // 创建人

		String createUserName = "createUserName"; // 创建人昵称

		String lastUpdateTime = "lastUpdateTime"; // 更新时间

		String lastUpdateUser = "lastUpdateUser"; // 更新人

		String lastUpdateUserName = "lastUpdateUserName"; // 更新人创建人昵称

		String sourceType = "sourceType"; // 数据来源

		String remark = "remark"; // 说明

	}

	// ---------- 表中字段 ----------

	/**
	 * [PK]用户ID
	 */
	@Id
	@GeneratedValue(generatorType = SnowflakeIdGenerator.class)
	String id();

	/**
	 * 请求地址
	 */
	@Nullable
	@Column(name = Columns.uri)
	String uri();

	/**
	 * 请求方法
	 */
	@Nullable
	@Column(name = Columns.method)
	String method();

	/**
	 * 请求参数
	 */
	@Nullable
	@Column(name = Columns.params)
	String params();

	/**
	 * IP
	 */
	@Nullable
	@Column(name = Columns.ip)
	String ip();

	/**
	 * 日志追踪id
	 */
	@Nullable
	@Column(name = Columns.reqid)
	String reqid();

	/**
	 * 用户ID
	 */
	@Nullable
	@Column(name = Columns.usersId)
	String usersId();

	/**
	 * 发生时间(时间戳)
	 */
	@Nullable
	@Column(name = Columns.happenTime)
	LocalDateTime happenTime();

	/**
	 * 异常堆栈消息
	 */
	@Nullable
	@Column(name = Columns.stackTrace)
	String stackTrace();

	/**
	 * 是否有效:0-有效 1-禁用
	 */
	@Nullable
	@Column(name = Columns.disabled)
	Integer disabled();

	/**
	 * appId
	 */
	@Nullable
	@Column(name = Columns.appId)
	String appId();

	/**
	 * 创建人
	 */
	@Nullable
	@Column(name = Columns.createUser)
	String createUser();

	/**
	 * 创建人昵称
	 */
	@Nullable
	@Column(name = Columns.createUserName)
	String createUserName();

	/**
	 * 更新时间
	 */
	@Nullable
	@Column(name = Columns.lastUpdateTime)
	LocalDateTime lastUpdateTime();

	/**
	 * 更新人
	 */
	@Nullable
	@Column(name = Columns.lastUpdateUser)
	String lastUpdateUser();

	/**
	 * 更新人创建人昵称
	 */
	@Nullable
	@Column(name = Columns.lastUpdateUserName)
	String lastUpdateUserName();

	/**
	 * 数据来源
	 */
	@Nullable
	@Column(name = Columns.sourceType)
	String sourceType();

	/**
	 * 说明
	 */
	@Nullable
	@Column(name = Columns.remark)
	String remark();

}
