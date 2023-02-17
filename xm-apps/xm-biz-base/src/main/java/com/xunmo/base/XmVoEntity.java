package com.xunmo.base;

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
public class XmVoEntity extends XmIdEntity {

    /**
     * 操作权限标识： 0-无限制 1-有权限 2-只能删除和修改 3-只能删除 4-只能修改 5-无权限，其他业务自定义
     */
    private String edit = "0";

    /**
     * 显示权限标识： 0-不能看 1-能看
     */
    private String isShow = "1";

}
