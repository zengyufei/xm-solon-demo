package com.xunmo.webs.department;

import com.xunmo.BizApp;
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
public class TestDepartment extends HttpTestBase {

    @Test
    public void testChangeSort() throws Exception {
        HttpUtils httpUtils = path("/department/changeSort?preId=1583288786544308226&nextId=1583288786133266433")
                .bodyJson("{}");
        httpUtils.post();
    }


}
