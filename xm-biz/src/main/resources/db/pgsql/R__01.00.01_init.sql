-- ----------------------------
-- Table structure for t_exception_record
-- ----------------------------
-- DROP TABLE IF EXISTS `t_exception_record`;
CREATE TABLE IF NOT EXISTS t_exception_record
(
    id                    text NOT NULL,
    uri                   text DEFAULT NULL,
    method                text DEFAULT NULL,
    params                text,
    ip                    text DEFAULT NULL,
    reqId                 text DEFAULT NULL,
    user_id               text DEFAULT NULL,
    happen_time           text DEFAULT NULL,
    stack_trace           text,
    disabled              text DEFAULT '0',
    tenant_id             text DEFAULT NULL,
    app_id                text DEFAULT NULL,
    create_time           text DEFAULT NULL,
    create_user           text DEFAULT NULL,
    create_user_name      text DEFAULT NULL,
    last_update_time      text DEFAULT NULL,
    last_update_user      text DEFAULT NULL,
    last_update_user_name text DEFAULT NULL,
    source_type           text DEFAULT NULL,
    remark                text DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN t_exception_record.id IS '[PK]用户ID';
COMMENT ON COLUMN t_exception_record.uri IS '请求地址';
COMMENT ON COLUMN t_exception_record.method IS '请求方法';
COMMENT ON COLUMN t_exception_record.params IS '请求参数';
COMMENT ON COLUMN t_exception_record.ip IS 'IP';
COMMENT ON COLUMN t_exception_record.reqId IS '日志追踪id';
COMMENT ON COLUMN t_exception_record.user_id IS '用户ID';
COMMENT ON COLUMN t_exception_record.happen_time IS '发生时间';
COMMENT ON COLUMN t_exception_record.stack_trace IS '异常堆栈消息';
COMMENT ON COLUMN t_exception_record.disabled IS '是否有效:0-有效 1-禁用';
COMMENT ON COLUMN t_exception_record.tenant_id IS '租户ID';
COMMENT ON COLUMN t_exception_record.app_id IS 'appId';
COMMENT ON COLUMN t_exception_record.create_time IS '创建时间';
COMMENT ON COLUMN t_exception_record.create_user IS '创建人';
COMMENT ON COLUMN t_exception_record.create_user_name IS '创建人昵称';
COMMENT ON COLUMN t_exception_record.last_update_time IS '更新时间';
COMMENT ON COLUMN t_exception_record.last_update_user IS '更新人';
COMMENT ON COLUMN t_exception_record.last_update_user_name IS '更新人创建人昵称';
COMMENT ON COLUMN t_exception_record.source_type IS '数据来源';
COMMENT ON COLUMN t_exception_record.remark IS '说明';

COMMENT ON TABLE t_exception_record IS '异常记录表';

-- ----------------------------
-- Table structure for organization
-- ----------------------------
-- DROP TABLE IF EXISTS `organization`;
CREATE TABLE IF NOT EXISTS organization
(
    organization_id        text PRIMARY KEY,
    organization_name      text NULL DEFAULT NULL,
    parent_organization_id text REFERENCES organization(organization_id) ON DELETE CASCADE ON UPDATE RESTRICT,
    create_time            text DEFAULT CURRENT_TIMESTAMP,
    update_time            text DEFAULT NULL,
    create_id              text DEFAULT NULL,
    update_id              text DEFAULT NULL,
    approval_status        text DEFAULT NULL,
    approver_id            text DEFAULT NULL,
    approval_comment       text DEFAULT NULL,
    approval_time          text DEFAULT NULL,
    is_imported            text DEFAULT NULL,
    import_time            text DEFAULT NULL,
    is_system_default      text DEFAULT NULL,
    tenant_id              text DEFAULT NULL,
    version                text DEFAULT NULL,
    status                 text DEFAULT NULL
);

COMMENT ON COLUMN organization.organization_id IS '组织ID';
COMMENT ON COLUMN organization.organization_name IS '组织名称';
COMMENT ON COLUMN organization.parent_organization_id IS '父组织ID';
COMMENT ON COLUMN organization.create_time IS '创建时间';
COMMENT ON COLUMN organization.update_time IS '修改时间';
COMMENT ON COLUMN organization.create_id IS '创建人ID';
COMMENT ON COLUMN organization.update_id IS '修改人ID';
COMMENT ON COLUMN organization.approval_status IS '审批状态';
COMMENT ON COLUMN organization.approver_id IS '审批人id';
COMMENT ON COLUMN organization.approval_comment IS '审批意见';
COMMENT ON COLUMN organization.approval_time IS '审批时间';
COMMENT ON COLUMN organization.is_imported IS '是否导入';
COMMENT ON COLUMN organization.import_time IS '导入时间';
COMMENT ON COLUMN organization.is_system_default IS '是否系统默认';
COMMENT ON COLUMN organization.tenant_id IS '租户id';
COMMENT ON COLUMN organization.version IS '乐观锁版本号';
COMMENT ON COLUMN organization.status IS '状态';

COMMENT ON TABLE organization IS '组织表';


-- ----------------------------
-- Table structure for user
-- ----------------------------
-- DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS "user"
(
    user_id             text PRIMARY KEY,
    user_name           text NULL DEFAULT NULL,
    create_time         text DEFAULT CURRENT_TIMESTAMP,
    update_time         text DEFAULT NULL,
    create_id           text DEFAULT NULL,
    update_id           text DEFAULT NULL,
    approval_status     text DEFAULT NULL,
    approver_id         text DEFAULT NULL,
    approval_comment    text DEFAULT NULL,
    approval_time       text DEFAULT NULL,
    is_imported         text DEFAULT NULL,
    import_time         text DEFAULT NULL,
    is_system_default   text DEFAULT NULL,
    tenant_id           text DEFAULT NULL,
    version             text DEFAULT NULL,
    status              text DEFAULT NULL
);

COMMENT ON COLUMN "user".user_id IS '用户ID';
COMMENT ON COLUMN "user".user_name IS '用户名';
COMMENT ON COLUMN "user".create_time IS '创建时间';
COMMENT ON COLUMN "user".update_time IS '修改时间';
COMMENT ON COLUMN "user".create_id IS '创建人ID';
COMMENT ON COLUMN "user".update_id IS '修改人ID';
COMMENT ON COLUMN "user".approval_status IS '审批状态';
COMMENT ON COLUMN "user".approver_id IS '审批人id';
COMMENT ON COLUMN "user".approval_comment IS '审批意见';
COMMENT ON COLUMN "user".approval_time IS '审批时间';
COMMENT ON COLUMN "user".is_imported IS '是否导入';
COMMENT ON COLUMN "user".import_time IS '导入时间';
COMMENT ON COLUMN "user".is_system_default IS '是否系统默认';
COMMENT ON COLUMN "user".tenant_id IS '租户id';
COMMENT ON COLUMN "user".version IS '乐观锁版本号';
COMMENT ON COLUMN "user".status IS '状态';

COMMENT ON TABLE "user" IS '用户表';


-- ----------------------------
-- Table structure for role
-- ----------------------------
-- DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS "role"
(
    role_id             text PRIMARY KEY,
    role_name           text NULL DEFAULT NULL,
    parent_role_id      text DEFAULT NULL,
    create_time         text DEFAULT CURRENT_TIMESTAMP,
    update_time         text DEFAULT NULL,
    create_id           text DEFAULT NULL,
    update_id           text DEFAULT NULL,
    approval_status     text DEFAULT NULL,
    approver_id         text DEFAULT NULL,
    approval_comment    text DEFAULT NULL,
    approval_time       text DEFAULT NULL,
    is_imported         text DEFAULT NULL,
    import_time         text DEFAULT NULL,
    is_system_default   text DEFAULT NULL,
    tenant_id           text DEFAULT NULL,
    version             text DEFAULT NULL,
    status              text DEFAULT NULL
);

COMMENT ON COLUMN "role".role_id IS '角色ID';
COMMENT ON COLUMN "role".role_name IS '角色名称';
COMMENT ON COLUMN "role".parent_role_id IS '父角色ID';
COMMENT ON COLUMN "role".create_time IS '创建时间';
COMMENT ON COLUMN "role".update_time IS '修改时间';
COMMENT ON COLUMN "role".create_id IS '创建人ID';
COMMENT ON COLUMN "role".update_id IS '修改人ID';
COMMENT ON COLUMN "role".approval_status IS '审批状态';
COMMENT ON COLUMN "role".approver_id IS '审批人id';
COMMENT ON COLUMN "role".approval_comment IS '审批意见';
COMMENT ON COLUMN "role".approval_time IS '审批时间';
COMMENT ON COLUMN "role".is_imported IS '是否导入';
COMMENT ON COLUMN "role".import_time IS '导入时间';
COMMENT ON COLUMN "role".is_system_default IS '是否系统默认';
COMMENT ON COLUMN "role".tenant_id IS '租户id';
COMMENT ON COLUMN "role".version IS '乐观锁版本号';
COMMENT ON COLUMN "role".status IS '状态';

-- 添加外键约束
ALTER TABLE "role"
    ADD CONSTRAINT fk_role_parent_role_id
    FOREIGN KEY (parent_role_id) REFERENCES "role" (role_id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "role" IS '角色表';


-- ----------------------------
-- Table structure for permission
-- ----------------------------
-- DROP TABLE IF EXISTS `permission`;
CREATE TABLE IF NOT EXISTS "permission"
(
    permission_id           text PRIMARY KEY,
    permission_name         text NULL DEFAULT NULL,
    permission_type         text NULL DEFAULT NULL,
    parent_permission_id    text DEFAULT NULL,
    create_time             text DEFAULT CURRENT_TIMESTAMP,
    update_time             text DEFAULT NULL,
    create_id               text DEFAULT NULL,
    update_id               text DEFAULT NULL,
    approval_status         text DEFAULT NULL,
    approver_id             text DEFAULT NULL,
    approval_comment        text DEFAULT NULL,
    approval_time           text DEFAULT NULL,
    is_imported             text DEFAULT NULL,
    import_time             text DEFAULT NULL,
    is_system_default       text DEFAULT NULL,
    tenant_id               text DEFAULT NULL,
    version                 text DEFAULT NULL,
    status                  text DEFAULT NULL
);

COMMENT ON COLUMN "permission".permission_id IS '权限ID';
COMMENT ON COLUMN "permission".permission_name IS '权限名称';
COMMENT ON COLUMN "permission".permission_type IS '权限类型';
COMMENT ON COLUMN "permission".parent_permission_id IS '父权限ID';
COMMENT ON COLUMN "permission".create_time IS '创建时间';
COMMENT ON COLUMN "permission".update_time IS '修改时间';
COMMENT ON COLUMN "permission".create_id IS '创建人ID';
COMMENT ON COLUMN "permission".update_id IS '修改人ID';
COMMENT ON COLUMN "permission".approval_status IS '审批状态';
COMMENT ON COLUMN "permission".approver_id IS '审批人id';
COMMENT ON COLUMN "permission".approval_comment IS '审批意见';
COMMENT ON COLUMN "permission".approval_time IS '审批时间';
COMMENT ON COLUMN "permission".is_imported IS '是否导入';
COMMENT ON COLUMN "permission".import_time IS '导入时间';
COMMENT ON COLUMN "permission".is_system_default IS '是否系统默认';
COMMENT ON COLUMN "permission".tenant_id IS '租户id';
COMMENT ON COLUMN "permission".version IS '乐观锁版本号';
COMMENT ON COLUMN "permission".status IS '状态';


-- 添加外键约束
ALTER TABLE "permission"
    ADD CONSTRAINT fk_permission_parent_permission_id
    FOREIGN KEY (parent_permission_id) REFERENCES "permission" (permission_id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "permission" IS '权限表';







-- ----------------------------
-- Table structure for user_organization
-- ----------------------------
-- DROP TABLE IF EXISTS `user_organization`;
CREATE TABLE IF NOT EXISTS "user_organization"
(
    "user_id"         text  NULL DEFAULT NULL,
    "organization_id" text  NULL DEFAULT NULL
);

COMMENT ON COLUMN "user_organization"."user_id" IS '用户ID';
COMMENT ON COLUMN "user_organization"."organization_id" IS '组织ID';

-- 添加主键和外键的约束
ALTER TABLE "user_organization"
    ADD CONSTRAINT pk_user_organization PRIMARY KEY ("user_id", "organization_id");

-- 添加外键约束
ALTER TABLE "user_organization"
    ADD CONSTRAINT fk_user_organization_user_id
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE "user_organization"
    ADD CONSTRAINT fk_user_organization_organization_id
    FOREIGN KEY ("organization_id") REFERENCES "organization" ("organization_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "user_organization" IS '用户-组织关联表';



-- ----------------------------
-- Table structure for user_role
-- ----------------------------
-- DROP TABLE IF EXISTS `user_role`;
CREATE TABLE IF NOT EXISTS "user_role"
(
    "user_id" text  NULL DEFAULT NULL,
    "role_id" text  NULL DEFAULT NULL
);

COMMENT ON COLUMN "user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "user_role"."role_id" IS '角色ID';

-- 添加主键和外键的约束
ALTER TABLE "user_role"
    ADD CONSTRAINT pk_user_role PRIMARY KEY ("user_id", "role_id");

-- 添加外键约束
ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_user_id
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_role_id
    FOREIGN KEY ("role_id") REFERENCES "role" ("role_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "user_role" IS '用户-角色关联表';





-- ----------------------------
-- Table structure for role_mutex
-- ----------------------------
-- DROP TABLE IF EXISTS `role_mutex`;
CREATE TABLE IF NOT EXISTS "role_mutex"
(
    "role_id1"          text NULL DEFAULT NULL,
    "role_id2"          text NULL DEFAULT NULL,
    "mutex_description" text NULL DEFAULT NULL
);

COMMENT ON COLUMN "role_mutex"."role_id1" IS '角色ID1';
COMMENT ON COLUMN "role_mutex"."role_id2" IS '角色ID2';
COMMENT ON COLUMN "role_mutex"."mutex_description" IS '互斥描述';

-- 添加主键和外键的约束
ALTER TABLE "role_mutex"
    ADD CONSTRAINT pk_role_mutex PRIMARY KEY ("role_id1", "role_id2");

-- 添加外键约束
ALTER TABLE "role_mutex"
    ADD CONSTRAINT fk_role_mutex_role_id1
    FOREIGN KEY ("role_id1") REFERENCES "role" ("role_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE "role_mutex"
    ADD CONSTRAINT fk_role_mutex_role_id2
    FOREIGN KEY ("role_id2") REFERENCES "role" ("role_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "role_mutex" IS '角色互斥表';


-- ----------------------------
-- Records of role_mutex
-- ----------------------------

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
-- DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE IF NOT EXISTS "role_permission"
(
    "role_id"       text NULL DEFAULT NULL,
    "permission_id" text NULL DEFAULT NULL
);

COMMENT ON COLUMN "role_permission"."role_id" IS '角色ID';
COMMENT ON COLUMN "role_permission"."permission_id" IS '权限ID';

-- 添加主键和外键的约束
ALTER TABLE "role_permission"
    ADD CONSTRAINT pk_role_permission PRIMARY KEY ("role_id", "permission_id");

-- 添加外键约束
ALTER TABLE "role_permission"
    ADD CONSTRAINT fk_role_permission_role_id
    FOREIGN KEY ("role_id") REFERENCES "role" ("role_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE "role_permission"
    ADD CONSTRAINT fk_role_permission_permission_id
    FOREIGN KEY ("permission_id") REFERENCES "permission" ("permission_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "role_permission" IS '角色-权限关联表';








-- ----------------------------
-- Table structure for organization_role
-- ----------------------------
-- DROP TABLE IF EXISTS `organization_role`;
CREATE TABLE IF NOT EXISTS "organization_role"
(
    "role_id"         text NULL DEFAULT NULL,
    "organization_id" text NULL DEFAULT NULL
);

COMMENT ON COLUMN "organization_role"."role_id" IS '角色ID';
COMMENT ON COLUMN "organization_role"."organization_id" IS '组织ID';

-- 添加主键和外键的约束
ALTER TABLE "organization_role"
    ADD CONSTRAINT pk_organization_role PRIMARY KEY ("role_id", "organization_id");

-- 添加外键约束
ALTER TABLE "organization_role"
    ADD CONSTRAINT fk_organization_role_role_id
    FOREIGN KEY ("role_id") REFERENCES "role" ("role_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

ALTER TABLE "organization_role"
    ADD CONSTRAINT fk_organization_role_organization_id
    FOREIGN KEY ("organization_id") REFERENCES "organization" ("organization_id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

COMMENT ON TABLE "organization_role" IS '组织-角色关联表';



-- ----------------------------
-- Table structure for user_group
-- ----------------------------
-- DROP TABLE IF EXISTS `user_group`;
CREATE TABLE IF NOT EXISTS "user_group"
(
    "user_group_id"     text NOT NULL,
    "user_group_name"   text NULL DEFAULT NULL,
    "create_time"       text  NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time"       text  NULL DEFAULT NULL,
    "create_id"         text NULL DEFAULT NULL,
    "update_id"         text NULL DEFAULT NULL,
    "approval_status"   text NULL DEFAULT NULL,
    "approver_id"       text NULL DEFAULT NULL,
    "approval_comment"  text NULL DEFAULT NULL,
    "approval_time"     text  NULL DEFAULT NULL,
    "is_imported"       text NULL DEFAULT NULL,
    "import_time"       text  NULL DEFAULT NULL,
    "is_system_default" text NULL DEFAULT NULL,
    "tenant_id"         text NULL DEFAULT NULL,
    "version"           text NULL DEFAULT NULL,
    "status"            text NULL DEFAULT NULL,
    PRIMARY KEY ("user_group_id")
);

COMMENT ON COLUMN "user_group"."user_group_id" IS '用户组ID';
COMMENT ON COLUMN "user_group"."user_group_name" IS '用户组名称';
COMMENT ON COLUMN "user_group"."create_time" IS '创建时间';
COMMENT ON COLUMN "user_group"."update_time" IS '修改时间';
COMMENT ON COLUMN "user_group"."create_id" IS '创建人ID';
COMMENT ON COLUMN "user_group"."update_id" IS '修改人ID';
COMMENT ON COLUMN "user_group"."approval_status" IS '审批状态';
COMMENT ON COLUMN "user_group"."approver_id" IS '审批人id';
COMMENT ON COLUMN "user_group"."approval_comment" IS '审批意见';
COMMENT ON COLUMN "user_group"."approval_time" IS '审批时间';
COMMENT ON COLUMN "user_group"."is_imported" IS '是否导入';
COMMENT ON COLUMN "user_group"."import_time" IS '导入时间';
COMMENT ON COLUMN "user_group"."is_system_default" IS '是否系统默认';
COMMENT ON COLUMN "user_group"."tenant_id" IS '租户id';
COMMENT ON COLUMN "user_group"."version" IS '乐观锁版本号';
COMMENT ON COLUMN "user_group"."status" IS '状态';

COMMENT ON TABLE "user_group" IS '用户组表';



-- ----------------------------
-- Table structure for user_group_role
-- ----------------------------
-- DROP TABLE IF EXISTS `user_group_role`;
CREATE TABLE IF NOT EXISTS "user_group_role"
(
    "role_id"       text NULL DEFAULT NULL,
    "user_group_id" text NULL DEFAULT NULL,
    CONSTRAINT "pk_user_group_role" PRIMARY KEY ("role_id", "user_group_id"),
    CONSTRAINT "fk_user_group_role_role_id" FOREIGN KEY ("role_id") REFERENCES "role" ("role_id") ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT "fk_user_group_role_user_group_id" FOREIGN KEY ("user_group_id") REFERENCES "user_group" ("user_group_id") ON DELETE CASCADE ON UPDATE RESTRICT
);

COMMENT ON COLUMN "user_group_role"."role_id" IS '角色ID';
COMMENT ON COLUMN "user_group_role"."user_group_id" IS '用户组ID';
COMMENT ON TABLE "user_group_role" IS '用户组-角色关联表';

-- ----------------------------
-- Table structure for user_user_group
-- ----------------------------
-- DROP TABLE IF EXISTS `user_user_group`;
CREATE TABLE IF NOT EXISTS "user_user_group"
(
    "user_id"       text NULL DEFAULT NULL,
    "user_group_id" text NULL DEFAULT NULL,
    CONSTRAINT "fk_user_user_group_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("user_id") ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT "fk_user_user_group_user_group_id" FOREIGN KEY ("user_group_id") REFERENCES "user_group" ("user_group_id") ON DELETE CASCADE ON UPDATE RESTRICT
);

COMMENT ON COLUMN "user_user_group"."user_id" IS '用户ID';
COMMENT ON COLUMN "user_user_group"."user_group_id" IS '用户组ID';
COMMENT ON TABLE "user_user_group" IS '用户-用户组关联表';
