package com.xunmo.base;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.toolkit.GenericTypeUtil;
import com.xunmo.XmConstants;
import com.xunmo.utils.AjaxJson;
import com.xunmo.utils.XmPageUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

//这个注解可继承，用于支持子类的验证
//
@Slf4j
@Valid
public class XmSuperControllerImpl<M extends XmService<E>, E> implements XmSuperController<E> {

    @Inject("${page.pageNumName}")
    private String pageNumName;

    @Inject("${page.pageSizeName}")
    private String pageSizeName;

    @Inject
    protected M baseService;

    protected Class<M> currentServiceClass() {
        return (Class<M>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImpl.class, 0);
    }

    protected Class<E> currentModelClass() {
        return (Class<E>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImpl.class, 1);
    }

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
    @Override
    public AjaxJson add(@Validated E criteria, Context ctx) throws Exception {
        if (ObjectUtil.isNull(criteria)) {
            return AjaxJson.getSuccess();
        } else {
            baseService.save(criteria);
        }
        // 设置常用数据
        return AjaxJson.getSuccess();
    }

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
    @Override
    public AjaxJson remove(String id, Context ctx) throws Exception {
        UpdateWrapper<E> updateWrapper = new UpdateWrapper<E>()
                .set(XmConstants.column_disabled, XmConstants.DISABLED)
                .set(XmConstants.column_last_update_time, DateUtil.date())
                .eq(XmConstants.column_id, id);
        return AjaxJson.getSuccessData(baseService.update(updateWrapper));
    }


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
    @Override
    public AjaxJson query(E criteria, Context ctx) throws Exception {
        QueryWrapper<E> queryWrapper;
        if (ObjectUtil.isNull(criteria)) {
            queryWrapper = Wrappers.emptyWrapper();
        } else {
            queryWrapper = Wrappers.query(criteria);
        }
        return queryWrapper.isEmptyOfEntity() ? null : AjaxJson.getSuccessData(baseService.getOne(queryWrapper));
    }


    @Override
    public AjaxJson count(@Validated E criteria, Context ctx) throws Exception {
        QueryWrapper<E> queryWrapper;
        if (ObjectUtil.isNull(criteria)) {
            queryWrapper = Wrappers.emptyWrapper();
        } else {
            queryWrapper = Wrappers.query(criteria);
        }
        return AjaxJson.getSuccessData(baseService.count(queryWrapper));
    }

    /**
     * 根据条件查询多条数据(不区分是否为当前用户创建)
     *
     * @param criteria
     * @return
     * @throws Exception
     * @author : LinDexing
     * @date : 2017/11/3 0003 11:32
     * @phone : 13570978853
     * @e-mail : 1148506799@qq.com
     */
    @Override
    public AjaxJson list(@Validated E criteria, Context ctx) throws Exception {
        QueryWrapper<E> queryWrapper;
        if (ObjectUtil.isNull(criteria)) {
            queryWrapper = Wrappers.emptyWrapper();
        } else {
            queryWrapper = Wrappers.query(criteria);
        }
        final boolean addOrderBy = XmPageUtil.addOrderBy(queryWrapper);
        if (!addOrderBy) {
            queryWrapper.orderByAsc(XmConstants.column_create_time);
        }
        return AjaxJson.getSuccessData(XmPageUtil.startPage(() -> baseService.list(queryWrapper)));
    }

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
    @Override
    public AjaxJson updateById(E criteria, String id, Context ctx) throws Exception {
        return AjaxJson.getSuccessData(baseService.updateNotNullById(criteria));
    }

}
