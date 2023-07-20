package com.xunmo.common.utils;

import com.xunmo.XmConstants;
import com.xunmo.common.ISystemStatus;
import com.xunmo.common.entity.ResponseEntity;
import com.xunmo.common.entity.Status;
import com.xunmo.jimmer.page.Page;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;

import java.util.List;
import java.util.Objects;

/**
 * Created by LinDexing on 2017/6/6 0006.
 */
@Slf4j
public class ResponseUtil {

	/**
	 * 获得响应对象实体
	 * @param code
	 * @param success
	 * @param msg
	 * @param contentPojo
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/6 0006
	 */
	public static Object genResponse(int code, boolean success, String msg, Object contentPojo) {
		ResponseEntity responseEntity = new ResponseEntity();
		responseEntity.setCode(code);
		responseEntity.setSuccess(success);
		responseEntity.setMsg(msg);
		responseEntity.setData(contentPojo);
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	/**
	 * 获得响应对象实体
	 * @param code
	 * @param success
	 * @param msg
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/6 0006
	 */
	public static Object genResponse(int code, boolean success, String msg) {
		ResponseEntity responseEntity = new ResponseEntity();
		responseEntity.setCode(code);
		responseEntity.setSuccess(success);
		responseEntity.setMsg(msg);
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	/**
	 * 获得响应对象实体
	 * @param status
	 * @param content
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/9 0009
	 */
	public static ResponseEntity genResponse(ISystemStatus status, Object content) {
		ResponseEntity responseEntity = new ResponseEntity();
		responseEntity.setCode(status.getCode());
		responseEntity.setSuccess(status.isSuccess());
		responseEntity.setMsg(status.getMsg());
		if (content instanceof Page) {
			final Page page = (Page) content;
			final List list = page.getContent();
			final int totalPages = page.getTotalPages();
			final long totalElements = page.getTotalElements();
			responseEntity.setTotalPages(totalPages);
			responseEntity.setTotal(totalElements);
			responseEntity.setPageNum(page.getNumber());
			responseEntity.setPageSize(page.getSize());
			responseEntity.setData(list);
		}
		else {
			responseEntity.setData(content);
		}
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	/**
	 * 获得响应对象实体
	 * @param status
	 * @param content
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/9 0009
	 */
	public static Object genInvalidParamErrorResponse(ISystemStatus status, Object content, Object errorField) {
		ResponseEntity responseEntity = new ResponseEntity();
		responseEntity.setCode(status.getCode());
		responseEntity.setSuccess(status.isSuccess());
		responseEntity.setMsg(content.toString());
		responseEntity.setField(Objects.isNull(errorField) ? null : errorField.toString());
		// responseEntity.setData(content);
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	/**
	 * 获得响应对象实体
	 * @param status
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/9 0009
	 */
	public static ResponseEntity genResponse(ISystemStatus status) {
		ResponseEntity responseEntity = new ResponseEntity();
		responseEntity.setCode(status.getCode());
		responseEntity.setSuccess(status.isSuccess());
		responseEntity.setMsg(status.getMsg());
		// responseEntity.setData(status.getData());
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	/**
	 * 获得响应对象实体
	 * @param responseStatus
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/9 0009
	 */
	public static Object genResponse(Status responseStatus) {
		ResponseEntity responseEntity = new ResponseEntity();
		ISystemStatus status = responseStatus.getStatus();
		responseEntity.setCode(status.getCode());
		responseEntity.setSuccess(status.isSuccess());
		responseEntity.setMsg(status.getMsg());
		responseEntity.setData(responseStatus.getContent());
		final Context ctx = Context.current();
		if (ctx != null) {
			responseEntity.setReqId(ctx.attr(XmConstants.REQ_ID));
		}
		return jsonWrapper(responseEntity);
	}

	public static ResponseEntity jsonWrapper(ResponseEntity object) {
		return object;
	}

	public static void sendMsg(String msg) {
		Context.current().outputAsJson(msg);
	}

}
