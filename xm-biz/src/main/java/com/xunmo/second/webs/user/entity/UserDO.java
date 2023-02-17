package com.xunmo.second.webs.user.entity;

import com.xunmo.base.XmDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class UserDO<T> extends XmDO<T> {

    private String avatar;
    private String nickname;
    private String fullname;

    /**
     * 性别：0-女；1-男；2-未知；
     */
    private String sex;
    private String birthday;
    private String phone;
    private String email;

    /**
     * 状态: normal-正常; forbidden-禁用;
     */
    private String status;

}
