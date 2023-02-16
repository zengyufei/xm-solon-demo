package com.xunmo.config;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author noear 2022/4/17 created
 */
public class XmMetaObjectHandlerImpl implements MetaObjectHandler {
    public XmMetaObjectHandlerImpl() {
        System.out.println("....XmMetaObjectHandlerImpl");
    }

    @Override
    public void insertFill(MetaObject metaObject) {
//        this.strictInsertFill(metaObject, "id", () -> IdGen.getId(), String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createTime", DateUtil::date, Date.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "disabled", () -> 0, Integer.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "appId", () -> "1", String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "tenantId", () -> "1", String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createUser", () -> "1", String.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createUserNickName", () -> "管理员", String.class); // 起始版本 3.3.3(推荐)
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        this.strictUpdateFill(metaObject, "lastUpdateTime", DateUtil::date, Date.class); // 起始版本 3.3.0(推荐)
    }
}
