package com.xunmo;

public interface XmConstants {

	String REQ_ID = "reqId";

	String default_app_id = "-1";

	String default_tenant_id = "-1";

	String default_create_id = "system";

	String default_create_name = "system";

	String default_create_time = "1991-01-12 01:01:01";

	String default_source_type = "system";

	// 数据库记录状态：0-启用 1-删除
	Integer ENABLED = 0;

	Integer DISABLED = 1;

	// 公共实体字段
	String field_id = "id";

	String field_app_id = "appId";

	String field_tenant_id = "tenantId";

	String field_disabled = "disabled";

	String field_create_id = "create_id"; // 创建人ID

	String field_update_id = "update_id"; // 修改人ID

	String field_create_name = "create_name"; // 创建人姓名

	String field_update_name = "update_name"; // 修改人姓名

	String field_create_time = "create_time"; // 创建时间

	String field_update_time = "update_time"; // 修改时间

	// 数据库公共字段
	String column_id = "id";

	String column_appid = "app_id";

	String column_tenant_id = "tenant_id";

	String column_disabled = "disabled";

	String column_create_id = "create_id"; // 创建人ID

	String column_update_id = "update_id"; // 修改人ID

	String column_create_name = "create_name"; // 创建人姓名

	String column_update_name = "update_name"; // 修改人姓名

	String column_create_time = "create_time"; // 创建时间

	String column_update_time = "update_time"; // 修改时间

}
