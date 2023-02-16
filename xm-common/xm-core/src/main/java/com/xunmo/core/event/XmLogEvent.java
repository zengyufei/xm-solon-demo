package com.xunmo.core.event;

import lombok.Getter;

@Getter
public class XmLogEvent {
    private String msg;
    public XmLogEvent(String msg){
        this.msg = msg;
    }
}
