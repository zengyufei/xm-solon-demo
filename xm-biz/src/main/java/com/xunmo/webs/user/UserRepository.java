package com.xunmo.webs.user;


import com.xunmo.jimmer.repository.JRepository;
import com.xunmo.webs.user.entity.User;

public interface UserRepository extends JRepository<User, String> {
}