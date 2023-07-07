package com.xunmo.webs.user.controller;


import cn.hutool.core.util.StrUtil;
import com.xunmo.common.base.BaseController;
import com.xunmo.common.entity.ResponseEntity;
import com.xunmo.common.entity.page.Page;
import com.xunmo.common.entity.page.PageRequest;
import com.xunmo.common.enums.SystemStatus;
import com.xunmo.common.utils.ResponseUtil;
import com.xunmo.webs.organization.entity.OrganizationFetcher;
import com.xunmo.webs.permission.entity.PermissionFetcher;
import com.xunmo.webs.role.entity.RoleFetcher;
import com.xunmo.webs.user.entity.User;
import com.xunmo.webs.user.entity.UserFetcher;
import com.xunmo.webs.user.entity.UserTable;
import com.xunmo.webs.user.input.UserInput;
import com.xunmo.webs.user.query.UserQuery;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.DeleteResult;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.noear.solon.annotation.*;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户表(User)表控制层
 *
 * @author zengyufei
 * @since 2023-06-29 11:07:50
 */
@Slf4j
@Valid
@Controller
@Mapping("/user")
public class UserController extends BaseController {

    private final static UserTable TABLE = UserTable.$;
    private final static UserFetcher FETCHER = UserFetcher.$;

    @Inject
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
    public ResponseEntity<Page<User>> list(@Validated @Body UserQuery query, @Param PageRequest pageRequest) throws Exception {
        final String        userId          = query.getUserId();
        final String        userName        = query.getUserName();
        final LocalDateTime beginCreateTime = query.getBeginCreateTime();
        final LocalDateTime endCreateTime   = query.getEndCreateTime();
        final String        orgId           = query.getOrgId();
        final String        orgName         = query.getOrgName();
        final String        roleId          = query.getRoleId();
        final String        roleName        = query.getRoleName();
        final String        permissionId    = query.getPermissionId();
        final String        permissionName  = query.getPermissionName();
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, pager(pageRequest)
                .execute(sqlClient
                        .createQuery(TABLE)
                        // 根据 用户id 查询
                        .whereIf(
                                StrUtil.isNotBlank(userId),
                                () -> TABLE.userId().eq(userId)
                        )
                        // 根据 用户名称 模糊查询
                        .whereIf(
                                StrUtil.isNotBlank(userName),
                                () -> TABLE.userName().like(userName)
                        )
                        // 根据 组织机构id 查询
                        .whereIf(
                                StrUtil.isNotBlank(orgId),
                                () -> TABLE.organization().organizationId().eq(orgId)
                        )
                        // 根据 组织机构名称 模糊查询
                        .whereIf(
                                StrUtil.isNotBlank(orgName),
                                () -> TABLE.organization().organizationName().like(orgName)
                        )
                        // 根据 角色id 查询
                        .whereIf(
                                StrUtil.isNotBlank(roleId),
                                () -> TABLE.asTableEx().roles().roleId().eq(roleId)
                        )
                        // 根据 角色名称 模糊查询
                        .whereIf(
                                StrUtil.isNotBlank(roleName),
                                () -> TABLE.asTableEx().roles().roleName().like(roleName)
                        )
                        // 根据 权限id 查询
                        .whereIf(
                                StrUtil.isNotBlank(permissionId),
                                () -> TABLE.asTableEx().roles().permissions().permissionId().like(permissionId)
                        )
                        // 根据 权限名称 模糊查询
                        .whereIf(
                                StrUtil.isNotBlank(permissionName),
                                () -> TABLE.asTableEx().roles().permissions().permissionName().like(permissionName)
                        )
                        // 根据 创建时间 大于等于查询
                        .whereIf(
                                beginCreateTime != null,
                                () -> TABLE.createTime().ge(beginCreateTime)
                        )
                        // 根据 创建时间 小于等于查询
                        .whereIf(
                                endCreateTime != null,
                                () -> TABLE.createTime().le(endCreateTime)
                        )
                        // 默认排序 创建时间 倒排
                        .orderBy(
                                TABLE.createTime().desc()
                        )
                        .select(
                                TABLE.fetch(
                                        // 查询 用户表 所有属性（非对象）
                                        FETCHER.allScalarFields()
                                                // 查询 创建者 对象，只显示 姓名
                                                .create(UserFetcher.$
                                                        .userName())
                                                // 查询 修改者 对象，只显示 姓名
                                                .update(UserFetcher.$
                                                        .userName())
                                                // 查询 组织机构者 对象，只显示 姓名
                                                .organization(OrganizationFetcher.$
                                                        .organizationName())
                                                .roles(RoleFetcher.$
                                                        .parentRoleId()
                                                        .roleName()
                                                        .permissions(PermissionFetcher.$
                                                                .permissionName()))
                                )
                        )
                ));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Post
    @Mapping("/getById")
    public ResponseEntity<User> getById(@NotNull @NotBlank String id) throws Exception {
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.findById(User.class, id));
    }


    /**
     * 新增数据
     *
     * @param input 实体
     * @return 新增结果
     */
    @Post
    @Mapping("/add")
    public ResponseEntity<User> add(@Validated UserInput input) throws Exception {
        final SimpleSaveResult<User> result = this.sqlClient.save(input);
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, result.getModifiedEntity());
    }

    /**
     * 编辑数据
     *
     * @param input 实体
     * @return 编辑结果
     */
    @Post
    @Mapping("/update")
    public ResponseEntity<User> update(@Validated UserInput input) throws Exception {
        final SimpleSaveResult<User> result = this.sqlClient.update(input);
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, result.getModifiedEntity());
    }

    /**
     * 删除数据
     *
     * @param ids 主键集合
     * @return 删除是否成功
     */
    @Post
    @Mapping("/deleteByIds")
    @Tran
    public ResponseEntity<Boolean> deleteByIds(List<String> ids) throws Exception {
        final DeleteResult result = this.sqlClient.deleteByIds(User.class, ids);
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, result.getTotalAffectedRowCount());
    }


}
