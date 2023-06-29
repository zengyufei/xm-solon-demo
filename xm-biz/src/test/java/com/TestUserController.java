package com;

import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.noear.solon.annotation.Inject;
import org.noear.solon.serialization.jackson.JacksonActionExecutor;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUserController extends HttpTester {

    @Inject
    JacksonActionExecutor jacksonActionExecutor;


    @Test
    public void test_001_GetList() throws Exception {
        HttpUtils httpUtils = path("/user/list?size=10&page=1")
//                .bodyJson("{\"orgName\": \"Organization 2\"}")
//                .bodyJson("{\"roleName\": \"Role 10\"}")
                .bodyJson("{\"permissionName\": \"Permission 103\"}")
//                .bodyJson("{\"size\": 10, \"page\": 1, \"userName\": \"张三\"}")
                ;
        httpUtils.post();
    }


    @Test
    public void test_002_GetById() throws Exception {
        HttpUtils httpUtils = path("/user/getById?id=user_1009")
                .bodyJson("{}");
        httpUtils.post();
    }

    @Test
    public void test_003_Del() throws Exception {
        HttpUtils httpUtils = path("/user/deleteByIds")
                .bodyJson("[\"user_1009\"]");
        httpUtils.post();
    }

    @Test
    public void test_004_Add() throws Exception {
        final String data = "{\n" +
                "  \"userId\": \"user_1009\",\n" +
                "  \"userName\": \"User 1008\",\n" +
                "  \"createTime\": \"2023-06-29 11:49:40\",\n" +
                "  \"updateTime\": \"2023-06-29 13:32:46\",\n" +
                "  \"createId\": \"user_2450\",\n" +
                "  \"updateId\": \"user_9427\",\n" +
                "  \"approvalStatus\": \"1\",\n" +
                "  \"approverId\": \"user_1009\",\n" +
                "  \"approvalComment\": \"审批通过\",\n" +
                "  \"approvalTime\": \"2023-06-29 11:49:36\",\n" +
                "  \"isImported\": 0,\n" +
                "  \"importTime\": \"2023-06-29 11:49:36\",\n" +
                "  \"isSystemDefault\": 0,\n" +
                "  \"tenantId\": \"tenant1\",\n" +
                "  \"version\": 1,\n" +
                "  \"status\": \"active\"\n" +
                "}\n";
        HttpUtils httpUtils = path("/user/add")
                .bodyJson(data);
        httpUtils.post();
    }


    @Test
    public void test_005_Update() throws Throwable {
        final String data = "{\n" +
                "  \"userId\": \"user_1009\",\n" +
                "  \"userName\": \"我修改了名字哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦哦\",\n" +
                "  \"createTime\": \"2023-06-29 11:49:40\",\n" +
                "  \"updateTime\": \"2023-06-29 13:32:46\",\n" +
                "  \"createId\": \"user_2450\",\n" +
                "  \"updateId\": \"user_9427\",\n" +
                "  \"approvalStatus\": \"1\",\n" +
                "  \"approverId\": \"user_1009\",\n" +
                "  \"approvalComment\": \"审批通过\",\n" +
                "  \"approvalTime\": \"2023-06-29 11:49:36\",\n" +
                "  \"isImported\": 0,\n" +
                "  \"importTime\": \"2023-06-29 11:49:36\",\n" +
                "  \"isSystemDefault\": 0,\n" +
                "  \"tenantId\": \"tenant1\",\n" +
                "  \"version\": 1,\n" +
                "  \"status\": \"active\"\n" +
                "}\n";
        HttpUtils httpUtils = path("/user/update")
                .bodyJson(data);
        httpUtils.post();
    }


}
