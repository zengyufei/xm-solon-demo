package com.xunmo.enums;


import com.xunmo.common.ISystemStatus;

public enum DicSystemStatus implements ISystemStatus {

    SYSTEM_REGEDIT_REPET(false, 5032, "字典重复！"),
    NO_SYSTEM_REGEDIT(false, 5033, "没有此字典！"),
    SYSTEM_REGEDIT_LEVEL_SAME(false, 5034, "层级相同！"),
    SYSTEM_REGEDIT_LEVEL_ZERO(false, 5034, "子级为0！"),
    NOT_CODE(false, 5036, "字典编码为空！"),
    YL_APP_ID_IS_NULL(false, 5037, "应用id为空！"),
    ;

    private boolean success;
    private int code;
    private String msg;

    DicSystemStatus(boolean success, int code, String msg) {
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
        return "SystemStatus{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

}
