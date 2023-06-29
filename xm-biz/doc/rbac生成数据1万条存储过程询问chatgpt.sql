DROP PROCEDURE IF EXISTS generate_test_data;
DELIMITER //

CREATE PROCEDURE generate_test_data()
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE create_id VARCHAR(255) DEFAULT 'admin';
  DECLARE update_id VARCHAR(255) DEFAULT 'admin';

  -- 生成组织表数据（20条）
  WHILE i < 20 DO
    SET create_id = CONCAT('user_', FLOOR(RAND() * 100));
    SET update_id = CONCAT('user_', FLOOR(RAND() * 100));
    INSERT INTO organization (organization_id, organization_name, parent_organization_id, create_time, update_time, create_id, update_id, approval_status, approver_id, approval_comment, approval_time, is_imported, import_time, is_system_default, tenant_id, version, status)
    VALUES (CONCAT('org_', i), CONCAT('Organization ', i), NULL, NOW(), NOW(), create_id, update_id, '', '', '', NULL, FALSE, NULL, FALSE, 'tenant1', 1, 'active');
    SET i = i + 1;
  END WHILE;

  -- 生成用户表数据（1万条）
  SET i = 0;
  WHILE i < 10000 DO
    IF i >= 1000 THEN
      SET create_id = CONCAT('user_', FLOOR(RAND() * 1000));
      SET update_id = CONCAT('user_', FLOOR(RAND() * 1000));
    END IF;

    INSERT INTO user (user_id, user_name, create_time, update_time, create_id, update_id, approval_status, approver_id, approval_comment, approval_time, is_imported, import_time, is_system_default, tenant_id, version, status)
    VALUES (CONCAT('user_', i), CONCAT('User ', i), NOW(), NOW(), create_id, update_id, '', '', '', NULL, FALSE, NULL, FALSE, 'tenant1', 1, 'active');
    SET i = i + 1;
  END WHILE;

  -- 生成角色表数据（1000条）
  SET i = 0;
  WHILE i < 1000 DO
    SET create_id = CONCAT('user_', FLOOR(RAND() * 1000));
    SET update_id = CONCAT('user_', FLOOR(RAND() * 1000));
    INSERT INTO role (role_id, role_name, parent_role_id, create_time, update_time, create_id, update_id, approval_status, approver_id, approval_comment, approval_time, is_imported, import_time, is_system_default, tenant_id, version, status)
    VALUES (CONCAT('role_', i), CONCAT('Role ', i), NULL, NOW(), NOW(), create_id, update_id, '', '', '', NULL, FALSE, NULL, FALSE, 'tenant1', 1, 'active');
    SET i = i + 1;
  END WHILE;

  -- 生成权限表数据（500条）
  SET i = 0;
  WHILE i < 500 DO
    SET create_id = CONCAT('user_', FLOOR(RAND() * 1000));
    SET update_id = CONCAT('user_', FLOOR(RAND() * 1000));
    INSERT INTO permission (permission_id, permission_name, permission_type, parent_permission_id, create_time, update_time, create_id, update_id, approval_status, approver_id, approval_comment, approval_time, is_imported, import_time, is_system_default, tenant_id, version, status)
    VALUES (CONCAT('perm_', i), CONCAT('Permission ', i), 'type', NULL, NOW(), NOW(), create_id, update_id, '', '', '', NULL, FALSE, NULL, FALSE, 'tenant1', 1, 'active');
    SET i = i + 1;
  END WHILE;

  -- 生成用户组表数据（70条）
  SET i = 0;
  WHILE i < 70 DO
    SET create_id = CONCAT('user_', FLOOR(RAND() * 1000));
    SET update_id = CONCAT('user_', FLOOR(RAND() * 1000));
    INSERT INTO user_group (user_group_id, user_group_name, create_time, update_time, create_id, update_id, approval_status, approver_id, approval_comment, approval_time, is_imported, import_time, is_system_default, tenant_id, version, status)
    VALUES (CONCAT('group_', i), CONCAT('User Group ', i), NOW(), NOW(), create_id, update_id, '', '', '', NULL, FALSE, NULL, FALSE, 'tenant1', 1, 'active');
    SET i = i + 1;
  END WHILE;

  -- 用户-组织关联表（用户表1万条数据关联组织表20条数据）
  SET i = 0;
  WHILE i < 10000 DO
    INSERT INTO user_organization (user_id, organization_id)
    VALUES (CONCAT('user_', i), CONCAT('org_', i % 20));
    SET i = i + 1;
  END WHILE;

  -- 用户-角色关联表（用户表1万条数据关联角色表1000条数据）
  SET i = 0;
  WHILE i < 10000 DO
    INSERT INTO user_role (user_id, role_id)
    VALUES (CONCAT('user_', i), CONCAT('role_', i % 1000));
    SET i = i + 1;
  END WHILE;

  -- 角色-权限关联表（角色表1000条数据关联权限表500条数据）
  SET i = 0;
  WHILE i < 1000 DO
    INSERT INTO role_permission (role_id, permission_id)
    VALUES (CONCAT('role_', i), CONCAT('perm_', i % 500));
    SET i = i + 1;
  END WHILE;

  -- 用户组-角色关联表（用户组表70条数据关联角色表1000条数据）
  SET i = 0;
  WHILE i < 70 DO
    INSERT INTO user_group_role (role_id, user_group_id)
    VALUES (CONCAT('role_', i % 1000), CONCAT('group_', i));
    SET i = i + 1;
  END WHILE;

  -- 用户-用户组关联表（用户表1万条数据关联用户组表70条数据）
  SET i = 0;
  WHILE i < 10000 DO
    INSERT INTO user_user_group (user_id, user_group_id)
    VALUES (CONCAT('user_', i), CONCAT('group_', i % 70));
    SET i = i + 1;
  END WHILE;

END //

DELIMITER ;





call generate_test_data();