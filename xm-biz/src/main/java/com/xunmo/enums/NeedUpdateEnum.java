package com.xunmo.enums;

import com.xunmo.ext.ICodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 是否需要更新枚举
 *
 * @author zengyufei
 * @date 2022/06/27
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NeedUpdateEnum implements ICodeEnum<String> {
    NO("3","否"),
    YES("6","是"),
    ;

    private String code;
    private String description;

    private static final Map<String, NeedUpdateEnum> cache = new HashMap<>();

    static {
        Stream.of(NeedUpdateEnum.values()).forEach(item -> cache.put(item.getCode(), item));
    }

    public static NeedUpdateEnum getByCode(Integer code) {
        return cache.get(code);
    }

}
