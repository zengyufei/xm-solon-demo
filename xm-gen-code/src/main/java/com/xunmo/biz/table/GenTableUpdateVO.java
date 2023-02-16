package com.xunmo.biz.table;

import com.xunmo.biz.kv.GenKv;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenTableUpdateVO implements Serializable  {

	private static final long serialVersionUID = 1L;

	private GenTable genTable;

	private List<GenKv> genKvList;

}
