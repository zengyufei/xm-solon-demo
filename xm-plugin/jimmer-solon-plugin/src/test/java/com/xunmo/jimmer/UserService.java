package com.xunmo.jimmer;

import com.xunmo.jimmer.annotation.Db;
import org.babyfish.jimmer.sql.JSqlClient;
import org.noear.solon.annotation.ProxyComponent;

@ProxyComponent
public class UserService {

	final UserTable table = UserTable.$;
	@Db
	JSqlClient sqlClient;

	public int count() {
		return sqlClient.createQuery(table)
				.select(table)
				.count();
	}

}
