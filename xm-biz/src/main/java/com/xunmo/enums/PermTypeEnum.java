package com.xunmo.enums;

import com.xunmo.ext.ICodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限类型 枚举
 *
 * @author zengyufei
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermTypeEnum implements ICodeEnum<Integer> {
    /**
     * 菜单
     */
    MENU(0, "菜单"),
    /**
     * 按钮
     */
    BUTTON(1, "按钮"),
    /**
     * 请求
     */
    REQUEST(2, "请求"),
    ;

    private Integer code;
    private String description;
}
