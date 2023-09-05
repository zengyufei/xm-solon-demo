package com.xunmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.xunmo.common.BaseEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EnumType(EnumType.Strategy.NAME)
public enum IsSystem implements BaseEnum<String> {


	NO("3", ""),
	YES("6", ""),
	OTHER("9", ""),
	;

	@JsonValue
	private final String code;
	private final String description;

	private static final Map<String, IsSystem> mappings;

	static {
		Map<String, IsSystem> temp = new HashMap<>();
		for (IsSystem courseType : values()) {
			temp.put(courseType.code, courseType);
		}
		mappings = Collections.unmodifiableMap(temp);
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static IsSystem create(Object code) {
		return mappings.get(code.toString());
	}
}
