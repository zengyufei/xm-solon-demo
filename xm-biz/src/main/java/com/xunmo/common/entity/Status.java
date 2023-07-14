package com.xunmo.common.entity;

import com.xunmo.common.ISystemStatus;

/**
 * Created by LinDexing on 2017/6/15 0015.
 */
public class Status {

	private ISystemStatus status;

	private Object content;

	public Status(ISystemStatus status, Object content) {
		this.status = status;
		this.content = content;
	}

	public Status(ISystemStatus status) {
		this.status = status;
	}

	/**
	 * 获得响应状态
	 * @param status
	 * @param content
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/15 0015 21:54
	 */
	public static Status generateStatus(ISystemStatus status, Object content) {
		return new Status(status, content);

	}

	/**
	 * 获得响应状态
	 * @param status
	 * @return
	 * @author : LinDexing
	 * @date : 2017/6/15 0015 21:54
	 */
	public static Status generateStatus(ISystemStatus status) {
		return new Status(status);

	}

	public ISystemStatus getStatus() {
		return status;
	}

	public void setStatus(ISystemStatus status) {
		this.status = status;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

}
