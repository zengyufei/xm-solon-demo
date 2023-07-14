package com.xunmo.mq.exceptionRecord;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ConsumeAction;
import com.xunmo.utils.MqHelper;
import com.xunmo.webs.exceptionRecord.entity.ExceptionRecord;
import com.xunmo.webs.exceptionRecord.input.ExceptionRecordInput;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MqConsumerService implements EventListener<AppLoadEndEvent> {

	public static final AtomicBoolean isInit = new AtomicBoolean(false);

	public static MqConfig mqConfig;

	@Inject
	private JacksonActionExecutor jacksonActionExecutor;

	@Inject
	private JSqlClient sqlClient;

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
			isInit.compareAndSet(false, true);
		}
	}

	@Override
	public void onEvent(AppLoadEndEvent event) throws Throwable {
		ThreadUtil.execute(() -> {
			log.info("启动 {} 消费队列", MqSendService.BUSINESS_NAME);
			final ObjectMapper objectMapper = jacksonActionExecutor.config();
			try {
				MqHelper.consumeMsg(mqConfig, (channel, content) -> {
					final ExceptionRecordInput exceptionRecordInput;
					try {
						exceptionRecordInput = objectMapper.readValue(content, ExceptionRecordInput.class).toUpdate();
					}
					catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					log.info("{} 消息队列-收到-队列: {} {} {}", MqSendService.BUSINESS_NAME, exceptionRecordInput.getMethod(),
							exceptionRecordInput.getUri(), exceptionRecordInput.getParams());
					try {
						final SimpleSaveResult<ExceptionRecord> saveResult = sqlClient.save(exceptionRecordInput,
								SaveMode.INSERT_ONLY);
						log.info("保存异常记录结果：{}", saveResult.getTotalAffectedRowCount());
					}
					catch (Exception e) {
						log.info("保存异常记录异常：{}", ExceptionUtil.stacktraceToString(e));
						return ConsumeAction.REJECT;
					}
					return ConsumeAction.ACCEPT;
				});
			}
			catch (IOException | TimeoutException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
