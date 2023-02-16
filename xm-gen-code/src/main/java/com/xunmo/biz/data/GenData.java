package com.xunmo.biz.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GenData implements Serializable  {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	private Date createTime;
	private String authorName;
	private String tablePrefuix;

	private String serviceName;
	private String packageName;
	private String moduleName;
	private String subPackageName;
	private String entitySuperClassName;
	private String mapperSuperClassName;
	private String serviceSuperClassName;
	private String serviceImplSuperClassName;

	private String dsFlag;
	private String tenderFlag;
	private String lombokFlag;
	private String chainFlag;
	private String constFlag;
	private String genFlag;
	private String prefixPath;
	private String suffixPath;
	private String remark;

	private String appPackageName;
	private String appModuleName;
	private String appPopupType;
	private String appTemplateType;
	private String appAddBtnFlag;
	private String appUpdateBtnFlag;
	private String appDelBtnFlag;
	private String appCopyBtnFlag;
	private String appShowBtnFlag;

	private String appAddBtnCode;
	private String appUpdateBtnCode;
	private String appDelBtnCode;
	private String appCopyBtnCode;
	private String appShowBtnCode;

	private Long parentSystemId;
	private Long parentMenuId;
	private String menuIcon;

	private String parentIdName;
	private String slaveTalbeName;
	private String slaveFieldName;




}
