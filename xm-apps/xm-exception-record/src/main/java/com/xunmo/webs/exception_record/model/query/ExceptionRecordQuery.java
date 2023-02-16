package com.xunmo.webs.exception_record.model.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zengyufei
 * @date 2021/9/28 14:29
 */
@Data
@Accessors(chain = true)
public class ExceptionRecordQuery {

    /**
     * 请求地址
     */
    private String uri;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
