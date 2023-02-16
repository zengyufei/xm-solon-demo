package com.xunmo.webs.exception_record.mapper;

import com.xunmo.base.XmMapper;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import org.apache.ibatis.annotations.Param;

public interface ExceptionRecordMapper extends XmMapper<ExceptionRecord> {

    /**
     * 删除时间之前的数据
     *
     * @param time 时间
     * @return 结果
     */
    int delBeforeTime(@Param("time") Long time);

}
