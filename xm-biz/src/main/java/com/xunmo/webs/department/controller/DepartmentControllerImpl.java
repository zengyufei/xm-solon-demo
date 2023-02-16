package com.xunmo.webs.department.controller;

import com.xunmo.base.move.XmSimpleMoveControllerImpl;
import com.xunmo.utils.AjaxJson;
import com.xunmo.webs.department.dto.DepartmentDelDTO;
import com.xunmo.webs.department.dto.DepartmentGetDTO;
import com.xunmo.webs.department.dto.DepartmentGetPageDTO;
import com.xunmo.webs.department.dto.DepartmentSaveDTO;
import com.xunmo.webs.department.dto.DepartmentUpdateDTO;
import com.xunmo.webs.department.entity.Department;
import com.xunmo.webs.department.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Slf4j
@Valid
@Controller
@Mapping("/department")
public class DepartmentControllerImpl extends XmSimpleMoveControllerImpl<DepartmentService, Department> implements IDepartmentController{

    @Override
    public AjaxJson get(@Validated DepartmentGetDTO departmentGetDTO) throws Exception {
        final Department department = service.getById(departmentGetDTO.getId());
        return AjaxJson.getSuccessData(department);
    }

    @Override
    public AjaxJson getTree(@Validated Department department) throws Exception {
        return AjaxJson.getSuccessData(service.getTree(department));
    }

    @Override
    public AjaxJson list(@Validated DepartmentGetPageDTO departmentGetPageDTO) throws Exception {
        service.getList(departmentGetPageDTO);
        return AjaxJson.getPageData();
    }

    @Override
    public AjaxJson add(@Validated DepartmentSaveDTO departmentSaveDTO) throws Exception {
        final Department department = departmentSaveDTO.toEntity();
        service.add(department);
        return AjaxJson.getSuccessData(department);
    }

    @Override
    public AjaxJson del(@Validated DepartmentDelDTO departmentDelDTO) throws Exception {
        service.del(departmentDelDTO.getId());
        return AjaxJson.getSuccess();
    }

    @Override
    public AjaxJson update(@Validated DepartmentUpdateDTO departmentUpdateDTO) throws Exception {
        service.updateBean(departmentUpdateDTO);
        return AjaxJson.getSuccess();
    }
}
