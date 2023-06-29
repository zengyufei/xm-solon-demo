package com.xunmo.webs.user.controller;

import com.xunmo.common.base.BaseController;
import com.xunmo.common.entity.ResponseEntity;
import com.xunmo.common.entity.page.Page;
import com.xunmo.common.entity.page.PageRequest;
import com.xunmo.common.enums.SystemStatus;
import com.xunmo.common.utils.ResponseUtil;
import com.xunmo.webs.user.entity.User;
import com.xunmo.webs.user.entity.UserFetcher;
import com.xunmo.webs.user.entity.UserTable;
import com.xunmo.webs.user.input.UserInput;
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
 * 用户表(User)表控制层
 *
 * @author zengyufei
 * @since 2023-06-28 10:23:13
 */
@Slf4j
@Valid
@Controller
@Mapping("/user")
public class UserController extends BaseController {

    private final static UserTable TABLE = UserTable.$;
    private final static UserFetcher FETCHER = UserFetcher.$;

    /**
     * 服务对象
     */
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
    public ResponseEntity<Page<User>> list(@Validated @Body UserInput input, @Param PageRequest pageRequest) throws Exception {
        final LocalDateTime beginCreateTime = input.getBeginCreateTime();
        final LocalDateTime endCreateTime = input.getEndCreateTime();
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, pager(pageRequest)
                .execute(sqlClient
                        .createQuery(TABLE)
                        .whereIf(
                                beginCreateTime != null,
                                () -> TABLE.createTime().ge(beginCreateTime)
                        )
                        .whereIf(
                                endCreateTime != null,
                                () -> TABLE.createTime().le(endCreateTime)
                        )
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
    public ResponseEntity<User> update(@Validated UserInput input) throws Exception {
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
        return ResponseUtil.genResponse(SystemStatus.IS_SUCCESS, this.sqlClient.deleteByIds(User.class, ids));
    }

}
