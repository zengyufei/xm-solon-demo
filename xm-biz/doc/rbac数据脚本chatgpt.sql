-- 删除所有表
DROP TABLE IF EXISTS user_user_group;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_organization;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_group_role;
DROP TABLE IF EXISTS user_group;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS role_mutex;
DROP TABLE IF EXISTS organization_role;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS organization;

-- 用户表
CREATE TABLE user (
  user_id VARCHAR(20) PRIMARY KEY COMMENT '用户ID',
  user_name VARCHAR(100) COMMENT '用户名',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  create_id VARCHAR(20) COMMENT '创建人ID',
  update_id VARCHAR(20) COMMENT '修改人ID',
  approval_status VARCHAR(100) COMMENT '审批状态',
  approver_id VARCHAR(20) COMMENT '审批人id',
  approval_comment VARCHAR(100) COMMENT '审批意见',
  approval_time DATETIME COMMENT '审批时间',
  is_imported BOOLEAN COMMENT '是否导入',
  import_time DATETIME COMMENT '导入时间',
  is_system_default BOOLEAN COMMENT '是否系统默认',
  tenant_id varchar(20) COMMENT '租户id',
  version int COMMENT '乐观锁版本号',
  status VARCHAR(100) COMMENT '状态'
) COMMENT='用户表';;

