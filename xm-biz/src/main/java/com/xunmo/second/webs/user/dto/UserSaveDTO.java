package com.xunmo.second.webs.user.dto;

import com.xunmo.second.webs.user.entity.User;
import com.xunmo.second.webs.user.entity.UserDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserSaveDTO extends UserDO<User> {
}
