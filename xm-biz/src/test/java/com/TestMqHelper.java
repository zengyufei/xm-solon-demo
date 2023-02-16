package com;

import cn.hutool.core.thread.ThreadUtil;
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
    @Order(2)
    public void test0() throws Exception {
        TimeUnit.SECONDS.sleep(30);
        MqHelper.initMq("127.0.0.1", "root", "root", "5672");
        MqHelper.sendMsg("ttt","demo1");
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("bbb","bbb2");
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","demo2");
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("bbb","bb3");
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","demo3");
        TimeUnit.SECONDS.sleep(1);
        MqHelper.sendMsg("ttt","demo5");
        MqHelper.sendMsg("ttt","demo6");
        MqHelper.sendMsg("ttt","demo7");
        MqHelper.sendMsg("ttt","demo8");
        MqHelper.sendMsg("bbb","bb5");
        MqHelper.sendMsg("bbb","bb6");
        MqHelper.sendMsg("bbb","bb7");
        MqHelper.sendMsg("bbb","bb8");
        MqHelper.sendMsg("bbb","bb9");
    }

    @Test
    @Order(1)
    public void test1() throws Exception {
        MqHelper.initMq("127.0.0.1", "root", "*!Xunmo@AiQing68951@zyf!*", "5672");
        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "ttt",true, s -> {
                    System.out.println("ttt: "+s);
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "bbb",true, s -> {
                    System.out.println("bb: "+s);
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        TimeUnit.SECONDS.sleep(5);

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "ttt",false, s -> {
                    System.out.println("ttt2: "+s);
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        ThreadUtil.execute(() -> {
            try {
                MqHelper.consumeMsg( "bbb",false, s -> {
                    System.out.println("bb2: "+s);
                    return MqHelper.ConsumeAction.ACCEPT;
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        });

        TimeUnit.SECONDS.sleep(90);
    }



}
