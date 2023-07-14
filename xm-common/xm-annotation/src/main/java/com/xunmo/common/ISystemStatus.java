package com.xunmo.common;

/**
 * create by tan 2020/02/25 除了系统的状态外，其他的模块的状态需要继承此接口 不要在SystemStatus中添加各自模块的状态。免得越来越长
 */
public interface ISystemStatus {

	boolean isSuccess();

	int getCode();

	String getMsg();

}
