package com.xunmo.enums;

import com.xunmo.ext.ICodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuditStatusEnum implements ICodeEnum<String> {
    EMPTY("0","还没草稿的时候"),
    DRAFT("3","草稿"),
    AUDITING("6","待审核"),
    PUBLISHED("9","已发布"),
    RETURNED("12","已退回"),
    TO_DRAFT("15","正式转草稿发布"),

    SECOND_AUDITING("18","第二次待审核"),
            ;

    private final String code;
    private final String description;

    private static final Map<String, AuditStatusEnum> cache = new HashMap<>();

    static {
        Stream.of(AuditStatusEnum.values()).forEach(item -> cache.put(item.getCode(), item));
    }

    public static AuditStatusEnum getByCode(String code) {
        return cache.get(code);
    }
}
