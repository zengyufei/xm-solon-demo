package com.xunmo.webs.user;

import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestUser extends HttpTestBase {

    @Test
    public void testGetListAndAdd() throws Exception {
        page();
        add();
    }

    @Test
    public void list() throws Exception {
        String json = path("/user/list")
                .bodyJson("{" +
                        "parentId: '-1',\n" +
                        "dicDescription: '',\n" +
                        "dicCode: '',\n" +
                        "dicValue: ''" +
                        "}")
                .post();
        ONode node = ONode.load(json);
        assert node.get("code").getInt() == 200;
    }

    @Test
    public void add() throws Exception {
        String json = path("/user/add")
                .bodyJson("{\n" +
                        "  \"avatar\": \"fake_data\",\n" +
                        "  \"nickname\": \"fake_data\",\n" +
                        "  \"fullname\": \"fake_data\",\n" +
                        "  \"sex\": 74,\n" +
                        "  \"birthday\": \"2017-03-11 00:06:58\",\n" +
                        "  \"phone\": \"fake_data\",\n" +
                        "  \"email\": \"fake_data\",\n" +
                        "  \"errorTimes\": 47,\n" +
                        "  \"loginTime\": \"2031-11-12 10:46:46\",\n" +
                        "  \"loginIp\": \"fake_data\",\n" +
                        "  \"logoutTime\": \"2017-07-10 20:29:29\",\n" +
                        "  \"registerTime\": \"2031-02-11 13:19:08\",\n" +
                        "  \"status\": \"fake_data\",\n" +
                        "  \"isTenantAccount\": 18\n" +
                        "}")
                .post();
        ONode node = ONode.load(json);
        assert node.get("code").getInt() == 200;
    }

    @Test
    public void page() throws Exception {
        String json = path("/user/list?pageNo=2&pageSize=2&orderBy=birthday")
                .bodyJson("{\n" +
                        "  \"avatar\": \"\",\n" +
                        "  \"nickname\": \"\",\n" +
                        "  \"fullname\": \"\",\n" +
                        "  \"sex\": null,\n" +
                        "  \"birthday\": \"\",\n" +
                        "  \"phone\": \"\",\n" +
                        "  \"email\": \"\",\n" +
                        "  \"errorTimes\": null,\n" +
                        "  \"loginTime\": \"\",\n" +
                        "  \"loginIp\": \"\",\n" +
                        "  \"logoutTime\": \"\",\n" +
                        "  \"registerTime\": \"\",\n" +
                        "  \"status\": \"\",\n" +
                        "  \"isTenantAccount\": null\n" +
                        "}")
                .post();
        ONode node = ONode.load(json);
        assert node.get("code").getInt() == 200;
    }

}
