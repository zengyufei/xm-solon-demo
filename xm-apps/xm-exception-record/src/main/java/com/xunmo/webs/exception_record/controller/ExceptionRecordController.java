package com.xunmo.webs.exception_record.controller;


import com.xunmo.base.XmSuperControllerImpl;
import com.xunmo.utils.AjaxJson;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import com.xunmo.webs.exception_record.model.query.ExceptionRecordQuery;
import com.xunmo.webs.exception_record.service.ExceptionRecordService;
import com.xunmo.webs.exception_record.service.impl.ExceptionRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.List;

/**
 * @author zengyufei
 * @date 2021/9/27 21:08
 */
@Slf4j
@Controller
@Mapping("/exceptionRecord")
public class ExceptionRecordController extends XmSuperControllerImpl<ExceptionRecordServiceImpl, ExceptionRecord> {

    @Inject
    private ExceptionRecordService exceptionRecordService;

    @Get
    @Mapping("/list")
    public AjaxJson<List<ExceptionRecord>> list(ExceptionRecordQuery query) {
        return AjaxJson.getSuccessData( exceptionRecordService.getList(query));
    }

}
