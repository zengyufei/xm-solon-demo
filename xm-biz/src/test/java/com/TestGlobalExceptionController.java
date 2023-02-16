package com;

import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestGlobalExceptionController extends HttpTestBase {

    @Test
    @Order(1)
    public void testNullPointException() throws Exception {
        HttpUtils httpUtils = path("/exception/null")
                .bodyJson("{}");
        httpUtils.post();
    }
    @Test
    @Order(2)
    public void testArithmeticException() throws Exception {
        HttpUtils httpUtils = path("/exception/arithmetic")
                .bodyJson("{}");
        httpUtils.post();
    }
    @Test
    @Order(3)
    public void testDefaultException() throws Exception {
        HttpUtils httpUtils = path("/exception/exception")
                .bodyJson("{}");
        httpUtils.post();
    }


}
