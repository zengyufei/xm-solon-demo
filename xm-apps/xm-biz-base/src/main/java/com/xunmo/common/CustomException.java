package com.xunmo.common;



/**
 * Created by LinDexing on 2017/6/8 0008.
 * 将原来继承Exception异常类改为继承RuntimeException，这样才可触发事务回滚 change by feitan on 2019/11/8
 */
public class CustomException extends Exception{
    private static final long serialVersionUID = -1406296040092623625L;
    private ISystemStatus status;
    private Object params;
    private Object msg;
    private Object field;

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.msg = message;
    }
    public CustomException(ISystemStatus status, Object params) {
        super(status.toString());
        this.status = status;
        this.params = params;
    }
    public CustomException(ISystemStatus status, Object params, String errorField) {
        super(status.toString());
        this.status = status;
        this.params = params;
        this.field = errorField;
    }
    public CustomException(ISystemStatus status) {
        super(status.toString());
        this.status = status;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ISystemStatus getStatus() {
        return status;
    }

    public void setStatus(ISystemStatus status) {
        this.status = status;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "status=" + status +
                ", params=" + params +
                ", msg=" + msg +
                ", field=" + field +
                '}';
    }

    public Object getField() {
        return field;
    }

    public void setField(Object errorField) {
        this.field = errorField;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
