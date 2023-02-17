package com.xunmo.second.webs.user.controller;

import com.xunmo.base.XmSuperControllerImpl;
import com.xunmo.second.webs.user.dto.UserDelDTO;
import com.xunmo.second.webs.user.dto.UserGetDTO;
import com.xunmo.second.webs.user.dto.UserGetPageDTO;
import com.xunmo.second.webs.user.dto.UserSaveDTO;
import com.xunmo.second.webs.user.dto.UserUpdateDTO;
import com.xunmo.second.webs.user.entity.User;
import com.xunmo.second.webs.user.service.UserService;
import com.xunmo.utils.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Validated;

@Slf4j
@Controller
@Mapping("/user")
public class UserController extends XmSuperControllerImpl<UserService, User>  {

    @Post
    @Mapping("/get")
    public AjaxJson get(@Validated UserGetDTO userGetDTO) throws Exception {
        final User dser = baseService.getById(userGetDTO.getId());
        return AjaxJson.getSuccessData(dser);
    }

    @Post
    @Mapping("/list")
    public AjaxJson list(@Validated UserGetPageDTO userGetPageDTO) throws Exception {
        baseService.getList(userGetPageDTO);
        return AjaxJson.getPageData();
    }

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/add")
    public AjaxJson add(@Validated UserSaveDTO userSaveDTO) throws Exception {
        final User dser = userSaveDTO.toEntity();
        baseService.save(dser);
        return AjaxJson.getSuccessData(dser);
    }

    @Post
    @Mapping("/del")
    public AjaxJson del(@Validated UserDelDTO userDelDTO) throws Exception {
        baseService.removeById(userDelDTO.getId());
        return AjaxJson.getSuccess();
    }

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/update")
    public AjaxJson update(@Validated UserUpdateDTO userUpdateDTO) throws Exception {
        baseService.updateNotNullById(userUpdateDTO.toEntity());
        return AjaxJson.getSuccess();
    }


}
