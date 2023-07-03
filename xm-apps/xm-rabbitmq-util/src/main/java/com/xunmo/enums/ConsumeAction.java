package com.xunmo.enums;

public enum ConsumeAction {
    ACCEPT,  // 消费成功
    RETRY,   // 消费失败，可以放回队列重新消费
    REJECT,  // 消费拒绝
}
