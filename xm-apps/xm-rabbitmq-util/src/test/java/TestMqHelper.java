import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.rabbitmq.entity.DeadConfig;
import com.xunmo.rabbitmq.entity.MqConfig;
import com.xunmo.rabbitmq.enums.ConsumeAction;
import com.xunmo.rabbitmq.enums.SendAction;
import com.xunmo.utils.MqHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMqHelper {

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
        AtomicBoolean isBreak = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(1);
        ThreadUtil.execute(() -> {
            while (!isBreak.get()) {
                final int andIncrement = count.getAndIncrement();
                if (andIncrement >= 90) {
                    isBreak.set(true);
                    countDownLatch.countDown();
                } else {
                    try {
                        log.debug("发送: {}秒, 复用 channel {}, 已创建发送 channel {}, 已创建消费 channel {}, 已关闭 channel {}",andIncrement,
                                MqHelper.getReChannelNames().size(),
                                MqHelper.getSendExistsChannelNames().size(),
                                MqHelper.getConsumerExistsChannelNames().size(),
                                MqHelper.getCloseChannelNames().size());
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .changeName("cxmb")
                    .queueName("qxmb")
                    .deadConfig(DeadConfig.of()
                            .changeName("csxb")
                            .queueName("qsxb")
                            .build())
                    .build();
            while (!isBreak.get()) {
                final String msg = "序号：ttt ,时间：" + DateUtil.now();
                try {
                    MqHelper.sendMsg(mqConfig, msg, sendAction -> {
                            log.debug("aaa 0号 生产者发送消息:" + msg);
                        if (SendAction.SUCCESS.equals(sendAction)) {
                            log.debug("aaa 0号 生产者发送消息 mq 接收成功");
                        } else {
                            log.debug("aaa 0号 生产者发送消息 mq 接收失败");
                        }
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
    }

    @Test
    public void consumer() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", 32701);
        AtomicBoolean isBreak = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(1);
        ThreadUtil.execute(() -> {
            while (!isBreak.get()) {
                final int andIncrement = count.getAndIncrement();
                if (andIncrement >= 90) {
                    isBreak.set(true);
                    countDownLatch.countDown();
                } else {
                    try {
                        log.trace("消费: " + andIncrement + "秒, " + MqHelper.getConsumerChannelCount().get());
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .changeName("cxmb")
                    .queueName("qxmb")
                    .deadConfig(DeadConfig.of()
                            .changeName("csxb")
                            .queueName("qsxb")
                            .build())
                    .build();
            while (!isBreak.get()) {
                try {
                    MqHelper.consumeMsg(mqConfig, s -> {
                        log.debug("ttt 1号 消费者收到消息:" + new String(s) + ",当前时间:" + DateUtil.now());
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return ConsumeAction.REJECT;
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
    }


    @Test
    public void consumerDead() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", 32701);
        AtomicBoolean isBreak = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(1);
        ThreadUtil.execute(() -> {
            while (!isBreak.get()) {
                final int andIncrement = count.getAndIncrement();
                if (andIncrement >= 90) {
                    isBreak.set(true);
                    countDownLatch.countDown();
                } else {
                    try {
                        log.trace("死信: " + andIncrement + "秒");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        ThreadUtil.execute(() -> {
            final MqConfig mqConfig = MqConfig.of()
                    .changeName("csxb")
                    .queueName("qsxb")
                    .build();
            while (!isBreak.get()) {
                try {
                    MqHelper.consumeMsg(mqConfig, s -> {
                        log.debug("死信 2号 收到消息:" + new String(s) + ",当前时间:" + DateUtil.now());
                        return ConsumeAction.ACCEPT;
                    });
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
    }


}
