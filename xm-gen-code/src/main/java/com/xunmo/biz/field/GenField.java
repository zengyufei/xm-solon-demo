package com.xunmo.biz.field;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GenField implements Serializable  {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long genTableId;

	private String tableName;
	private String columnName;
	private String columnComment;
	private String swaggerComment;
	private String columnType;
	private String javaType;
	private String javaField;
	private String tsType;
	private Long sort;
	private String pkFlag;
	private String incFlag;
	private String mustFlag;
	private String logicDeleteFlag;
	private String versionFlag;
	private String fillType;
	private String addFlag;
	private String editFlag;
	private String listFlag;
	private String queryFlag;
	private String defaultValue;
	private String isNullable;
	private String dataType;
	private Long length;
	private String width;
	private String component;
	private String dictType;
	private String queryType;
	private String enumStr;
	private String editHelpMessage;
	private String listHelpMessage;
	private String numericPrecision;
	private String numericScale;
	private String columnKey;
	private String extra;
	private Date createTime;
	private Date updateTime;


}
