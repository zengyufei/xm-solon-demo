package com.xunmo.biz.data;

import com.xunmo.biz.kv.DataKv;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GenDataVO implements Serializable  {

	private static final long serialVersionUID = 1L;

	private List<DataKv> dataKvList;
	private GenData genData;

}
