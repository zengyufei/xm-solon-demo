package com.xunmo.core.event;

import com.xunmo.core.utils.XmLog;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;

@Slf4j
@Component
public class XmLogEventListener implements EventListener<XmLogEvent> {

	@Override
	public void onEvent(XmLogEvent event) throws Throwable {
		XmLog.info(event.getMsg());
	}

}
