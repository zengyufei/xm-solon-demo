package com.xunmo;

import org.noear.solon.Solon;

/**
 * 启动 
 * @author kong 
 */
public class GenCodeApp {
	public static void main(String[] args) {
		Solon.start(GenCodeApp.class, args, app -> {

			// 自动添加 multipart
			app.filter(-1, (ctx, chain) -> {
				if (ctx.path().startsWith("/upload")) {
					ctx.autoMultipart(true); //给需要的路径加 autoMultipart
				}
				chain.doFilter(ctx);
			});
		});
	}
	
}
