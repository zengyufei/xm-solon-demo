SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_exception_record
-- ----------------------------
-- DROP TABLE IF EXISTS `t_exception_record`;
CREATE TABLE IF NOT EXISTS `t_exception_record`
(
    `id`                    varchar(36) NOT NULL COMMENT '[PK]用户ID',
    `uri`                   varchar(2000) DEFAULT NULL COMMENT '请求地址',
    `method`                varchar(10)   DEFAULT NULL COMMENT '请求方法',
    `params`                longtext COMMENT '请求参数',
    `ip`                    varchar(50)   DEFAULT NULL COMMENT 'IP',
    `reqId`                 varchar(36)   DEFAULT NULL COMMENT '日志追踪id',
    `user_id`               varchar(36)   DEFAULT NULL COMMENT '用户ID',
    `happen_time`           datetime      DEFAULT NULL COMMENT '发生时间',
    `stack_trace`           longtext COMMENT '异常堆栈消息',
    `disabled`              tinyint(1)    DEFAULT '0' COMMENT '是否有效:0-有效 1-禁用',
    `tenant_id`             varchar(36)   DEFAULT NULL COMMENT '租户ID',
    `app_id`                varchar(100)  DEFAULT NULL COMMENT 'appId',
    `create_time`           datetime      DEFAULT NULL COMMENT '创建时间',
    `create_user`           varchar(36)   DEFAULT NULL COMMENT '创建人',
    `create_user_name`      varchar(255)  DEFAULT NULL COMMENT '创建人昵称',
    `last_update_time`      datetime      DEFAULT NULL COMMENT '更新时间',
    `last_update_user`      varchar(36)   DEFAULT NULL COMMENT '更新人',
    `last_update_user_name` varchar(255)  DEFAULT NULL COMMENT '更新人创建人昵称',
    `source_type`           varchar(50)   DEFAULT NULL COMMENT '数据来源',
    `remark`                varchar(255)  DEFAULT NULL COMMENT '说明',
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = utf8mb4

 COMMENT ='异常记录表';

-- ----------------------------
-- Table structure for organization
-- ----------------------------
-- DROP TABLE IF EXISTS `organization`;
CREATE TABLE IF NOT EXISTS `organization`
(
    `organization_id`        varchar(20)  NOT NULL COMMENT '组织ID',
    `organization_name`      varchar(100) NULL DEFAULT NULL COMMENT '组织名称',
    `parent_organization_id` varchar(20)  NULL DEFAULT NULL COMMENT '父组织ID',
    `create_time`            datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`            datetime(0)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    `create_id`              varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
    `update_id`              varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
    `approval_status`        varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
    `approver_id`            varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
    `approval_comment`       varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
    `approval_time`          datetime(0)  NULL DEFAULT NULL COMMENT '审批时间',
    `is_imported`            tinyint(1)   NULL DEFAULT NULL COMMENT '是否导入',
    `import_time`            datetime(0)  NULL DEFAULT NULL COMMENT '导入时间',
    `is_system_default`      tinyint(1)   NULL DEFAULT NULL COMMENT '是否系统默认',
    `tenant_id`              varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
    `version`                int       NULL DEFAULT NULL COMMENT '乐观锁版本号',
    `status`                 varchar(100) NULL DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`organization_id`),
    INDEX `parent_organization_id` (`parent_organization_id`),
    CONSTRAINT `organization_ibfk_1` FOREIGN KEY (`parent_organization_id`) REFERENCES `organization` (`organization_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
 COMMENT = '组织表'
;


-- ----------------------------
-- Table structure for user
-- ----------------------------
-- DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`           varchar(20)  NOT NULL COMMENT '用户ID',
    `user_name`         varchar(100) NULL DEFAULT NULL COMMENT '用户名',
    `create_time`       datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`       datetime(0)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    `create_id`         varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
    `update_id`         varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
    `approval_status`   varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
    `approver_id`       varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
    `approval_comment`  varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
    `approval_time`     datetime(0)  NULL DEFAULT NULL COMMENT '审批时间',
    `is_imported`       tinyint(1)   NULL DEFAULT NULL COMMENT '是否导入',
    `import_time`       datetime(0)  NULL DEFAULT NULL COMMENT '导入时间',
    `is_system_default` tinyint(1)   NULL DEFAULT NULL COMMENT '是否系统默认',
    `tenant_id`         varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
    `version`           int       NULL DEFAULT NULL COMMENT '乐观锁版本号',
    `status`            varchar(100) NULL DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`user_id`),
    INDEX `idx_user_create_time` (`create_time`)
) ENGINE = InnoDB

 COMMENT = '用户表'
