package com.xunmo.enums;

public enum ExchangeType {
    direct,  // direct 类型的行为是"先匹配, 再投送". 即在绑定时设定一个 routingkey, 消息的routingkey 匹配时, 才会被交换器投送到绑定的队列中去.
    topic,  // 按规则转发消息（最灵活）
    headers,  // 设置header attribute参数类型的交换机
    fanout,  // 转发消息到所有绑定队列
}
