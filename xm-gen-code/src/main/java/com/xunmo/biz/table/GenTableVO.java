package com.xunmo.biz.table;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenTableVO implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String name;
	private Long dataBaseId;

	private Long genDataId;

	private List<GenTable> tables;

}
