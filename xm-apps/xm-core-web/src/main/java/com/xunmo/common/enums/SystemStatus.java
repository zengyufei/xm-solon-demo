package com.xunmo.common.enums;


import com.xunmo.common.ISystemStatus;

/**
 * Created by Administrator on 2017/7/28.
 */
public enum SystemStatus implements ISystemStatus {

    /**
     * 2xxx 成功
     */
    IS_SUCCESS(true, 200, "操作成功！"),
    IS_ASYNC_SUCCESS(true, 200, "异步操作成功！"),
    IS_EXIST(true, 2001, "记录已存在！"),
    STATUS_DEF(true, 2002, "init status ！"),
    GET_RECORD_IN_DB(true, 2003, "success to get record from db ！"),
    GET_RECORD_IN_REDIS(true, 2004, "success to get record from redis ！"),

    /**
     * 4xxx 客户端的错误
     */
    IN_VALIDA_PARAM(false, 400, "参数尚未填完整！"),//使用此枚举在new CustomerException()时需要输入Field。field指的是某参数
    VALUE_ILLEGAL(false, 4000, "输入值不合法！"),
    NO_MULTIPART_FILE(false, 4001, "请上传文件！"),
    TOO_LARGE_AMOUNT(false, 4002, "数值过大！"),
    INVALID_TIME(false, 4003, "时间格式不正确！"),
    IN_VALIDA_MAP_STR(false, 4004, "json格式不合法，无法转换成Map！"),
    INVALID_CHECK_CODE(false, 4005, "验证码错误！"),
    CHECK_CODE_EXPIRE(false, 4005, "验证码失效，请重试！"),
    FORMID_IS_WRONGFUL(false, 4006, "formid不合法！"),
    FORMID_ADD_FAIL(false, 4007, "formid添加失败！"),
    SELECT_NEED_UPLOAD_FILE(false, 4008, "请选择需要上传的文件"),
    METHOD_NOT_ALLOWED(false, 405, "客户端请求方法被禁止"),

    /**
     * 5xxx 系统方面的错误
     */
    NO_ROW_AFFECTED_TO_DB(false, 5001, "执行操作失败，此操作没有对数据中心造成任何影响！"),
    NO_ROW_AFFECTED_TO_REDIS(false, 5002, "存入缓存失败，可能已经有相同的记录！"),
    NO_RECORD_IN_DB(false, 5003, "无法从数据中心获得此条件的数据！"),
    NO_RECORD_IN_REDIS(false, 5004, "无法从缓存中心获得此条件的数据！ "),
    SYSTEM_ERROR(false, 5005, "系统错误！"),
    NOT_EXIST(false, 5006, "记录不存在！"),
    IS_FAIL(false, 5007, "操作失败！"),
    CAN_NOT_GET_REDIS_CLIENT(false, 5008, "无法获得缓存连接！"),
    DESERIALIZE_ERROR(false, 5009, "反序列化失败！"),
    CAN_NOT_CREATE_OBJECT(false, 5010, "无法创建对象！"),
    FILE_NOT_EXIST(false, 5011, "文件不存在！"),
    FILE_UPLOAD_FAIL(false, 5012, "文件上传失败！"),
    CALL_WX_INTERFACE_FAIL(false, 5013, "调用微信接口失败"),
    CAN_NOT_DEL_SYSTEM_ROLE(false, 5014, "系统角色不可删除"),
    MAPPING_ERROR(false, 5015, "字段映射错误"),

    /**
     * 600x - 特殊的code
     */
    SYSTEM_IS_BUSY(false, 6000, "系统繁忙，请稍后再试！"),
    GET_ACCESS_TOKEN_FAIL(false, 6001, "获取access_token失败！"),
    DECRYPT_PHONE_FAIL(false, 6002, "解密手机号码失败"),

