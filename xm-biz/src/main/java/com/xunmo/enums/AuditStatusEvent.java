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
public enum AuditStatusEvent implements ICodeEnum<String> {
    CREATE("3","新增保存"),
    SAVE("6","修改保存"),
    SUBMIT("9","发布"),
    AUDIT_PASS("12","审核通过"),
    AUDIT_NOPASS("15","审核不通过,退回"),
    CANCLE_AUDITING("18","取消审核"),
            ;

    private final String code;
    private final String description;

    private static final Map<String, AuditStatusEvent> cache = new HashMap<>();

    static {
        Stream.of(AuditStatusEvent.values()).forEach(item -> cache.put(item.getCode(), item));
    }

    public static AuditStatusEvent getByCode(Integer code) {
        return cache.get(code);
    }
}
