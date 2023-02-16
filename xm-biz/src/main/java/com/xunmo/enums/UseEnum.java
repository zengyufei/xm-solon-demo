package com.xunmo.enums;

import com.xunmo.ext.ICodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UseEnum implements ICodeEnum<Integer> {

    USE(1, "使用"),
    FORBIDDEN(0, "禁用"),
    ;

    private Integer code;
    private String description;

}
