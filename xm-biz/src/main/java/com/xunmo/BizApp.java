package com.xunmo;

import cn.hutool.core.date.StopWatch;
import com.xunmo.utils.LuaTool;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.logging.utils.LogUtilToSlf4j;
import org.redisson.api.RedissonClient;

@Slf4j
// @EnableQuartz
public class BizApp {

	static StopWatch start = new StopWatch("启动");

	public static void main(String[] args) throws NoSuchFieldException {
		Solon.start(BizApp.class, args, app -> {
			// 启用 WebSocket 服务
			app.enableWebSocket(true);

			// 转发日志到 Slf4j 接口
			LogUtil.globalSet(new LogUtilToSlf4j()); // v1.10.11 后支持

			app.get("/", ctx -> {
				// ctx.forward("/railway-bureau-test/index.html");
				// ctx.redirect("/dict/view/tree");
				// ctx.redirect("/employee_info/view/index");
				ctx.render("主页");
			});

			// 异步订阅方式，根据bean type获取Bean（已存在或产生时，会通知回调；否则，一直不回调）
			Solon.context().getBeanAsync(CacheService.class, bean -> {
				// bean 获取后，可以做些后续处理。。。
				log.info("app 异步订阅 CacheService, 执行初始化缓存动作");
			});

			// 异步订阅方式，根据bean type获取Bean（已存在或产生时，会通知回调；否则，一直不回调）
			Solon.context().getBeanAsync(RedissonClient.class, bean -> {
				// bean 获取后，可以做些后续处理。。。
				log.info("app 异步订阅 RedissonClient, 执行 初始化LuaTool 动作");
				LuaTool.setRedissonClient(bean);
			});

			// // 2.添加资源路径
			// StaticMappings.add("/railway-bureau-test", false, new
			// ClassPathStaticRepository("railway-bureau-test"));
			// // 3.添加扩展目录：${solon.extend}/static/
			// StaticMappings.add("/railway-bureau-test", relativePath -> {
			// String location;
			// String path = Solon.cfg().sourceLocation().getPath();
			// if (path == null) {
			// throw new IllegalStateException("No extension directory exists");
			// } else {
			// location = path + "railway-bureau-test";
			// }
			// File file = new File(location, relativePath);
			// return file.exists() ? file.toURI().toURL() : null;
			// });

			// 向外提供钩子
			// app.before(9999999, ctx -> {
			// final PageRequest pageRequest = ctx.paramAsBean(PageRequest.class);
			// ctx.paramSet("pageRequest", JSONUtil.toJsonStr(pageRequest));
			// });

		});
	}

}
