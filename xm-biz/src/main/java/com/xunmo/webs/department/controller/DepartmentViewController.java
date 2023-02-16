package com.xunmo.webs.department.controller;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.validation.annotation.Valid;

@Slf4j
@Valid
@Mapping("/department/view")
@Controller
public class DepartmentViewController {

    @Mapping("/index")
    public ModelAndView list() throws Exception {
        ModelAndView model = new ModelAndView("department-list.ftl");
        model.put("title", "部门列表页");
        return model;
    }

}
