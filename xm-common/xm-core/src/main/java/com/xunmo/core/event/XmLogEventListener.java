package com.xunmo.core.event;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;

//注解模式
@Slf4j
@Component
public class XmLogEventListener implements EventListener<XmLogEvent> {
    @Override
    public void onEvent(XmLogEvent event) throws Throwable {
        if (log.isInfoEnabled()) {
            log.info(event.getMsg());
        }
    }
}
