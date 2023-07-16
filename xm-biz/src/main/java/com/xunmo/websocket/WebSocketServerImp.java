package com.xunmo.websocket;

import org.noear.solon.annotation.ServerEndpoint;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

/**
 * ws://127.0.0.1:18080/hello/12?token=xxx
 *
 * @author zengyufei
 * @date 2023/07/15
 */
@ServerEndpoint(path = "/hello/{id}")
public class WebSocketServerImp implements Listener {

	@Override
	public void onOpen(Session session) {
		// path var
		String id = session.param("id");
		// query var
		String token = session.param("token");

		System.out.println("id: " + id + ", token: " + token);
	}

	@Override
	public void onMessage(Session session, Message message) {
		// message.setHandled(true); //设为true，则不进入mvc路由

		session.send("我收到了：" + message.bodyAsString());

		// 接收二进制
		// byte[] bytes = message.body();
		// 发送二进制
		// session.send(Message.wrap(new byte[0]));
	}

}
