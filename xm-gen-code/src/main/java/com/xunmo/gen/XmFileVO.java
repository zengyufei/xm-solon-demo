package com.xunmo.gen;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class XmFileVO implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<?> children;

}
