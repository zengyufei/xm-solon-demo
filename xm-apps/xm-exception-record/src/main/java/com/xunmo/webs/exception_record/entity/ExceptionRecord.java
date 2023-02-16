package com.xunmo.webs.exception_record.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xunmo.base.XmEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 异常记录表
 * @author zengyufei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_exception_record")
public class ExceptionRecord extends XmEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 请求地址
     */
    private String uri;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * IP
     */
    private String ip;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 发生时间
     */
    private Long happenTime;

    /**
     * 异常堆栈消息
     */
    private String stackTrace;

}
