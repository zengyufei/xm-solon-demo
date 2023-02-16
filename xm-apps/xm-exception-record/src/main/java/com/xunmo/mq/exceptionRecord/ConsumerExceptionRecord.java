package com.xunmo.mq.exceptionRecord;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.xunmo.utils.MqHelper;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import com.xunmo.webs.exception_record.service.ExceptionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

@Slf4j
public class ConsumerExceptionRecord implements EventListener<AppLoadEndEvent> {

    @Inject
    private ExceptionRecordService exceptionRecordService;

    @Override
    public void onEvent(AppLoadEndEvent event) throws Throwable {
        MqHelper.consumeMsg(SenderExceptionRecord.BUSINESS_NAME, false, content -> {
            final ExceptionRecord msg = JSONUtil.toBean(content, ExceptionRecord.class);
            log.info("消息队列-收到-队列: {}", SenderExceptionRecord.BUSINESS_NAME);
            try{
                boolean flag = exceptionRecordService.save(msg);
                log.info("保存异常记录结果：{}", flag);
            }catch (Exception e){
                log.info("保存异常记录异常：{}", ExceptionUtil.stacktraceToString(e));
                return MqHelper.ConsumeAction.REJECT;
            }
            return MqHelper.ConsumeAction.ACCEPT;
        });
    }

}
