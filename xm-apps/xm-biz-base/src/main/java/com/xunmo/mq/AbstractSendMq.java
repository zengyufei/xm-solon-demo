package com.xunmo.mq;



/**
 * 消息队列
 *
 * @author zengyufei
 * @date 2022/4/15 15:44
 */
public abstract class AbstractSendMq<T> {

    public abstract void sendMsg(T msg);
}
