package com.xunmo.webs.permission.controller;

import com.xunmo.common.base.BaseController;
import com.xunmo.common.entity.ResponseEntity;
import com.xunmo.common.entity.page.Page;
import com.xunmo.common.entity.page.PageRequest;
import com.xunmo.common.utils.ResponseUtil;
import com.xunmo.enums.SystemStatus;
import com.xunmo.webs.permission.entity.Permission;
import com.xunmo.webs.permission.entity.PermissionFetcher;
import com.xunmo.webs.permission.entity.PermissionTable;
import com.xunmo.webs.permission.input.PermissionInput;
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
 * 权限表(Permission)表控制层
 *
 * @author zengyufei
 * @since 2023-06-29 15:28:08
 */
@Slf4j
@Valid
@Controller
@Mapping("/permission")
public class PermissionController extends BaseController {

	private final static PermissionTable TABLE = PermissionTable.$;

	private final static PermissionFetcher FETCHER = PermissionFetcher.$;

	@Inject
	private JSqlClient sqlClient;

	/**
	 * 分页查询
	 * @param input 筛选条件
	 * @param pageRequest 分页对象
	 * @return 查询结果
	 */
	@Post
	@Mapping("/list")
	public ResponseEntity<Page<Permission>> list(@Validated @Body PermissionInput input, @Param PageRequest pageRequest)
			throws Exception {
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
	 * @param id 主键
	 * @return 单条数据
	 */
	@Post
	@Mapping("/getById")
	public ResponseEntity<Permission> getById(@NotNull @NotBlank String id) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.findById(Permission.class, id));
	}

	/**
	 * 新增数据
	 * @param input 实体
	 * @return 新增结果
	 */
	@Post
	@Mapping("/add")
	public ResponseEntity<Permission> add(@Validated PermissionInput input) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.save(input));
	}

	/**
	 * 编辑数据
	 * @param input 实体
	 * @return 编辑结果
	 */
	@Post
	@Mapping("/update")
	public ResponseEntity<Permission> update(@Validated PermissionInput input) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.update(input));
	}

	/**
	 * 删除数据
	 * @param ids 主键集合
	 * @return 删除是否成功
	 */
	@Post
	@Mapping("/deleteByIds")
	public ResponseEntity<Boolean> deleteByIds(List<String> ids) throws Exception {
		return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.deleteByIds(Permission.class, ids));
	}

}
