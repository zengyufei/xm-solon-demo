package com.xunmo.mq.exceptionRecord;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.SendAction;
import com.xunmo.utils.MqHelper;
import com.xunmo.webs.exceptionRecord.input.ExceptionRecordInput;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.annotation.Inject;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MqSendService {

    //消息队列队列名称前缀
    public static final String BUSINESS_NAME = "exceptionRecord";//业务名称
    public static final AtomicBoolean isInit = new AtomicBoolean(false);
    public static MqConfig mqConfig;
    @Inject
    private JacksonActionExecutor jacksonActionExecutor;

    public void init() {
        if (!isInit.get()) {
            final SolonProps cfg     = Solon.cfg();
            final String     group   = cfg.get("solon.app.group");
            final String     appName = cfg.get("solon.app.name");
            mqConfig = MqConfig.of()
                    .title("生产者")
                    .changeName(StrUtil.join("_", group, appName))
                    .queueName(BUSINESS_NAME)
                    .build();
            isInit.compareAndSet(false, true);
        }
    }


    public void send(ExceptionRecordInput exceptionRecordInput) {
        if (!isInit.get()) {
            log.error("exception消息队列-尚未初始化!");
            throw new NullPointerException("exception消息队列-尚未初始化!");
        }
        log.info("exception消息队列-发送-队列: {}", BUSINESS_NAME);
        final ObjectMapper objectMapper = jacksonActionExecutor.config();
        ThreadUtil.execAsync(() -> {
            try {
                final String jsonStr = objectMapper.writeValueAsString(exceptionRecordInput);
//                final String jsonStr = JSONUtil.toJsonStr(record);
                MqHelper.sendMsg(
                        mqConfig,
                        jsonStr,
                        (channel, sendAction) -> {
                            if (SendAction.SUCCESS.equals(sendAction)) {
                                log.debug("exception消息队列-发送成功: {}", jsonStr);
                            }
                            else {
                                log.error("exception消息队列-发送失败: {}", jsonStr);
                            }
                        });
            } catch (Exception e) {
                log.error("exception消息队列-发送异常导致失败: {}", ExceptionUtil.stacktraceToString(e));
                throw new RuntimeException(e);
            }
        });
    }

}
