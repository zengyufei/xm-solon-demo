package com.xunmo.second.webs.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.base.XmServiceImpl;
import com.xunmo.common.utils.XmUtil;
import com.xunmo.second.webs.user.dto.UserGetPageDTO;
import com.xunmo.second.webs.user.entity.User;
import com.xunmo.second.webs.user.mapper.UserMapper;
import com.xunmo.webs.dict.entity.Dict;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

import java.util.List;

@Slf4j
@Service
public class UserService extends XmServiceImpl<UserMapper, User> {

    @Inject
    private UserMapper userMapper;

    /**
     * 获取列表
     *
     * @param userGetPageDTO dict获取页面dto
     * @return {@link List}<{@link User}>
     */
    public List<User> getList(UserGetPageDTO userGetPageDTO) {
        final User user = userGetPageDTO.toEntity();
        return XmUtil.startPage(Dict.class, () -> this.baseMapper.selectList(Wrappers.lambdaQuery(user)));
    }

}
