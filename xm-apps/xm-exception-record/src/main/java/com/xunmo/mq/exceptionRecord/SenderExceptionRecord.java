package com.xunmo.mq.exceptionRecord;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xunmo.utils.MqHelper;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SenderExceptionRecord {

    //消息队列队列名称前缀
    public static final String BUSINESS_NAME = "exceptionRecord";//业务名称

    public static void send(ExceptionRecord record) {
        log.info("exception消息队列-发送-队列: {}", BUSINESS_NAME);
        ThreadUtil.execAsync(() -> {
            try {
                final String errorMsg = MqHelper.sendMsg(BUSINESS_NAME, JSONUtil.toJsonStr(record));
                if (StrUtil.isNotBlank(errorMsg)) {
                    log.error("exception消息队列-发送失败: {}", errorMsg);
                }
            } catch (Exception e) {
                log.error("exception消息队列-发送失败: {}", ExceptionUtil.stacktraceToString(e));
                throw new RuntimeException(e);
            }
        });
    }

}
