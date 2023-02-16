package com.xunmo.base;

import com.xunmo.utils.AjaxJson;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Delete;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Put;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * Created by LinDexing on 2017/11/2 0002.
 */
@Controller
public interface XmSuperController<E> {

    /**
     * 新增一条数据
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:29
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Post
    @Mapping(value = "/")
    AjaxJson add(@Validated E criteria, Context ctx) throws Exception;

    /**
     * 根据id逻辑删除一条数据
     *
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:30
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Delete
    @Mapping(value = "{id}")
    AjaxJson remove(String id, Context ctx) throws Exception;


    /**
     * 根据 id 查询一条数据 (不区分是否为当前用户创建)
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:31
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Get
    @Mapping(value = "/")
    AjaxJson<E> query(E criteria, Context ctx) throws Exception;


    /**
     * 根据条件查询多条数据
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:32
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Get
    @Mapping(value = "list")
    AjaxJson<List<E>> list(@Validated E criteria, Context ctx) throws Exception;

    /**
     * 查询数目
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:32
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Get
    @Mapping(value = "count")
    AjaxJson count(@Validated E criteria, Context ctx) throws Exception;


    /**
     * 根据主键更新
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:34
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Put
    @Mapping(value = "{id}")
    AjaxJson updateById(E criteria, String id, Context ctx) throws Exception;

}