    /**
     * 60xx 用户角色权限相关
     */
    GUEST_TOKEN_NOT_EXISTS(false, 6011, "游客token不存在"),
    GUEST_TOKEN_EXPIRED(false, 6012, "游客token过期"),
    TOKEN_NOT_EXISTS(false, 6013, "token不存在"),
    TOKEN_EXPIRED(false, 6014, "token过期"),
    JS_CODE_TIME_OUT(false, 6015, "请检查jsCode/appId/appSecret！"),
    WX_WEBPAGE_CODE_TIME_OUT(false, 6015, "请检查code/appId/appSecret！"),
    ALIPAY_WEBPAGE_CODE_TIME_OUT(false, 6015, "请检查code/appid/应用密钥和公钥！"),
    EMPTY_COOKIES(false, 6016, "无法找到用户的Cookie！"),
    NOT_LOG_IN(false, 6017, "用户尚未登录！"),
    CAN_NOT_GET_USER_INFO(false, 6017, "无法找到用户信息，请检查是否登录！"),
    NOT_REGISTER(false, 6018, "用户未注册，请先注册！"),
    HAS_BEEN_REGISTER(false, 6019, "已注册，请登录！"),
    SYSTEM_USER_CANNOT_DELETE(false, 6020, "超级管理员用户不可删除！"),
    UN_MATCHED_PWD(false, 6021, "密码错误！"),
    ACCOUNT_NOT_FOUND(false, 6022, "账号不存在！"),
    ACCOUNT_IS_EXIST(false, 6023, "账户已存在！"),
    ROLE_IS_EXIST(false, 6024, "用户已存在该角色，更新失败！"),
    ROLE_NAME_IS_EXIST(false, 6025, "已存在该名称角色，操作失败！"),
    NOT_ROLE(false, 6026, "没有此角色！"),
    OPENID_UNDEFINED(false, 6027, "找不到openid，操作失败！"),
    UN_MATCHED_OLD_PASSWORD(false, 6028, "原密码输入错误！"),
    ROLE_CODE_IS_EXIST(false, 6029, "已存在该角色编码，操作失败！"),
    AREA_PERMISSION_NOT_OPEN(false, 6030, "当前用户尚未开启区域后台权限，操作失败！"),
    ACCOUNT_BLACKLIST(false, 6031, "您的账号已被拉黑，无法操作！"),
    ACCOUNT_FREEZE(false, 6032, "您的账号已被冻结，无法操作！"),
    PASSWORD_ARE_INCONSISTENT(false, 6033, "两次输入的密码不一致！"),
    PERMISSION_DENY(false, 6034, "无权限"),
    PHONE_EXIST(false, 6035, "手机号码已存在"),
    EMAIL_EXIST(false, 6036, "Email已存在"),
    CANNOT_DELETE_SUPER_ADMIN_ACCOUNT(false, 6037, "无法删除超级管理员用户"),

    /**
     * 61xx 定时任务相关方面
     */
    INVALID_EXECUTE_TIME(false, 6100, "任务【执行时间】应大于【当前时间】！"),
    TRIGGER_NOT_FOUND(false, 6101, "根据条件无法找到对应的 TRIGGER ，任务可能不存在！"),
    JOB_NOT_FOUND(false, 6102, "根据条件无法找到对应的 JOB ！"),


    /**
     * 未分类的
     */


    AMOUNT_NOT_ENOUGH(false, 7117, "资金不足！"),
    NOT_AMOUNT_ACCOUNT(false, 7118, "没有此资金账户！"),
    FAIL_UPDATE_AMOUNT(false, 7119, "更新资金失败！"),
    FREQUENT_OPERATION(false, 7128, "频繁操作！"),
    AMOUNT_TOO_LEAST(false, 7136, "金额太少！"),
    AMOUNT_IS_NOT_ENOUGH(false, 7137, "数额不足,操作失败"),
    VERIFY_CODE_NOT_EXPIRED(false, 7138, "短信验证码自发送后10分钟内仍有效！"),

    //关于支付的状态
    REPETITION_PAY(false, 7200, "不可重复支付！"),
    ORDER_LOSE_EFFICACY(false, 7201, "订单失效！"),


    PUSH_WX_MSG_IS_SUCCESS(true, 9001, "推送微信消息成功"),
    PUSH_WX_MSG_IS_FAIL(false, 9101, "推送微信消息失败"),
    KEY_VALUE_NOT_EQUAL(false, 9102, "key与value数量不一致");;

    private boolean success;
    private int code;
    private String msg;

    SystemStatus(boolean success, int code, String msg) {
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
