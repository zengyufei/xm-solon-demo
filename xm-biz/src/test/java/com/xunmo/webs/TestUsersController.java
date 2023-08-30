package com.xunmo.webs;

import cn.hutool.core.util.RandomUtil;
import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.noear.solon.annotation.Condition;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@Slf4j
@Condition(onClass = CacheService.class)
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUsersController extends HttpTester {

	@Test
	public void test_001_GetList() throws Exception {
		HttpUtils httpUtils = path("/users/list?size=10&page=1")
				// .bodyJson("{\"orgName\": \"Organization 2\"}")
				// .bodyJson("{\"roleName\": \"Role 10\"}")
				// .bodyJson("{\"permissionName\": \"Permission 103\"}")
				// .bodyJson("{\"size\": 10, \"page\": 1, \"userName\": \"张三\"}")
				.bodyJson("{}");
		httpUtils.post();
	}

	@Test
	public void test_002_Add() throws Exception {
		final String data = "["
				+ "  {\n" + "  \"usersId\": \"user_77009\",\n" + "  \"userName\": \"User 1008\",\n"
//				+ "  \"createTime\": \"2023-06-29 11:49:40\",\n" + "  \"updateTime\": \"2023-06-29 13:32:46\",\n"
				+ "  \"createId\": \"user_2450\",\n" + "  \"updateId\": \"user_9427\",\n"
				+ "  \"approvalStatus\": \"1\",\n" + "  \"approverId\": \"user_1009\",\n"
				+ "  \"approvalComment\": \"审批通过\",\n" + "  \"approvalTime\": \"2023-06-29 11:49:36\",\n"
				+ "  \"isImported\": 0,\n" + "  \"importTime\": \"2023-06-29 11:49:36\",\n"
				+ "  \"isSystemDefault\": 0,\n" + "  \"tenantId\": \"tenant1\",\n" + "  \"version\": 1,\n"
				+ "  \"status\": \"active\",\n"
				+ "  \"organization\": {organizationId: \"org_12\"},\n"
				+ "  \"roles\": [{roleId: \"role_4\"}]\n"
				+ "  }"
				+ "]";
		HttpUtils httpUtils = path("/users/adds").bodyJson(data);
		httpUtils.post();
	}

	@Test
	public void test_003_Update() throws Throwable {
		final String data = "[{\n" + "  \"usersId\": \"user_77009\",\n" + "  \"userName\": \"我修改了名字"
				+ (RandomUtil.randomString(15)) + "\",\n" + "  \"createTime\": \"2023-06-29 11:49:40\",\n"
				+ "  \"updateTime\": \"2023-06-29 13:32:46\",\n" + "  \"createId\": \"user_2450\",\n"
				+ "  \"updateId\": \"user_9427\",\n" + "  \"approvalStatus\": \"1\",\n"
				+ "  \"approverId\": \"user_1009\",\n" + "  \"approvalComment\": \"审批通过\",\n"
				+ "  \"approvalTime\": \"2023-06-29 11:49:36\",\n" + "  \"isImported\": 0,\n"
				+ "  \"importTime\": \"2023-06-29 11:49:36\",\n" + "  \"isSystemDefault\": 0,\n"
				+ "  \"tenantId\": \"tenant1\",\n" + "  \"version\": 1,\n" +
				// " \"status\": \"active\"\n" +
				"  \"status\": null,\n"
				+ "  \"organization\": {organizationId: \"org_0\"},\n"
				+ "  \"roles\": [{roleId: \"role_9\"}]\n"
				+ "}]";
		HttpUtils httpUtils = path("/users/updates").timeout(5, -1, -1).bodyJson(data);
		httpUtils.post();
	}

	@Test
	public void test_004_GetById() throws Exception {
		HttpUtils httpUtils = path("/users/getByIds").bodyJson("[\"user_77009\"]");
		httpUtils.post();
	}

	@Test
	public void test_005_Del() throws Exception {
		HttpUtils httpUtils = path("/users/delByIds").bodyJson("[\"user_77009\"]");
		httpUtils.post();
	}

}