-- 组织表
CREATE TABLE organization (
  organization_id VARCHAR(20) PRIMARY KEY COMMENT '组织ID',
  organization_name VARCHAR(100) COMMENT '组织名称',
  parent_organization_id VARCHAR(20) COMMENT '父组织ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  create_id VARCHAR(20) COMMENT '创建人ID',
  update_id VARCHAR(20) COMMENT '修改人ID',
  approval_status VARCHAR(100) COMMENT '审批状态',
  approver_id VARCHAR(20) COMMENT '审批人id',
  approval_comment VARCHAR(100) COMMENT '审批意见',
  approval_time DATETIME COMMENT '审批时间',
  is_imported BOOLEAN COMMENT '是否导入',
  import_time DATETIME COMMENT '导入时间',
  is_system_default BOOLEAN COMMENT '是否系统默认',
  tenant_id varchar(20) COMMENT '租户id',
  version int COMMENT '乐观锁版本号',
  status VARCHAR(100) COMMENT '状态',
  FOREIGN KEY (parent_organization_id) REFERENCES organization(organization_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='组织表';

-- 用户-组织关联表
CREATE TABLE user_organization (
  user_id VARCHAR(20) COMMENT '用户ID',
  organization_id VARCHAR(20) COMMENT '组织ID',
  FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (organization_id) REFERENCES organization(organization_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='用户-组织关联表';

-- 角色表
CREATE TABLE role (
  role_id VARCHAR(20) PRIMARY KEY COMMENT '角色ID',
  role_name VARCHAR(100) COMMENT '角色名称',
  parent_role_id VARCHAR(20) COMMENT '父角色ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  create_id VARCHAR(20) COMMENT '创建人ID',
  update_id VARCHAR(20) COMMENT '修改人ID',
  approval_status VARCHAR(100) COMMENT '审批状态',
  approver_id VARCHAR(20) COMMENT '审批人id',
  approval_comment VARCHAR(100) COMMENT '审批意见',
  approval_time DATETIME COMMENT '审批时间',
  is_imported BOOLEAN COMMENT '是否导入',
  import_time DATETIME COMMENT '导入时间',
  is_system_default BOOLEAN COMMENT '是否系统默认',
  tenant_id varchar(20) COMMENT '租户id',
  version int COMMENT '乐观锁版本号',
  status VARCHAR(100) COMMENT '状态',
  FOREIGN KEY (parent_role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='角色表';

-- 组织-角色关联表
CREATE TABLE organization_role (
  role_id VARCHAR(20) COMMENT '角色ID',
  organization_id VARCHAR(20) COMMENT '组织ID',
  FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (organization_id) REFERENCES organization(organization_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='组织-角色关联表';

-- 角色互斥表
CREATE TABLE role_mutex (
  role_id1 VARCHAR(20) COMMENT '角色ID1',
  role_id2 VARCHAR(20) COMMENT '角色ID2',
  mutex_description VARCHAR(100) COMMENT '互斥描述',
  FOREIGN KEY (role_id1) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (role_id2) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='角色互斥表';

-- 权限表
CREATE TABLE permission (
  permission_id VARCHAR(20) PRIMARY KEY COMMENT '权限ID',
  permission_name VARCHAR(100) COMMENT '权限名称',
  permission_type VARCHAR(100) COMMENT '权限类型',
  parent_permission_id VARCHAR(20) COMMENT '父权限ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  create_id VARCHAR(20) COMMENT '创建人ID',
  update_id VARCHAR(20) COMMENT '修改人ID',
  approval_status VARCHAR(100) COMMENT '审批状态',
  approver_id VARCHAR(20) COMMENT '审批人id',
  approval_comment VARCHAR(100) COMMENT '审批意见',
  approval_time DATETIME COMMENT '审批时间',
  is_imported BOOLEAN COMMENT '是否导入',
  import_time DATETIME COMMENT '导入时间',
  is_system_default BOOLEAN COMMENT '是否系统默认',
  tenant_id varchar(20) COMMENT '租户id',
  version int COMMENT '乐观锁版本号',
  status VARCHAR(100) COMMENT '状态',
  FOREIGN KEY (parent_permission_id) REFERENCES permission(permission_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='权限表';

-- 角色-权限关联表
CREATE TABLE role_permission (
  role_id VARCHAR(20) COMMENT '角色ID',
  permission_id VARCHAR(20) COMMENT '权限ID',
  FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (permission_id) REFERENCES permission(permission_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='角色-权限关联表';

-- 用户组表
CREATE TABLE user_group (
  user_group_id VARCHAR(20) PRIMARY KEY COMMENT '用户组ID', 
  user_group_name VARCHAR(100) COMMENT '用户组名称',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  create_id VARCHAR(20) COMMENT '创建人ID',
  update_id VARCHAR(20) COMMENT '修改人ID',
  approval_status VARCHAR(100) COMMENT '审批状态',
  approver_id VARCHAR(20) COMMENT '审批人id',
  approval_comment VARCHAR(100) COMMENT '审批意见',
  approval_time DATETIME COMMENT '审批时间',
  is_imported BOOLEAN COMMENT '是否导入',
  import_time DATETIME COMMENT '导入时间',
  is_system_default BOOLEAN COMMENT '是否系统默认',
  tenant_id varchar(20) COMMENT '租户id',
  version int COMMENT '乐观锁版本号',
  status VARCHAR(100) COMMENT '状态'
) COMMENT='用户组表';

-- 用户-用户组关联表
CREATE TABLE user_user_group (
  user_id VARCHAR(20) COMMENT '用户ID',
  user_group_id VARCHAR(20) COMMENT '用户组ID',
  FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (user_group_id) REFERENCES user_group(user_group_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='用户-用户组关联表';

-- 用户组-角色关联表
CREATE TABLE user_group_role (
  role_id VARCHAR(20) COMMENT '角色ID',
  user_group_id VARCHAR(20) COMMENT '用户组ID',
  FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (user_group_id) REFERENCES user_group(user_group_id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- 用户-角色关联表
CREATE TABLE user_role (
  user_id VARCHAR(20) COMMENT '用户ID',
  role_id VARCHAR(20) COMMENT '角色ID',
  FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE ON UPDATE NO ACTION
) COMMENT='用户组-角色关联表';

-- 优化索引
ALTER TABLE user ADD INDEX idx_user_create_time (create_time);
ALTER TABLE user_organization ADD INDEX idx_user_organization_user_id (user_id);
ALTER TABLE user_organization ADD INDEX idx_user_organization_organization_id (organization_id);
ALTER TABLE organization_role ADD INDEX idx_organization_role_role_id (role_id);
ALTER TABLE organization_role ADD INDEX idx_organization_role_organization_id (organization_id);
ALTER TABLE role_permission ADD INDEX idx_role_permission_role_id (role_id);
ALTER TABLE role_permission ADD INDEX idx_role_permission_permission_id (permission_id);
ALTER TABLE user_group_role ADD INDEX idx_user_group_role_role_id (role_id);
ALTER TABLE user_group_role ADD INDEX idx_user_group_role_user_group_id (user_group_id);
ALTER TABLE user_role ADD INDEX idx_user_role_user_id (user_id);
ALTER TABLE user_role ADD INDEX idx_user_role_role_id (role_id);