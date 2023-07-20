package com.xunmo.webs.organization.controller;

import com.xunmo.common.base.BaseController;
import com.xunmo.common.entity.ResponseEntity;
import com.xunmo.common.utils.ResponseUtil;
import com.xunmo.enums.SystemStatus;
import com.xunmo.jimmer.annotation.Db;
import com.xunmo.jimmer.page.Page;
import com.xunmo.jimmer.page.PageRequest;
import com.xunmo.webs.organization.entity.Organization;
import com.xunmo.webs.organization.entity.OrganizationFetcher;
import com.xunmo.webs.organization.entity.OrganizationTable;
import com.xunmo.webs.organization.input.OrganizationInput;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织表(Organization)表控制层
 *
 * @author zengyufei
 * @since 2023-06-29 13:37:41
 */
@Slf4j
@Valid
@Controller
@Mapping("/organization")
public class OrganizationController extends BaseController {

	private final static OrganizationTable TABLE = OrganizationTable.$;

	private final static OrganizationFetcher FETCHER = OrganizationFetcher.$;

	@Db
	private JSqlClient sqlClient;

	/**
	 * 分页查询
	 *
	 * @param input       筛选条件
	 * @param pageRequest 分页对象
	 * @return 查询结果
	 */
	@Post
	@Mapping("/list")
	public ResponseEntity<Page<Organization>> list(@Validated @Body OrganizationInput input,
												   @Param PageRequest pageRequest) throws Exception {
		final LocalDateTime beginCreateTime = input.getBeginCreateTime();
		final LocalDateTime endCreateTime = input.getEndCreateTime();
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS,
				pager(pageRequest).execute(sqlClient.createQuery(TABLE)
						.whereIf(beginCreateTime != null, () -> TABLE.createTime().ge(beginCreateTime))
						.whereIf(endCreateTime != null, () -> TABLE.createTime().le(endCreateTime))
						.select(TABLE.fetch(FETCHER.allScalarFields()))));
	}

	/**
	 * 通过主键查询单条数据
	 *
	 * @param id 主键
	 * @return 单条数据
	 */
	@Post
	@Mapping("/getById")
	public ResponseEntity<Organization> getById(@NotNull @NotBlank String id) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.findById(Organization.class, id));
	}

	/**
	 * 新增数据
	 *
	 * @param input 实体
	 * @return 新增结果
	 */
	@Post
	@Mapping("/add")
	public ResponseEntity<Organization> add(@Validated OrganizationInput input) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.save(input));
	}

	/**
	 * 编辑数据
	 *
	 * @param input 实体
	 * @return 编辑结果
	 */
	@Post
	@Mapping("/update")
	public ResponseEntity<Organization> update(@Validated OrganizationInput input) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.update(input));
	}

	/**
	 * 删除数据
	 *
	 * @param ids 主键集合
	 * @return 删除是否成功
	 */
	@Post
	@Mapping("/deleteByIds")
	public ResponseEntity<Boolean> deleteByIds(List<String> ids) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.deleteByIds(Organization.class, ids));
	}

}