;

-- ----------------------------
-- Table structure for role
-- ----------------------------
-- DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role`
(
    `role_id`           varchar(20)  NOT NULL COMMENT '角色ID',
    `role_name`         varchar(100) NULL DEFAULT NULL COMMENT '角色名称',
    `parent_role_id`    varchar(20)  NULL DEFAULT NULL COMMENT '父角色ID',
    `create_time`       datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`       datetime(0)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    `create_id`         varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
    `update_id`         varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
    `approval_status`   varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
    `approver_id`       varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
    `approval_comment`  varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
    `approval_time`     datetime(0)  NULL DEFAULT NULL COMMENT '审批时间',
    `is_imported`       tinyint(1)   NULL DEFAULT NULL COMMENT '是否导入',
    `import_time`       datetime(0)  NULL DEFAULT NULL COMMENT '导入时间',
    `is_system_default` tinyint(1)   NULL DEFAULT NULL COMMENT '是否系统默认',
    `tenant_id`         varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
    `version`           int       NULL DEFAULT NULL COMMENT '乐观锁版本号',
    `status`            varchar(100) NULL DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`role_id`),
    INDEX `parent_role_id` (`parent_role_id`),
    CONSTRAINT `role_ibfk_1` FOREIGN KEY (`parent_role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '角色表'
;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
-- DROP TABLE IF EXISTS `permission`;
CREATE TABLE IF NOT EXISTS `permission`
(
    `permission_id`        varchar(20)  NOT NULL COMMENT '权限ID',
    `permission_name`      varchar(100) NULL DEFAULT NULL COMMENT '权限名称',
    `permission_type`      varchar(100) NULL DEFAULT NULL COMMENT '权限类型',
    `parent_permission_id` varchar(20)  NULL DEFAULT NULL COMMENT '父权限ID',
    `create_time`          datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`          datetime(0)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    `create_id`            varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
    `update_id`            varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
    `approval_status`      varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
    `approver_id`          varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
    `approval_comment`     varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
    `approval_time`        datetime(0)  NULL DEFAULT NULL COMMENT '审批时间',
    `is_imported`          tinyint(1)   NULL DEFAULT NULL COMMENT '是否导入',
    `import_time`          datetime(0)  NULL DEFAULT NULL COMMENT '导入时间',
    `is_system_default`    tinyint(1)   NULL DEFAULT NULL COMMENT '是否系统默认',
    `tenant_id`            varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
    `version`              int      NULL DEFAULT NULL COMMENT '乐观锁版本号',
    `status`               varchar(100) NULL DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`permission_id`),
    INDEX `parent_permission_id` (`parent_permission_id`),
    CONSTRAINT `permission_ibfk_1` FOREIGN KEY (`parent_permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '权限表'
;






-- ----------------------------
-- Table structure for user_organization
-- ----------------------------
-- DROP TABLE IF EXISTS `user_organization`;
CREATE TABLE IF NOT EXISTS `user_organization`
(
    `user_id`         varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
    `organization_id` varchar(20) NULL DEFAULT NULL COMMENT '组织ID',
    INDEX `idx_user_organization_user_id` (`user_id`),
    INDEX `idx_user_organization_organization_id` (`organization_id`),
    CONSTRAINT `user_organization_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `user_organization_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`organization_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '用户-组织关联表'
;


-- ----------------------------
-- Table structure for user_role
-- ----------------------------
-- DROP TABLE IF EXISTS `user_role`;
CREATE TABLE IF NOT EXISTS `user_role`
(
    `user_id` varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
    `role_id` varchar(20) NULL DEFAULT NULL COMMENT '角色ID',
    INDEX `idx_user_role_user_id` (`user_id`),
    INDEX `idx_user_role_role_id` (`role_id`),
    CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '用户组-角色关联表'
;




-- ----------------------------
-- Table structure for role_mutex
-- ----------------------------
-- DROP TABLE IF EXISTS `role_mutex`;
CREATE TABLE IF NOT EXISTS `role_mutex`
(
    `role_id1`          varchar(20)  NULL DEFAULT NULL COMMENT '角色ID1',
    `role_id2`          varchar(20)  NULL DEFAULT NULL COMMENT '角色ID2',
    `mutex_description` varchar(100) NULL DEFAULT NULL COMMENT '互斥描述',
    INDEX `role_id1` (`role_id1`),
    INDEX `role_id2` (`role_id2`),
    CONSTRAINT `role_mutex_ibfk_1` FOREIGN KEY (`role_id1`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `role_mutex_ibfk_2` FOREIGN KEY (`role_id2`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '角色互斥表'
;

-- ----------------------------
-- Records of role_mutex
-- ----------------------------

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
-- DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE IF NOT EXISTS `role_permission`
(
    `role_id`       varchar(20) NULL DEFAULT NULL COMMENT '角色ID',
    `permission_id` varchar(20) NULL DEFAULT NULL COMMENT '权限ID',
    INDEX `idx_role_permission_role_id` (`role_id`),
    INDEX `idx_role_permission_permission_id` (`permission_id`),
    CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '角色-权限关联表'
;







-- ----------------------------
-- Table structure for organization_role
-- ----------------------------
-- DROP TABLE IF EXISTS `organization_role`;
CREATE TABLE IF NOT EXISTS `organization_role`
(
    `role_id`         varchar(20) NULL DEFAULT NULL COMMENT '角色ID',
    `organization_id` varchar(20) NULL DEFAULT NULL COMMENT '组织ID',
    INDEX `idx_organization_role_role_id` (`role_id`),
    INDEX `idx_organization_role_organization_id` (`organization_id`),
    CONSTRAINT `organization_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `organization_role_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`organization_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '组织-角色关联表'
;


-- ----------------------------
-- Table structure for user_group
-- ----------------------------
-- DROP TABLE IF EXISTS `user_group`;
CREATE TABLE IF NOT EXISTS `user_group`
(
    `user_group_id`     varchar(20)  NOT NULL COMMENT '用户组ID',
    `user_group_name`   varchar(100) NULL DEFAULT NULL COMMENT '用户组名称',
    `create_time`       datetime(0)  NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`       datetime(0)  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    `create_id`         varchar(20)  NULL DEFAULT NULL COMMENT '创建人ID',
    `update_id`         varchar(20)  NULL DEFAULT NULL COMMENT '修改人ID',
    `approval_status`   varchar(100) NULL DEFAULT NULL COMMENT '审批状态',
    `approver_id`       varchar(20)  NULL DEFAULT NULL COMMENT '审批人id',
    `approval_comment`  varchar(100) NULL DEFAULT NULL COMMENT '审批意见',
    `approval_time`     datetime(0)  NULL DEFAULT NULL COMMENT '审批时间',
    `is_imported`       tinyint(1)   NULL DEFAULT NULL COMMENT '是否导入',
    `import_time`       datetime(0)  NULL DEFAULT NULL COMMENT '导入时间',
    `is_system_default` tinyint(1)   NULL DEFAULT NULL COMMENT '是否系统默认',
    `tenant_id`         varchar(20)  NULL DEFAULT NULL COMMENT '租户id',
    `version`           int       NULL DEFAULT NULL COMMENT '乐观锁版本号',
    `status`            varchar(100) NULL DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`user_group_id`)
) ENGINE = InnoDB

 COMMENT = '用户组表'
;


-- ----------------------------
-- Table structure for user_group_role
-- ----------------------------
-- DROP TABLE IF EXISTS `user_group_role`;
CREATE TABLE IF NOT EXISTS `user_group_role`
(
    `role_id`       varchar(20) NULL DEFAULT NULL COMMENT '角色ID',
    `user_group_id` varchar(20) NULL DEFAULT NULL COMMENT '用户组ID',
    INDEX `idx_user_group_role_role_id` (`role_id`),
    INDEX `idx_user_group_role_user_group_id` (`user_group_id`),
    CONSTRAINT `user_group_role_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `user_group_role_ibfk_2` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`user_group_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB


;


-- ----------------------------
-- Table structure for user_user_group
-- ----------------------------
-- DROP TABLE IF EXISTS `user_user_group`;
CREATE TABLE IF NOT EXISTS `user_user_group`
(
    `user_id`       varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
    `user_group_id` varchar(20) NULL DEFAULT NULL COMMENT '用户组ID',
    INDEX `user_id` (`user_id`),
    INDEX `user_group_id` (`user_group_id`),
    CONSTRAINT `user_user_group_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `user_user_group_ibfk_2` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`user_group_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB

 COMMENT = '用户-用户组关联表'
;


SET FOREIGN_KEY_CHECKS = 1;
