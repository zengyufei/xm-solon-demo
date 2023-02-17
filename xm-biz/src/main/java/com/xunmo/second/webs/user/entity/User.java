package com.xunmo.second.webs.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xunmo.base.XmEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户基本信息表
 *
 * @author zengyufei
 * @date 2023/02/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_user")
@EqualsAndHashCode(callSuper = true)
public class User extends XmEntity {

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
