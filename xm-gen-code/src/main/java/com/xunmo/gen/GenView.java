package com.xunmo.gen;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenView implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String name;
	private String fullPath;
	private String parentDir;
	private String suffix;
	private String fileName;
	private String language;
	private String code;




}
