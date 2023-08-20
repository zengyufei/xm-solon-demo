package com.xunmo.webs.users;

import com.xunmo.jimmer.annotation.Db;
import com.xunmo.jimmer.repository.JRepository;
import com.xunmo.webs.users.entity.Users;

@Db
public interface UsersRepository extends JRepository<Users, String> {

}
