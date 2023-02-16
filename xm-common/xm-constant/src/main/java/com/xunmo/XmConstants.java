package com.xunmo;

public interface XmConstants {

	String REQ_ID = "reqId";


	String default_app_id = "1";
	String default_tenant_id = "1";
	String default_create_user = "admin";
	String default_create_user_name = "admin";

	//数据库记录状态：0-启用 1-删除
	Integer ENABLED = 0;
	Integer DISABLED = 1;

	// 公共实体字段，在 CommonFieldMetaObjectHandler 内使用
	String field_id = "id";
	String field_app_id = "appId";
	String field_tenant_id = "tenantId";
	String field_create_user = "createUser";
	String field_create_time = "createTime";
	String field_create_user_name = "createUserName";
	String field_disabled = "disabled";
	String field_last_update_time = "lastUpdateTime";
	String field_last_update_user = "lastUpdateUser";
	String field_last_update_user_name = "lastUpdateUserName";

	// 数据库公共字段
	String column_id = "id";
	String column_appid = "app_id";
	String column_disabled = "disabled";
	String column_create_user = "create_user";
	String column_create_time = "create_time";
	String column_create_user_name = "create_user_name";
	String column_last_update_user = "last_update_user";
	String column_last_update_time = "last_update_time";
	String column_last_update_user_name = "last_update_user_name";
}
