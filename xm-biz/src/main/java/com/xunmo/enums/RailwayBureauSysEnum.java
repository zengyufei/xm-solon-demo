package com.xunmo.enums;


import com.xunmo.common.ISystemStatus;

/**
 * @author zengyufei
 * @date 2022/6/27 14:07
 */
public enum RailwayBureauSysEnum implements ISystemStatus {

    ATTACHMENT_NULL(false, 90000, "附件不存在"),
    NOT_PERMISSION_ATTACHMENT(false, 90001, "无权限操作附件"),
    ;

    private boolean success;
    private int code;
    private String msg;

    RailwayBureauSysEnum(boolean success, int code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }


    @Override
    public String toString() {
        return "RailwayBureauSysEnum{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

}
