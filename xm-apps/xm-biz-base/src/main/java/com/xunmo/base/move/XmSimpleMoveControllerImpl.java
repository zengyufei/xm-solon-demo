package com.xunmo.base.move;

import com.xunmo.base.XmController;
import com.xunmo.base.XmIdEntity;
import com.xunmo.base.validationGroups.MoveValid;
import com.xunmo.utils.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

//这个注解可继承，用于支持子类的验证
//
@Slf4j
@Valid
public class XmSimpleMoveControllerImpl<M extends XmSimpleMoveService<T>, T> extends XmController<M, T> {

    @Post
    @Mapping("/upTop")
    public AjaxJson upTop(@Validated(MoveValid.class) XmIdEntity baseIdEntity) throws Exception {
        return AjaxJson.getSuccessData(service.upTop(baseIdEntity.getId()));
    }

    @Post
    @Mapping("/upMove")
    public AjaxJson upMove(@Validated(MoveValid.class) XmIdEntity baseIdEntity) throws Exception {
        return AjaxJson.getSuccessData(service.upMove(baseIdEntity.getId()));
    }

    @Post
    @Mapping("/downMove")
    public AjaxJson downMove(@Validated(MoveValid.class) XmIdEntity baseIdEntity) throws Exception {
        return AjaxJson.getSuccessData(service.downMove(baseIdEntity.getId()));
    }

    @NotBlank(value = {"preId", "nextId"}, message = "preId 和 nextId 均不能为空")
    @NotNull(value = {"preId", "nextId"}, message = "preId 和 nextId 均不能为 null")
    @Post
    @Mapping("/changeSort")
    public AjaxJson changeSort(final String preId, final String nextId) throws Exception {
        return AjaxJson.getSuccessData(service.changeSort(preId, nextId));
    }

//    @Post
//    @Mapping("/changeSort")
//    public AjaxJson changeSort(@Validated XmChangeSortEntity xmChangeSortEntity) throws Exception {
//        return AjaxJson.getSuccessData(service.changeSort(xmChangeSortEntity.getPreId(), xmChangeSortEntity.getNextId()));
//    }

}
