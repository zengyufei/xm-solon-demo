package com.xunmo.jimmer;

import org.noear.solon.Solon;

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {

	public static void main(String[] args) {
		Solon.start(DemoApp.class, args);

		final UserService userService = Solon.context().getBean(UserService.class);
		// test
		final int count = userService.count();
		System.out.println(count);
	}

}
