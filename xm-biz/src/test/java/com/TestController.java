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
public class TestController extends HttpTester {

    @Inject
    JacksonActionExecutor jacksonActionExecutor;

    @Test
    public void test_001_Del() throws Exception {
        HttpUtils httpUtils = path("/user/deleteByIds")
                .bodyJson("[\"user_000001\"]");
        httpUtils.post();
    }

    @Test
    public void test_002_Add() throws Exception {
        final String data = "{\n" +
                "  \"userId\": \"user_000001\",\n" +
                "  \"userName\": \"User 000001\",\n" +
                "  \"createTime\": \"2023-04-13 11:36:34\",\n" +
                "  \"updateTime\": \"2023-03-19 11:36:34\",\n" +
                "  \"createId\": \"create_000001\",\n" +
                "  \"updateId\": \"update_000001\",\n" +
                "  \"createName\": \"Creator 000001\",\n" +
                "  \"updateName\": \"Updater 000001\",\n" +
                "  \"approvalStatus\": \"Rejected\",\n" +
                "  \"approverId\": \"approver_000001\",\n" +
                "  \"approverName\": \"Approver 000001\",\n" +
                "  \"approvalComment\": \"Comment 000001\",\n" +
                "  \"approvalTime\": \"2022-07-17 11:36:34\",\n" +
                "  \"isImported\": 1,\n" +
                "  \"importTime\": \"2022-12-21 11:36:34\",\n" +
                "  \"isSystemDefault\": 1,\n" +
                "  \"tenantId\": \"tenant_000001\",\n" +
                "  \"version\": 1,\n" +
                "  \"status\": \"Inactive\"\n" +
                "}\n";
        HttpUtils httpUtils = path("/user/add")
                .bodyJson(data);
        httpUtils.post();
    }


    @Test
    public void test_003_Update() throws Throwable {
        final String data = "{\n" +
                "  \"userId\": \"user_000001\",\n" +
                "  \"userName\": \"User 1111111\",\n" +
                "  \"createTime\": \"2023-04-13 11:36:34\",\n" +
                "  \"updateTime\": \"2023-03-19 11:36:34\",\n" +
                "  \"createId\": \"create_000001\",\n" +
                "  \"updateId\": \"update_000001\",\n" +
                "  \"createName\": \"Creator 000001\",\n" +
                "  \"updateName\": \"Updater 000001\",\n" +
                "  \"approvalStatus\": \"Rejected\",\n" +
                "  \"approverId\": \"approver_000001\",\n" +
                "  \"approverName\": \"Approver 000001\",\n" +
                "  \"approvalComment\": \"Comment 000001\",\n" +
                "  \"approvalTime\": \"2022-07-17 11:36:34\",\n" +
                "  \"isImported\": 1,\n" +
                "  \"importTime\": \"2022-12-21 11:36:34\",\n" +
                "  \"isSystemDefault\": 1,\n" +
                "  \"tenantId\": \"tenant_000001\",\n" +
                "  \"version\": 1,\n" +
                "  \"status\": \"Inactive\"\n" +
                "}\n";
        HttpUtils httpUtils = path("/user/update")
                .bodyJson(data);
        httpUtils.post();
    }


    @Test
    public void test_004_GetList() throws Exception {
        HttpUtils httpUtils = path("/user/list?size=10&page=1")
                .bodyJson("{}")
//                .bodyJson("{\"size\": 10, \"page\": 1, \"userName\": \"张三\"}")
                ;
        httpUtils.post();
    }


    @Test
    public void test_005_GetById() throws Exception {
        HttpUtils httpUtils = path("/user/getById?id=user_000002")
                .bodyJson("{}");
        httpUtils.post();
    }


}
