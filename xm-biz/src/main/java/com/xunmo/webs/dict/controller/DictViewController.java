package com.xunmo.webs.dict.controller;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.validation.annotation.Valid;

@Slf4j
@Valid
@Mapping("/dict/view")
@Controller
public class DictViewController {

    @Mapping("/list")
    public ModelAndView list() throws Exception {
        ModelAndView model = new ModelAndView("dict-list.ftl");
        model.put("title", "字典列表页");
        return model;
    }
    @Mapping("/tree")
    public ModelAndView tree() throws Exception {
        ModelAndView model = new ModelAndView("dict-tree.ftl");
        model.put("title", "字典树页");
        return model;
    }

}
