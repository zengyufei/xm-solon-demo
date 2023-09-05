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
public enum IsImported implements BaseEnum<String> {

	NO("3", ""),
	YES("6", ""),
	OTHER("9", ""),
	;

	@JsonValue
	private final String code;
	private final String description;


	private static final Map<String, IsImported> mappings;

	static {
		Map<String, IsImported> temp = new HashMap<>();
		for (IsImported courseType : values()) {
			temp.put(courseType.code, courseType);
		}
		mappings = Collections.unmodifiableMap(temp);
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static IsImported create(Object code) {
		return mappings.get(code.toString());
	}

}
