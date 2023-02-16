package com.xunmo.biz.table;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TableInfo implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String tableName;

	private String tableComment;

	private String engine;

	private Date createTime;



}
