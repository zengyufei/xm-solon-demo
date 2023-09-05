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
public enum ApprovalStatus implements BaseEnum<String> {

	DRAFT("3", ""),
	WAIT("6", ""),
	SUCCESS("9", ""),
	FAIL("12", ""),
	;

	@JsonValue
	private final String code;
	private final String description;


	private static final Map<String, ApprovalStatus> mappings;

	static {
		Map<String, ApprovalStatus> temp = new HashMap<>();
		for (ApprovalStatus courseType : values()) {
			temp.put(courseType.code, courseType);
		}
		mappings = Collections.unmodifiableMap(temp);
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static ApprovalStatus create(Object code) {
		return mappings.get(code.toString());
	}
}
