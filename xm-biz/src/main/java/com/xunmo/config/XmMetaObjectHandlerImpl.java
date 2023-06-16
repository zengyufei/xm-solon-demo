package com.xunmo.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xunmo.XmConstants;
import com.xunmo.base.XmEntity;
import com.xunmo.core.utils.LamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class XmMetaObjectHandlerImpl implements MetaObjectHandler {
    public XmMetaObjectHandlerImpl() {
        System.out.println("....XmMetaObjectHandlerImpl");
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        final String disabledField = LamUtil.getFieldName(XmEntity::getDisabled);
        final String idField = LamUtil.getFieldName(XmEntity::getId);
        final String createTimeField = LamUtil.getFieldName(XmEntity::getCreateTime);
        final String appIdField = LamUtil.getFieldName(XmEntity::getAppId);
        final String tenantIdField = LamUtil.getFieldName(XmEntity::getTenantId);
        final String createUserField = LamUtil.getFieldName(XmEntity::getCreateUser);
        final String createUserNameField = LamUtil.getFieldName(XmEntity::getCreateUserName);
        final String sourceTypeField = LamUtil.getFieldName(XmEntity::getSourceType);

        this.strictInsertFill(metaObject, idField, IdUtil::fastSimpleUUID, String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, createTimeField, DateUtil::date, Date.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, disabledField, () -> XmConstants.ENABLED, Integer.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, appIdField, () -> XmConstants.default_app_id, String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, tenantIdField, () -> XmConstants.default_tenant_id, String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, createUserField, () -> XmConstants.default_create_user, String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, createUserNameField, () -> XmConstants.default_create_user_name, String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, sourceTypeField, () -> XmConstants.default_source_type, String.class); // 起始版本 3.3.3(推荐)
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, LamUtil.getFieldName(XmEntity::getLastUpdateUser), String.class, XmConstants.default_create_user); // 起始版本 3.3.0(推荐)
        this.strictUpdateFill(metaObject, LamUtil.getFieldName(XmEntity::getLastUpdateTime), LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        this.strictUpdateFill(metaObject, LamUtil.getFieldName(XmEntity::getLastUpdateUserName), DateUtil::date, Date.class); // 起始版本 3.3.0(推荐)
    }



}
