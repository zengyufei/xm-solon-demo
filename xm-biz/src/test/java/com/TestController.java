package com;

import com.xunmo.BizApp;
import com.xunmo.common.utils.XmUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestController extends HttpTestBase {

    @Test
    public void testChangeSort() throws Exception {
        HttpUtils httpUtils = path("/test/test4?preId=123&nextId=321")
                .bodyJson("{}");
        httpUtils.post();
    }

    @Test
    public void test4() throws Exception {
        HttpUtils httpUtils = path("/test/test4/456/789");
        httpUtils.get();
    }

    @Test
    public void test5() throws Exception {
        final String sortVar = XmUtil.getSortVar();
        System.out.println(sortVar);
    }


}
