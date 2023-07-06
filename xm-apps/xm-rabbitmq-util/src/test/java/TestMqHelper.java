import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.rabbitmq.entity.DeadConfig;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ConsumeAction;
import com.xunmo.rabbitmq.enums.SendAction;
import com.xunmo.utils.MqHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMqHelper {

    private static AtomicBoolean countSecond(CountDownLatch countDownLatch, int maxSecond, Consumer<Integer> consumer) {
        AtomicBoolean isBreak = new AtomicBoolean(false);
        AtomicInteger count   = new AtomicInteger(1);
        ThreadUtil.execute(() -> {
            while (!isBreak.get()) {
                final int andIncrement = count.getAndIncrement();
                if (andIncrement >= maxSecond) {
                    isBreak.set(true);
                    countDownLatch.countDown();
                }
                else {
                    try {
                        consumer.accept(andIncrement);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return isBreak;
    }

    @Test
    public void test_001_run() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        ThreadUtil.execute(() -> {
            try {
                System.out.println("运行 正常 消费");
                consumer();
                countDownLatch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                System.out.println("运行 死信 消费");
                consumerDead();
                countDownLatch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                System.out.println("运行 发送");
                send();
                countDownLatch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        countDownLatch.await();
    }

    @Test
    public void send() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", 32701);
        AtomicBoolean isBreak = countSecond(countDownLatch, 900, second -> {
            log.debug("发送: {}秒", second);
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .title("生产者")
                    .changeName("cxmb")
                    .queueName("qxmb")
                    .deadConfig(DeadConfig.of()
                            .changeName("csxb")
                            .queueName("qsxb")
                            .build())
                    .build();
            while (!isBreak.get()) {
                final String now = DateUtil.now();
                log.trace("send while:  " + now);
                final String msg = StrUtil.format("【{}】 序号：ttt ,时间：{}", IdUtil.fastSimpleUUID(), now);
                try {
                    MqHelper.sendMsg(mqConfig, msg, (channel, sendAction) -> {
                        log.info("生产者 {}号 发送消息: {}", channel.getChannelNumber(), msg);
                        if (SendAction.SUCCESS.equals(sendAction)) {
                            log.debug("aaa 0号 生产者发送消息 mq 接收成功");
                        }
                        else {
                            log.debug("aaa 0号 生产者发送消息 mq 接收失败");
                        }
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
//                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
        System.out.println("send()  完成任务!");
    }

    @Test
    public void consumer() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", 32701);
        AtomicBoolean isBreak = countSecond(countDownLatch, 900, second -> {
            log.trace("消费: " + second + "秒");
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .title("消费者")
                    .changeName("cxmb")
                    .queueName("qxmb")
                    .deadConfig(DeadConfig.of()
                            .changeName("csxb")
                            .queueName("qsxb")
                            .build())
                    .build();
            while (!isBreak.get()) {
                final String now = DateUtil.now();
                log.trace("consumer while:  " + now);
                try {
                    MqHelper.consumeMsg(mqConfig, (channel, s) -> {
                        log.info("消费者 {}号 收到消息:{}", channel.getChannelNumber(), (new String(s) + ",当前时间:" + now));
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return ConsumeAction.REJECT;
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
//                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
        System.out.println("consumer()  完成任务!");
    }

    @Test
    public void consumerDead() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", 32701);
        AtomicBoolean isBreak = countSecond(countDownLatch, 900, second -> {
            log.trace("死信: " + second + "秒");
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .title("死信")
                    .changeName("csxb")
                    .queueName("qsxb")
                    .build();
            while (!isBreak.get()) {
                final String now = DateUtil.now();
                log.trace("consumerDead while:  " + now);
                try {
                    MqHelper.consumeMsg(mqConfig, (channel, s) -> {
                        log.info("死信 {}号 收到消息:{}", channel.getChannelNumber(), (new String(s) + ",当前时间:" + now));
                        return ConsumeAction.ACCEPT;
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
//                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
        System.out.println("consumerDead()  完成任务!");
    }
}
