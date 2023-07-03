import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.entity.DeadConfig;
import com.xunmo.entity.MqConfig;
import com.xunmo.enums.ConsumeAction;
import com.xunmo.enums.SendAction;
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

//        ThreadUtil.execute(() -> {
//            try {
//                System.out.println("运行 死信 消费");
//                consumerDead();
//                countDownLatch.countDown();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });

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
                        System.out.println("发送: " + andIncrement + "秒, " + MqHelper.getSendChannelCount().get());
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
                MqHelper.sendMsg(mqConfig, "序号：ttt ,时间：" + DateUtil.now(), sendAction -> {
                    if (SendAction.SUCCESS.equals(sendAction)) {
                        System.out.println("mq 接收成功");
                    } else {
                        System.out.println("mq 接收失败");
                    }
                });
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
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
                        System.out.println("消费: " + andIncrement + "秒, " + MqHelper.getConsumerChannelCount().get());
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
                        System.out.println("ttt 1号 消费者收到消息:" + new String(s) + ",当前时间:" + DateUtil.now());
                        return ConsumeAction.REJECT;
                    });
                    TimeUnit.MILLISECONDS.sleep(500);
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
                        System.out.println(andIncrement + "秒");
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
                        System.out.println("死信 1号 收到消息:" + new String(s) + ",当前时间:" + DateUtil.now());
                        return ConsumeAction.ACCEPT;
                    });
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countDownLatch.await();
    }


}
