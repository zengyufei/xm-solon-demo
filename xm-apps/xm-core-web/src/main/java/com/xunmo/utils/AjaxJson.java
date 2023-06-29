package com.xunmo.utils;

import com.xunmo.XmConstants;
import com.xunmo.common.ISystemStatus;
import org.noear.solon.core.handle.Context;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ajax请求返回Json格式数据的封装 <br>
 * 所有预留字段：<br>
 * code=状态码 <br>
 * msg=描述信息 <br>
 * data=携带对象 <br>
 * pageNo=当前页 <br>
 * pageSize=页大小 <br>
 * startIndex=起始索引 <br>
 * total=数据总数 <br>
 * totalPages=分页总数 <br>
 * <p> 返回范例：</p>
 * <pre>
 * {
 * "code": 200,    // 成功时=200, 失败时=500  msg=失败原因
 * "msg": "ok",
 * "data": {}
 * }
 * </pre>
 */
public class AjaxJson<S> extends LinkedHashMap<String, Object> implements Serializable {

    public static final int CODE_SUCCESS = 200;            // 成功状态码
    public static final int CODE_ERROR = 500;            // 错误状态码
    public static final int CODE_WARNING = 501;            // 警告状态码
    public static final int CODE_NOT_JUR = 403;            // 无权限状态码
    public static final int CODE_NOT_LOGIN = 401;        // 未登录状态码
    public static final int CODE_INVALID_REQUEST = 400;    // 无效请求状态码
    private static final long serialVersionUID = 1L;    // 序列化版本号


    // ============================  写值取值  ==================================

    public AjaxJson(int code, String msg, Object data, Long total) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
        this.setTotal(total);
        this.setReqId();

    }

    /**
     * 返回成功
     */
    public static <T> AjaxJson<T> getSuccess() {
        return new AjaxJson<>(CODE_SUCCESS, "ok", null, null);
    }

    public static <T> AjaxJson<T> getSuccess(String msg) {
        return new AjaxJson<>(CODE_SUCCESS, msg, null, null);
    }

    public static <T> AjaxJson<T> getSuccess(String msg, Object data) {
        return new AjaxJson(CODE_SUCCESS, msg, data, null);
    }

    public static <T> AjaxJson<T> getSuccessData(Object data) {
        return new AjaxJson(CODE_SUCCESS, "ok", data, null);
    }

    /**
     * 返回失败
     */
    public static AjaxJson getError() {
        return new AjaxJson(CODE_ERROR, "error", null, null);
    }

    public static AjaxJson getError(String msg) {
        return new AjaxJson(CODE_ERROR, msg, null, null);
    }

    /**
     * 返回警告
     */
    public static AjaxJson getWarning() {
        return new AjaxJson(CODE_ERROR, "warning", null, null);
    }

    public static AjaxJson getWarning(String msg) {
        return new AjaxJson(CODE_WARNING, msg, null, null);
    }

    /**
     * 返回未登录
     */
    public static AjaxJson getNotLogin() {
        return new AjaxJson(CODE_NOT_LOGIN, "未登录，请登录后再次访问", null, null);
    }

    /**
     * 返回没有权限的
     */
    public static AjaxJson getNotJur(String msg) {
        return new AjaxJson(CODE_NOT_JUR, msg, null, null);
    }

    /**
     * 返回一个自定义状态码的
     */
    public static AjaxJson get(int code, String msg) {
        return new AjaxJson(code, msg, null, null);
    }

    /**
     * 返回一个自定义状态码的
     */
    public static AjaxJson get(ISystemStatus systemStatus) {
        return new AjaxJson(systemStatus.getCode(), systemStatus.getMsg(), null, null);
    }

    /**
     * 返回一个自定义状态码的
     */
    public static AjaxJson get(ISystemStatus systemStatus, Object data) {
        return new AjaxJson(systemStatus.getCode(), systemStatus.getMsg(), data, null);
    }

    /**
     * 返回分页和数据的
     */
    public static AjaxJson getPageData(Long total, Object data) {
        return new AjaxJson(CODE_SUCCESS, "ok", data, total);
    }

    /**
     * 返回, 根据受影响行数的(大于0=ok，小于0=error)
     */
    public static AjaxJson getByLine(int line) {
        if (line > 0) {
            return getSuccess("ok", line);
        }
        return getError("error").setData(line);
    }

    /**
     * 返回，根据布尔值来确定最终结果的  (true=ok，false=error)
     */
    public static AjaxJson getByBoolean(boolean b) {
        return b ? getSuccess("ok") : getError("error");
    }


    // ============================  构建  ==================================

    /**
     * 返回code
     */
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    /**
     * 给code赋值，连缀风格
     */
    public AjaxJson<S> setCode(int code) {
        this.put("code", code);
        return this;
    }

    /**
     * 获取msg
     */
    public String getMsg() {
        return (String) this.get("msg");
    }

    /**
     * 给msg赋值，连缀风格
     */
    public AjaxJson<S> setMsg(String msg) {
        this.put("msg", msg);
        return this;
    }

    /**
     * 获取data
     */
    public Object getData() {
        return this.get("data");
    }

    /**
     * 给data赋值，连缀风格
     */
    public AjaxJson<S> setData(Object data) {
        this.put("data", data);
        return this;
    }

    /**
     * 将data还原为指定类型并返回
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> cs) {
        return (T) this.getData();
    }

    /**
     * 给total(数据总数)赋值，连缀风格
     */
    public AjaxJson<S> setReqId() {
        final Context context = Context.current();
        if (context != null) {
            this.put(XmConstants.REQ_ID, context.attr("reqId"));
        }
        return this;
    }

    /**
     * 获取total(数据总数)
     */
    public Long getTotal() {
        return (Long) this.get("total");
    }

    /**
     * 给total(数据总数)赋值，连缀风格
     */
    public AjaxJson<S> setTotal(Long total) {
        this.put("total", total);

        // 如果提供了数据总数，则尝试计算page信息
        if (total != null && total >= 0 && get("pageNo") != null) {
            this.initPageInfo();
        } else {
            this.initPageInfo();
        }
        return this;
    }

    public Long getTotalPages() {
        return (Long) this.get("totalPages");
    }

    public Long getPageNo() {
        return (Long) this.get("pageNo");
    }

    public Long getPageSize() {
        return (Long) this.get("pageSize");
    }

    public String getReqId() {
        return (String) this.get("reqId");
    }

    /**
     * 根据 pageSize total，计算startIndex 与 totalPages
     */
    public AjaxJson<S> initPageInfo() {
        Integer pageNo = (Integer) this.get("pageNo");
        Integer pageSize = (Integer) this.get("pageSize");
        Long total = (Long) this.get("total");
        if (pageNo == null || pageSize == null || pageNo == 0 || pageSize == 0) {
            this.set("totalPages", 0);
        } else {
            long pc = total / pageSize;
            this.set("totalPages", (total % pageSize == 0 ? pc : pc + 1));
        }
        return this;
    }

    /**
     * 写入一个值 自定义key, 连缀风格
     */
    public AjaxJson<S> set(String key, Object data) {
        this.put(key, data);
        return this;
    }

    /**
     * 写入一个Map, 连缀风格
     */
    public AjaxJson<S> setMap(Map<String, ?> map) {
        for (String key : map.keySet()) {
            this.put(key, map.get(key));
        }
        return this;
    }


//  // 历史版本遗留代码
//	public int code; 	// 状态码
//	public String msg; 	// 描述信息
//	public Object data; // 携带对象
//	public Long total;	// 数据总数，用于分页


}
