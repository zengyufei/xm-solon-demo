package com.xunmo.gen;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class XmFile implements Serializable  {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String parentDirs;
	private String parentDir;
	private String fullPath;
	private List<String> paths;

	private String firstDirName;
	private String secondDirName;
	private String threeDirName;




}
