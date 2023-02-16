package com.xunmo.webs.attachment;

import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestAttachment extends HttpTestBase {

    @Test
    public void testMove() throws Exception {
        final String s = path("/attachment/move/123/321").get();
        System.out.println(s);
    }
    @Test
    public void testMove2() throws Exception {
        final String s = path("/attachment/test4/123/321").get();
        System.out.println(s);
    }

}
