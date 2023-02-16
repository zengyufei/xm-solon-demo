package com.xunmo.webs.department.controller;

import com.xunmo.utils.AjaxJson;
import com.xunmo.webs.department.dto.DepartmentDelDTO;
import com.xunmo.webs.department.dto.DepartmentGetDTO;
import com.xunmo.webs.department.dto.DepartmentGetPageDTO;
import com.xunmo.webs.department.dto.DepartmentSaveDTO;
import com.xunmo.webs.department.dto.DepartmentUpdateDTO;
import com.xunmo.webs.department.entity.Department;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Valid
@Controller
public interface IDepartmentController {

    @Post
    @Mapping("/get")
    AjaxJson get(@Validated DepartmentGetDTO departmentGetDTO) throws Exception;

    @Post
    @Mapping("/getTree")
    AjaxJson getTree(@Validated Department department) throws Exception;

    @Post
    @Mapping("/list")
    AjaxJson list(@Validated DepartmentGetPageDTO departmentGetPageDTO) throws Exception;

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/add")
    AjaxJson add(@Validated DepartmentSaveDTO departmentSaveDTO) throws Exception;

    @Post
    @Mapping("/del")
    AjaxJson del(@Validated DepartmentDelDTO departmentDelDTO) throws Exception;

    //    @NoRepeatSubmit  //重复提交验证
    @Post
    @Mapping("/update")
    AjaxJson update(@Validated DepartmentUpdateDTO departmentUpdateDTO) throws Exception;
}
