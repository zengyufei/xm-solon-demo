package com.xunmo.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应对象
 */
@Data
public class ResponseEntity<E> implements Serializable {

	private static final long serialVersionUID = 7752433261294797208L;

	/**
	 * 响应结果（true、false表示成功或失败）
	 */
	private boolean success;

	/**
	 * 用户可看的成功或失败的描述
	 */
	private String msg;

	/**
	 * code
	 */
	private int code;

	/**
	 * 响应的数据内容
	 */
	private E data;

	/**
	 * 错误的的字段
	 */
	private String field;

	/**
	 * 分页条件查询出的总数据数
	 */
	private Long total;

	/**
	 * 计算出来的总共页数
	 */
	private Integer totalPages;

	/**
	 * 当前页码
	 */
	private Integer pageNum;

	/**
	 * 页面分页大小
	 */
	private Integer pageSize;

	/**
	 * 日志追踪id
	 */
	private String reqId;

}
