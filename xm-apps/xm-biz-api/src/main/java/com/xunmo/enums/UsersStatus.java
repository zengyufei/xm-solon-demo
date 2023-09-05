package com.xunmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.xunmo.common.BaseEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UsersStatus implements BaseEnum<String> {

	NONE("3", ""),
	ACTIVE("6", ""),
	VALID("9", ""),
	STOP("12", ""),
	;

	@JsonValue
	private final String code;
	private final String description;

	private static final Map<String, UsersStatus> mappings;

	static {
		Map<String, UsersStatus> temp = new HashMap<>();
		for (UsersStatus courseType : values()) {
			temp.put(courseType.code, courseType);
		}
		mappings = Collections.unmodifiableMap(temp);
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static UsersStatus create(Object code) {
		return mappings.get(code.toString());
	}
}
