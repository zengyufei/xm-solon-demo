package com.xunmo.webs.exception_record.service;


import com.xunmo.base.XmService;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import com.xunmo.webs.exception_record.model.query.ExceptionRecordQuery;

import java.util.List;

public interface ExceptionRecordService extends XmService<ExceptionRecord> {

    /**
     * 查询异常记录
     *
     * @param query 参数
     * @return 结果
     */
    List<ExceptionRecord> getList(ExceptionRecordQuery query);

    /**
     * 删除时间之前的数据
     *
     * @param time 时间
     * @return 结果
     */
    boolean delBeforeTime(Long time);

}
