/*
 Navicat Premium Data Transfer

 Source Server         : edu
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : jenkins:3306
 Source Schema         : sp-dev

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 10/05/2022 16:31:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sp_admin
-- ----------------------------
DROP TABLE IF EXISTS `sp_admin`;
CREATE TABLE `sp_admin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id，--主键、自增',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'admin名称',
  `avatar` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `pw` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '明文密码',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `role_id` int(0) NULL DEFAULT 11 COMMENT '所属角色id',
  `status` int(0) NULL DEFAULT 1 COMMENT '账号状态(1=正常, 2=禁用)',
  `create_by_aid` bigint(0) NULL DEFAULT -1 COMMENT '创建自哪个管理员',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '上次登陆时间',
  `login_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上次登陆IP',
  `login_count` int(0) NULL DEFAULT 0 COMMENT '登陆次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10003 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sp_admin
-- ----------------------------
INSERT INTO `sp_admin` VALUES (10001, 'sa', 'http://sa-admin.dev33.cn/sa-frame/admin-logo.png', 'E4EF2A290589A23EFE1565BB698437F5', '123456', NULL, 1, 1, -1, '2022-01-21 14:33:05', '2022-01-27 09:06:30', '127.0.0.1', 6);
INSERT INTO `sp_admin` VALUES (10002, 'admin', 'http://sa-admin.dev33.cn/sa-frame/admin-logo.png', '1DE197572C0B23B82BB2F54202E8E00B', 'admin', NULL, 1, 1, -1, '2022-01-21 14:33:05', NULL, NULL, 0);

-- ----------------------------
-- Table structure for sp_role
-- ----------------------------
DROP TABLE IF EXISTS `sp_role`;
CREATE TABLE `sp_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色id，--主键、自增',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称, 唯一约束',
  `info` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色详细描述',
  `is_lock` int(0) NOT NULL DEFAULT 1 COMMENT '是否锁定(1=是,2=否), 锁定之后不可随意删除, 防止用户误操作',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统角色表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sp_role
-- ----------------------------
INSERT INTO `sp_role` VALUES (1, '开发者', '系统开发人员，最高权限', 1, '2022-05-04 13:02:20');
INSERT INTO `sp_role` VALUES (2, '系统管理员', '系统管理员', 2, '2022-05-04 13:02:20');
INSERT INTO `sp_role` VALUES (11, '普通账号', '普通账号', 2, '2022-05-04 13:02:21');
INSERT INTO `sp_role` VALUES (12, '测试角色', '测试角色', 2, '2022-05-04 13:02:21');

-- ----------------------------
-- Table structure for t_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data`;
CREATE TABLE `t_data`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `authorName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `serviceName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `packageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `moduleName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `subPackageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entitySuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mapperSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `serviceSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `serviceImplSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dsFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tenderFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `lombokFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `chainFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `constFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `genFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `prefixPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `suffixPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appPackageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appModuleName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appPopupType` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appTemplateType` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appAddBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appUpdateBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appDelBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appCopyBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appShowBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appAddBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appUpdateBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appDelBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appCopyBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `appShowBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `parentSystemId` int(0) NULL DEFAULT NULL,
  `parentMenuId` int(0) NULL DEFAULT NULL,
  `menuIcon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `parentIdName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `slaveTalbeName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `slaveFieldName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_data
-- ----------------------------
INSERT INTO `t_data` VALUES (59, '单表-系统维护', 'zengyufei', '2022-05-10 11:11:22', 'system', 'com.xunmo.zyf', 'sys', '', 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', '', '', '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', '', '', '', '', '', 1, 1, 'el-icon-close-notification', '', '', '');
INSERT INTO `t_data` VALUES (72, '单表-RBAC业务-截图用', 'zengyufei', '2022-05-10 14:51:52', 'system', 'com.xunmo.zyf', 'sys', '', 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', '', '', '单表-RBAC业务-截图用', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', '', '', '', '', '', 1, 1, 'el-icon-close-notification', '', '', '');

-- ----------------------------
-- Table structure for t_database
-- ----------------------------
DROP TABLE IF EXISTS `t_database`;
CREATE TABLE `t_database`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `conType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `host` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `port` int(0) NULL DEFAULT NULL,
  `dataSchema` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `jdbcUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createTime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_database
-- ----------------------------
INSERT INTO `t_database` VALUES (4, 'edu', '1', 'edu', 'edu', '1', 'jenkins', 3306, 'sp-dev', '', '2022-05-10 09:53:48');
INSERT INTO `t_database` VALUES (5, 'edux_ac', '1', 'edu', 'edu', '1', 'jenkins', 3306, 'edux_ac', '', '2022-05-10 10:07:52');
INSERT INTO `t_database` VALUES (6, 'edux_截图用', '1', 'edu', 'edu', '1', 'jenkins', 3306, 'edux', '', '2022-05-10 14:44:36');

-- ----------------------------
-- Table structure for t_field
-- ----------------------------
DROP TABLE IF EXISTS `t_field`;
CREATE TABLE `t_field`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `genTableId` int(0) NOT NULL,
  `tableName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名',
  `columnName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '列名',
  `columnComment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '列注释',
  `swaggerComment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'swagger或javadoc注释',
  `columnType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `javaType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `javaField` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'java名称',
  `tsType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'TS类型',
  `sort` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排序',
  `pkFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否主键',
  `incFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否自增',
  `mustFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否必填',
  `logicDeleteFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否逻辑删除',
  `versionFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否乐观锁',
  `fillType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mp填充类型',
  `addFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新增显示',
  `editFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编辑显示',
  `listFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '列表显示',
  `queryFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '查询显示',
  `defaultValue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `isNullable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dataType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `length` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '宽度',
  `width` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端组件',
  `dictType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `queryType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '查询方式=/like',
  `enumStr` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '枚举',
  `editHelpMessage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编辑提示信息',
  `listHelpMessage` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '列表提示信息',
  `numericPrecision` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `numericScale` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `columnKey` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `extra` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createTime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 382 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_field
-- ----------------------------
INSERT INTO `t_field` VALUES (311, 91, 'sp_role', 'id', '角色id，--主键、自增', '角色id，--主键、自增', 'bigint', 'Long', 'id', '', '1', '1', '1', '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'bigint', NULL, '', NULL, '', '1', '', '', '', '', '', 'PRI', 'auto_increment', '2022-05-10 03:41:27', '2022-05-10 03:41:27');
INSERT INTO `t_field` VALUES (312, 91, 'sp_role', 'name', '角色名称, 唯一约束', '角色名称, 唯一约束', 'varchar(20)', 'String', 'name', '', '2', NULL, NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '20', '', NULL, '', '1', '', '', '', '', '', 'UNI', '', '2022-05-10 03:41:27', '2022-05-10 03:41:27');
INSERT INTO `t_field` VALUES (313, 91, 'sp_role', 'info', '角色详细描述', '角色详细描述', 'varchar(200)', 'String', 'info', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '200', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:27', '2022-05-10 03:41:27');
INSERT INTO `t_field` VALUES (314, 91, 'sp_role', 'is_lock', '是否锁定(1=是,2=否), 锁定之后不可随意删除, 防止用户误操作', '是否锁定(1=是,2=否), 锁定之后不可随意删除, 防止用户误操作', 'int', 'Long', 'isLock', '', '4', NULL, NULL, '1', '0', '0', NULL, '1', '1', '1', '1', '1', 'NO', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:27', '2022-05-10 03:41:27');
INSERT INTO `t_field` VALUES (315, 91, 'sp_role', 'create_time', '创建时间', '创建时间', 'timestamp', 'Date', 'createTime', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', 'CURRENT_TIMESTAMP', 'YES', 'timestamp', NULL, '', NULL, '', '1', '', '', '', '', '', '', 'DEFAULT_GENERATED', '2022-05-10 03:41:27', '2022-05-10 03:41:27');
INSERT INTO `t_field` VALUES (316, 92, 'sp_admin', 'id', 'id，--主键、自增', 'id，--主键、自增', 'bigint', 'Long', 'id', '', '1', '1', '1', '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'bigint', NULL, '', NULL, '', '1', '', '', '', '', '', 'PRI', 'auto_increment', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (317, 92, 'sp_admin', 'name', 'admin名称', 'admin名称', 'varchar(100)', 'String', 'name', '', '2', NULL, NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '100', '', NULL, '', '1', '', '', '', '', '', 'UNI', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (318, 92, 'sp_admin', 'avatar', '头像地址', '头像地址', 'varchar(500)', 'String', 'avatar', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '500', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (319, 92, 'sp_admin', 'password', '密码', '密码', 'varchar(100)', 'String', 'password', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '100', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (320, 92, 'sp_admin', 'pw', '明文密码', '明文密码', 'varchar(50)', 'String', 'pw', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '50', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (321, 92, 'sp_admin', 'phone', '手机号', '手机号', 'varchar(20)', 'String', 'phone', '', '6', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '20', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (322, 92, 'sp_admin', 'role_id', '所属角色id', '所属角色id', 'int', 'Long', 'roleId', '', '7', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '11', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (323, 92, 'sp_admin', 'status', '账号状态(1=正常, 2=禁用)', '账号状态(1=正常, 2=禁用)', 'int', 'Long', 'status', '', '8', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '1', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (324, 92, 'sp_admin', 'create_by_aid', '创建自哪个管理员', '创建自哪个管理员', 'bigint', 'Long', 'createByAid', '', '9', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '-1', 'YES', 'bigint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (325, 92, 'sp_admin', 'create_time', '创建时间', '创建时间', 'datetime', 'Date', 'createTime', '', '10', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (326, 92, 'sp_admin', 'login_time', '上次登陆时间', '上次登陆时间', 'datetime', 'Date', 'loginTime', '', '11', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (327, 92, 'sp_admin', 'login_ip', '上次登陆IP', '上次登陆IP', 'varchar(50)', 'String', 'loginIp', '', '12', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '50', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (328, 92, 'sp_admin', 'login_count', '登陆次数', '登陆次数', 'int', 'Long', 'loginCount', '', '13', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '0', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:35', '2022-05-10 03:41:35');
INSERT INTO `t_field` VALUES (329, 93, 'act_re_deployment', 'ID_', '', '', 'varchar(64)', 'String', 'id', '', '1', '1', NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'PRI', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (330, 93, 'act_re_deployment', 'NAME_', '部署文件名', '部署文件名', 'varchar(255)', 'String', 'name', '', '2', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (331, 93, 'act_re_deployment', 'CATEGORY_', '类别', '类别', 'varchar(255)', 'String', 'category', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (332, 93, 'act_re_deployment', 'TENANT_ID_', '', '', 'varchar(255)', 'String', 'tenantId', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '', 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (333, 93, 'act_re_deployment', 'DEPLOY_TIME_', '	\r\n部署时间', '	\r\n部署时间', 'timestamp(3)', 'Date', 'deployTime', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'timestamp', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (334, 94, 'act_ru_identitylink', 'ID_', '', '', 'varchar(64)', 'String', 'id', '', '1', '1', NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'PRI', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (335, 94, 'act_ru_identitylink', 'REV_', '乐观锁', '乐观锁', 'int', 'Long', 'rev', '', '2', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (336, 94, 'act_ru_identitylink', 'GROUP_ID_', '组ID', '组ID', 'varchar(255)', 'String', 'groupId', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (337, 94, 'act_ru_identitylink', 'TYPE_', '类型', '类型', 'varchar(255)', 'String', 'type', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (338, 94, 'act_ru_identitylink', 'USER_ID_', '用户ID', '用户ID', 'varchar(255)', 'String', 'userId', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (339, 94, 'act_ru_identitylink', 'TASK_ID_', '节点实例ID', '节点实例ID', 'varchar(64)', 'String', 'taskId', '', '6', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (340, 94, 'act_ru_identitylink', 'PROC_INST_ID_', '流程实例ID', '流程实例ID', 'varchar(64)', 'String', 'procInstId', '', '7', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (341, 94, 'act_ru_identitylink', 'PROC_DEF_ID_', '流程定义ID', '流程定义ID', 'varchar(64)', 'String', 'procDefId', '', '8', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:41:44', '2022-05-10 03:41:44');
INSERT INTO `t_field` VALUES (342, 95, 'act_ru_execution', 'ID_', '', '', 'varchar(64)', 'String', 'id', '', '1', '1', NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'PRI', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (343, 95, 'act_ru_execution', 'REV_', '	\r\n乐观锁', '	\r\n乐观锁', 'int', 'Long', 'rev', '', '2', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (344, 95, 'act_ru_execution', 'PROC_INST_ID_', '流程实例ID', '流程实例ID', 'varchar(64)', 'String', 'procInstId', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (345, 95, 'act_ru_execution', 'BUSINESS_KEY_', '业务主键ID', '业务主键ID', 'varchar(255)', 'String', 'businessKey', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (346, 95, 'act_ru_execution', 'PARENT_ID_', '父节点实例ID', '父节点实例ID', 'varchar(64)', 'String', 'parentId', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (347, 95, 'act_ru_execution', 'PROC_DEF_ID_', '流程定义ID', '流程定义ID', 'varchar(64)', 'String', 'procDefId', '', '6', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (348, 95, 'act_ru_execution', 'SUPER_EXEC_', '', '', 'varchar(64)', 'String', 'superExec', '', '7', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (349, 95, 'act_ru_execution', 'ACT_ID_', '节点实例ID即ACT_HI_ACTINST中ID', '节点实例ID即ACT_HI_ACTINST中ID', 'varchar(255)', 'String', 'actId', '', '8', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (350, 95, 'act_ru_execution', 'IS_ACTIVE_', '是否存活', '是否存活', 'tinyint', 'Integer', 'isActive', '', '9', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'tinyint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (351, 95, 'act_ru_execution', 'IS_CONCURRENT_', '是否为并行(true/false）', '是否为并行(true/false）', 'tinyint', 'Integer', 'isConcurrent', '', '10', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'tinyint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (352, 95, 'act_ru_execution', 'IS_SCOPE_', '', '', 'tinyint', 'Integer', 'isScope', '', '11', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'tinyint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (353, 95, 'act_ru_execution', 'IS_EVENT_SCOPE_', '', '', 'tinyint', 'Integer', 'isEventScope', '', '12', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'tinyint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (354, 95, 'act_ru_execution', 'SUSPENSION_STATE_', '挂起状态   1激活 2挂起', '挂起状态   1激活 2挂起', 'int', 'Long', 'suspensionState', '', '13', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (355, 95, 'act_ru_execution', 'CACHED_ENT_STATE_', '', '', 'int', 'Long', 'cachedEntState', '', '14', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (356, 95, 'act_ru_execution', 'TENANT_ID_', '', '', 'varchar(255)', 'String', 'tenantId', '', '15', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '', 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (357, 95, 'act_ru_execution', 'NAME_', '', '', 'varchar(255)', 'String', 'name', '', '16', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (358, 95, 'act_ru_execution', 'LOCK_TIME_', '', '', 'timestamp(3)', 'Date', 'lockTime', '', '17', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'timestamp', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 03:43:45', '2022-05-10 03:43:45');
INSERT INTO `t_field` VALUES (359, 96, 'sp_admin', 'id', 'id，--主键、自增', 'id，--主键、自增', 'bigint', 'Long', 'id', '', '1', '1', '1', '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'bigint', NULL, '', NULL, '', '1', '', '', '', '', '', 'PRI', 'auto_increment', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (360, 96, 'sp_admin', 'name', 'admin名称', 'admin名称', 'varchar(100)', 'String', 'name', '', '2', NULL, NULL, '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'varchar', '100', '', NULL, '', '1', '', '', '', '', '', 'UNI', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (361, 96, 'sp_admin', 'avatar', '头像地址', '头像地址', 'varchar(500)', 'String', 'avatar', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '500', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (362, 96, 'sp_admin', 'password', '密码', '密码', 'varchar(100)', 'String', 'password', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '100', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (363, 96, 'sp_admin', 'pw', '明文密码', '明文密码', 'varchar(50)', 'String', 'pw', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '50', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (364, 96, 'sp_admin', 'phone', '手机号', '手机号', 'varchar(20)', 'String', 'phone', '', '6', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '20', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (365, 96, 'sp_admin', 'role_id', '所属角色id', '所属角色id', 'int', 'Long', 'roleId', '', '7', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '11', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (366, 96, 'sp_admin', 'status', '账号状态(1=正常, 2=禁用)', '账号状态(1=正常, 2=禁用)', 'int', 'Long', 'status', '', '8', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '1', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (367, 96, 'sp_admin', 'create_by_aid', '创建自哪个管理员', '创建自哪个管理员', 'bigint', 'Long', 'createByAid', '', '9', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '-1', 'YES', 'bigint', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (368, 96, 'sp_admin', 'create_time', '创建时间', '创建时间', 'datetime', 'Date', 'createTime', '', '10', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (369, 96, 'sp_admin', 'login_time', '上次登陆时间', '上次登陆时间', 'datetime', 'Date', 'loginTime', '', '11', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (370, 96, 'sp_admin', 'login_ip', '上次登陆IP', '上次登陆IP', 'varchar(50)', 'String', 'loginIp', '', '12', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '50', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (371, 96, 'sp_admin', 'login_count', '登陆次数', '登陆次数', 'int', 'Long', 'loginCount', '', '13', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '0', 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 04:02:32', '2022-05-10 04:02:32');
INSERT INTO `t_field` VALUES (372, 97, 'sys_role', 'role_id', '', '', 'int', 'Long', 'roleId', '', '1', '1', '1', '1', '0', '0', NULL, '1', '1', '1', '1', NULL, 'NO', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', 'PRI', 'auto_increment', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (373, 97, 'sys_role', 'role_name', '', '', 'varchar(64)', 'String', 'roleName', '', '2', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (374, 97, 'sys_role', 'role_code', '', '', 'varchar(64)', 'String', 'roleCode', '', '3', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '64', '', NULL, '', '1', '', '', '', '', '', 'MUL', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (375, 97, 'sys_role', 'role_desc', '', '', 'varchar(255)', 'String', 'roleDesc', '', '4', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (376, 97, 'sys_role', 'ds_type', '', '', 'char(1)', 'String', 'dsType', '', '5', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '2', 'YES', 'char', '1', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (377, 97, 'sys_role', 'ds_scope', '', '', 'varchar(255)', 'String', 'dsScope', '', '6', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'varchar', '255', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (378, 97, 'sys_role', 'create_time', '', '', 'datetime', 'Date', 'createTime', '', '7', NULL, NULL, '1', '0', '0', NULL, '1', '1', '1', '1', 'CURRENT_TIMESTAMP', 'NO', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', 'DEFAULT_GENERATED', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (379, 97, 'sys_role', 'update_time', '', '', 'datetime', 'Date', 'updateTime', '', '8', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', NULL, 'YES', 'datetime', NULL, '', NULL, '', '1', '', '', '', '', '', '', 'on update CURRENT_TIMESTAMP', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (380, 97, 'sys_role', 'del_flag', '', '', 'char(1)', 'String', 'delFlag', '', '9', NULL, NULL, NULL, '0', '0', NULL, '1', '1', '1', '1', '0', 'YES', 'char', '1', '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');
INSERT INTO `t_field` VALUES (381, 97, 'sys_role', 'tenant_id', '', '', 'int', 'Long', 'tenantId', '', '10', NULL, NULL, NULL, '0', '0', NULL, '0', '0', '0', '0', NULL, 'YES', 'int', NULL, '', NULL, '', '1', '', '', '', '', '', '', '', '2022-05-10 06:52:26', '2022-05-10 06:52:26');

-- ----------------------------
-- Table structure for t_gen_kv
-- ----------------------------
DROP TABLE IF EXISTS `t_gen_kv`;
CREATE TABLE `t_gen_kv`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `kvId` int(0) NULL DEFAULT NULL,
  `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '自定义字段表单key',
  `dataId` int(0) NULL DEFAULT NULL,
  `tableId` int(0) NULL DEFAULT NULL,
  `source` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属后端3，前端6',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义字段控件类型',
  `label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义字段显示label',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义字段值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 437 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_gen_kv
-- ----------------------------
INSERT INTO `t_gen_kv` VALUES (377, 1, 'requestPath', 59, 92, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (378, 16, 'test', 59, 92, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (379, 17, 'cc', 59, 92, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (383, 1, 'requestPath', 59, 94, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (384, 16, 'test', 59, 94, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (385, 17, 'cc', 59, 94, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (386, 1, 'requestPath', 59, 95, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (387, 16, 'test', 59, 95, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (388, 17, 'cc', 59, 95, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (413, 18, 'cc2', 59, 96, '6', 'text', '测试前端自定义值', 'ddd');
INSERT INTO `t_gen_kv` VALUES (414, 1, 'requestPath', 59, 96, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (415, 16, 'test', 59, 96, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (416, 17, 'cc', 59, 96, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (417, 18, 'cc2', 59, 93, '3', 'text', '测试前端自定义值', 'ddd');
INSERT INTO `t_gen_kv` VALUES (418, 1, 'requestPath', 59, 93, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (419, 16, 'test', 59, 93, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (420, 17, 'cc', 59, 93, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (421, 18, 'cc2', 59, 91, '3', 'text', '测试前端自定义值', 'ddd');
INSERT INTO `t_gen_kv` VALUES (422, 1, 'requestPath', 59, 91, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (423, 16, 'test', 59, 91, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (424, 17, 'cc', 59, 91, '3', 'text', '测试22222', '我是测试');
INSERT INTO `t_gen_kv` VALUES (437, 28, 'cc2', 72, 97, '6', 'text', '测试前端自定义值', 'ddd');
INSERT INTO `t_gen_kv` VALUES (438, 25, 'requestPath', 72, 97, '3', 'text', '请求连接', '/spRole');
INSERT INTO `t_gen_kv` VALUES (439, 26, 'test', 72, 97, '3', 'text', '测试', '123');
INSERT INTO `t_gen_kv` VALUES (440, 27, 'cc', 72, 97, '3', 'text', '测试22222', '我是测试');

-- ----------------------------
-- Table structure for t_kv
-- ----------------------------
DROP TABLE IF EXISTS `t_kv`;
CREATE TABLE `t_kv`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '自定义字段表单key',
  `dataId` int(0) NULL DEFAULT NULL,
  `source` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '后端3，前端6',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义字段控件类型',
  `value` json NULL COMMENT '自定义字段值，json类型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `key_uni`(`key`, `dataId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_kv
-- ----------------------------
INSERT INTO `t_kv` VALUES (1, 'requestPath', 59, '3', 'text', '{\"name\": \"请求连接\", \"value\": \"/spRole\"}');
INSERT INTO `t_kv` VALUES (16, 'test', 59, '3', 'text', '{\"name\": \"测试\", \"value\": \"123\"}');
INSERT INTO `t_kv` VALUES (17, 'cc', 59, '3', 'text', '{\"name\": \"测试22222\", \"value\": \"我是测试\"}');
INSERT INTO `t_kv` VALUES (18, 'cc2', 59, '6', 'text', '{\"name\": \"测试前端自定义值\", \"value\": \"ddd\"}');
INSERT INTO `t_kv` VALUES (25, 'requestPath', 72, '3', 'text', '{\"name\": \"请求连接\", \"value\": \"/spRole\"}');
INSERT INTO `t_kv` VALUES (26, 'test', 72, '3', 'text', '{\"name\": \"测试\", \"value\": \"123\"}');
INSERT INTO `t_kv` VALUES (27, 'cc', 72, '3', 'text', '{\"name\": \"测试22222\", \"value\": \"我是测试\"}');
INSERT INTO `t_kv` VALUES (28, 'cc2', 72, '6', 'text', '{\"name\": \"测试前端自定义值\", \"value\": \"ddd\"}');

-- ----------------------------
-- Table structure for t_menus
-- ----------------------------
DROP TABLE IF EXISTS `t_menus`;
CREATE TABLE `t_menus`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_menus
-- ----------------------------
INSERT INTO `t_menus` VALUES (1, '默认菜单');

-- ----------------------------
-- Table structure for t_system
-- ----------------------------
DROP TABLE IF EXISTS `t_system`;
CREATE TABLE `t_system`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_system
-- ----------------------------
INSERT INTO `t_system` VALUES (1, '默认系统');

-- ----------------------------
-- Table structure for t_table
-- ----------------------------
DROP TABLE IF EXISTS `t_table`;
CREATE TABLE `t_table`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `dataId` int(0) NULL DEFAULT NULL,
  `tableName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表名',
  `tableComment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表注释',
  `entityName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体名称',
  `varName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '入参名称',
  `authorName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者',
  `createTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `swaggerComment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'swagger或javadoc注释',
  `serviceName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务名',
  `packageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父包名',
  `moduleName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名',
  `subPackageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子包名',
  `entitySuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体父类',
  `mapperSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mapper父类',
  `serviceSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'service父类',
  `serviceImplSuperClassName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'serviceImpl父类',
  `dsFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ServiceImpl类是否标记@DS注解',
  `tenderFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Mapper类是否标记@TenantLine注解',
  `lombokFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否使用@Data注解',
  `chainFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否使用@Accessors(chain = true)注解',
  `constFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否生成字段常量',
  `genFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生成方式',
  `prefixPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `suffixPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `appPackageName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端应用名',
  `appModuleName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端模块名',
  `appPopupType` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '点击新增或编辑时，弹窗的打开方式',
  `appTemplateType` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生成模板',
  `appAddBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示新增按钮',
  `appUpdateBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示编辑按钮',
  `appDelBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示删除按钮',
  `appCopyBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示复制按钮',
  `appShowBtnFlag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示详情按钮',
  `appAddBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新增按钮权限',
  `appUpdateBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编辑按钮权限',
  `appDelBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除按钮权限',
  `appCopyBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '复制按钮权限',
  `appShowBtnCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详情按钮权限',
  `parentSystemId` int(0) NULL DEFAULT NULL COMMENT '菜单所属应用',
  `parentMenuId` int(0) NULL DEFAULT NULL COMMENT '上级菜单',
  `menuName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前菜单名',
  `menuIcon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `parentIdName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '树字段名称',
  `slaveTalbeName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `slaveFieldName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_table
-- ----------------------------
INSERT INTO `t_table` VALUES (91, 59, 'sp_role', '系统角色表', 'SpRole', 'spRole', 'zengyufei', NULL, '系统角色表', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'spRole-add', 'spRole-update', 'spRole-del', 'spRole-copy', 'spRole-detail', 1, 1, '系统角色表', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (92, 59, 'sp_admin', '系统管理员表', 'SpAdmin', 'spAdmin', 'zengyufei', NULL, '系统管理员表', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'spAdmin-add', 'spAdmin-update', 'spAdmin-del', 'spAdmin-copy', 'spAdmin-detail', 1, 1, '系统管理员表', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (93, 59, 'act_re_deployment', '部署信息表\r\n部署流程定义时需要被持久化保存下来的信息。', 'ActReDeployment', 'actReDeployment', 'zengyufei', NULL, '部署信息表\r\n部署流程定义时需要被持久化保存下来的信息。', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'actReDeployment-add', 'actReDeployment-update', 'actReDeployment-del', 'actReDeployment-copy', 'actReDeployment-detail', 1, 1, '部署信息表\r\n部署流程定义时需要被持久化保存下来的信息。', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (94, 59, 'act_ru_identitylink', '运行时流程人员表\r\n任务参与者数据表。主要存储当前节点参与者的信息。', 'ActRuIdentitylink', 'actRuIdentitylink', 'zengyufei', NULL, '运行时流程人员表\r\n任务参与者数据表。主要存储当前节点参与者的信息。', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'actRuIdentitylink-add', 'actRuIdentitylink-update', 'actRuIdentitylink-del', 'actRuIdentitylink-copy', 'actRuIdentitylink-detail', 1, 1, '运行时流程人员表\r\n任务参与者数据表。主要存储当前节点参与者的信息。', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (95, 59, 'act_ru_execution', '运行时流程执行实例表\r\n流程执行记录表。', 'ActRuExecution', 'actRuExecution', 'zengyufei', NULL, '运行时流程执行实例表\r\n流程执行记录表。', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'actRuExecution-add', 'actRuExecution-update', 'actRuExecution-del', 'actRuExecution-copy', 'actRuExecution-detail', 1, 1, '运行时流程执行实例表\r\n流程执行记录表。', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (96, 59, 'sp_admin', '系统管理员表', 'SpAdmin', 'spAdmin', 'zengyufei', NULL, '系统管理员表', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-系统维护', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'spAdmin-add', 'spAdmin-update', 'spAdmin-del', 'spAdmin-copy', 'spAdmin-detail', 1, 1, '系统管理员表', 'el-icon-close-notification', NULL, NULL, NULL);
INSERT INTO `t_table` VALUES (97, 72, 'sys_role', '系统角色表', 'SysRole', 'sysRole', 'zengyufei', NULL, '系统角色表', 'system', 'com.xunmo.zyf', 'sys', NULL, 'Model', 'BaseMapper', 'BaseService', 'BaseServiceImpl', '1', '1', '1', '1', '0', '0', NULL, NULL, '单表-RBAC业务-截图用', 'edu', 'sys', '1', '1', '1', '1', '1', '1', '1', 'sysRole-add', 'sysRole-update', 'sysRole-del', 'sysRole-copy', 'sysRole-detail', 1, 1, '系统角色表', 'el-icon-close-notification', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
