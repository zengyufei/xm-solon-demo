package com.xunmo.mq.exceptionRecord;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ConsumeAction;
import com.xunmo.utils.MqHelper;
import com.xunmo.webs.exceptionRecord.entity.ExceptionRecord;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MqConsumerService implements EventListener<AppLoadEndEvent> {
    public static final AtomicBoolean isInit = new AtomicBoolean(false);
    public static MqConfig mqConfig;


    public void init() {
        if (!isInit.get()) {
            final SolonProps cfg = Solon.cfg();
            final String group = cfg.get("solon.app.group");
            final String appName = cfg.get("solon.app.name");
            mqConfig = MqConfig.of()
                    .title("消费者")
                    .changeName(StrUtil.join("_", group, appName))
                    .queueName(MqSendService.BUSINESS_NAME)
                    .build();
        }
    }

    @Override
    public void onEvent(AppLoadEndEvent event) throws Throwable {
        MqHelper.consumeMsg(
                mqConfig, (channel, content) -> {
                    final ExceptionRecord msg = JSONUtil.toBean(content, ExceptionRecord.class);
                    log.info("消息队列-收到-队列: {}", MqSendService.BUSINESS_NAME);
                    try {
//                        boolean flag = exceptionRecordService.save(msg);
                        boolean flag = true;
                        log.info("保存异常记录结果：{}", flag);
                    } catch (Exception e) {
                        log.info("保存异常记录异常：{}", ExceptionUtil.stacktraceToString(e));
                        return ConsumeAction.REJECT;
                    }
                    return ConsumeAction.ACCEPT;
                });
    }

}
