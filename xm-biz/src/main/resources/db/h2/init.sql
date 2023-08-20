
-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`(
  `organization_id` varchar(20)  NOT NULL COMMENT '组织ID',
  `organization_name` varchar(100)  NULL DEFAULT NULL COMMENT '组织名称',
  `parent_organization_id` varchar(20)  NULL DEFAULT NULL COMMENT '父组织ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_id` varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
  `approval_status` varchar(100)  NULL DEFAULT NULL COMMENT '审批状态',
  `approver_id` varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
  `approval_comment` varchar(100)  NULL DEFAULT NULL COMMENT '审批意见',
  `approval_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  `is_imported` tinyint(1) NULL DEFAULT NULL COMMENT '是否导入',
  `import_time` datetime(0) NULL DEFAULT NULL COMMENT '导入时间',
  `is_system_default` tinyint(1) NULL DEFAULT NULL COMMENT '是否系统默认',
  `tenant_id` varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
  `version` int(0) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `status` varchar(100)  NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`organization_id`)
)  COMMENT = '组织表' ;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `users_id` varchar(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) NULL DEFAULT NULL COMMENT '用户名',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `create_id` varchar(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_id` varchar(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `approval_status` varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
  `approver_id` varchar(20) NULL DEFAULT NULL COMMENT '审批人id',
  `approval_comment` varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
  `approval_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  `is_imported` tinyint(1) NULL DEFAULT NULL COMMENT '是否导入',
  `import_time` datetime(0) NULL DEFAULT NULL COMMENT '导入时间',
  `is_system_default` tinyint(1) NULL DEFAULT NULL COMMENT '是否系统默认',
  `tenant_id` varchar(20) NULL DEFAULT NULL COMMENT '租户id',
  `version` int(0) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `status` varchar(100) NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`users_id`),
  INDEX `idx_users_create_time`(`create_time`)
) COMMENT = '用户表';

-- ----------------------------
-- Table structure for users_organization
-- ----------------------------
DROP TABLE IF EXISTS `users_organization`;
CREATE TABLE `users_organization`  (
  `users_id` varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
  `organization_id` varchar(20) NULL DEFAULT NULL COMMENT '组织ID',
  INDEX `idx_users_organization_users_id`(`users_id`),
  INDEX `idx_users_organization_organization_id`(`organization_id`)
) COMMENT = '用户-组织关联表';
