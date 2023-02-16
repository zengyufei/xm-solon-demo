package com.xunmo.biz.kv;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenKv implements Serializable  {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long kvId;
	private String key;
	private Long dataId;
	private Long tableId;
	private String source;
	private String type;
	private String label;
	private String value;

}
