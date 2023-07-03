import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.xunmo.utils.MqHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TestMqHelper {

    @Test
    @Order(1)
    public void test0() throws Exception {
//        TimeUnit.SECONDS.sleep(30);
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", "32701");
        MqHelper.sendMsg("ttt","序号：ttt ,时间："+ DateUtil.now());
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("bbb","序号：bbb ,时间："+ DateUtil.now());
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","序号：ttt1 ,时间："+ DateUtil.now());
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("bbb","序号：bbb1 ,时间："+ DateUtil.now());
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","序号：ttt2 ,时间："+ DateUtil.now());
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","序号：ttt3 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("ttt","序号：ttt4 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("ttt","序号：ttt5 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("ttt","序号：ttt6 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("bbb","序号：bbb2 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("bbb","序号：bbb3 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("bbb","序号：bbb4 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("bbb","序号：bbb5 ,时间："+ DateUtil.now());
        MqHelper.sendMsg("bbb","序号：bbb6 ,时间："+ DateUtil.now());
    }

    @Test
    @Order(2)
    public void test1() throws Exception {
        MqHelper.initMq("vat", "root", "*xun13@mo14!154RConf!*", "32701");
        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "ttt",true, s -> {
                    System.out.println("bbb1号 消费者收到消息:" + new String(s)+",当前时间:"+ DateUtil.now());
                    try {
                        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(1,5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "bbb",true, s -> {
                    System.out.println("bbb2号 消费者收到消息:" + new String(s)+",当前时间:"+ DateUtil.now());
                    try {
                        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(1,5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        TimeUnit.SECONDS.sleep(5);

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "ttt",false, s -> {
                    System.out.println("ttt 消费者收到消息:" + new String(s)+",当前时间:"+ DateUtil.now());
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "bbb",false, s -> {
                    System.out.println("bbb3号 消费者收到消息:" + new String(s)+",当前时间:"+ DateUtil.now());
                    try {
                        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(1,5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        TimeUnit.SECONDS.sleep(90);
    }



}
