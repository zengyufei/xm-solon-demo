package com.xunmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.xunmo.common.BaseEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.babyfish.jimmer.sql.EnumItem;
import org.babyfish.jimmer.sql.EnumType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EnumType(value = EnumType.Strategy.NAME)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UsersStatus implements BaseEnum<String> {

	@EnumItem(name = "3")
	NONE("3", "默认"), @EnumItem(name = "6")
	ACTIVE("6", "生效"), @EnumItem(name = "9")
	VALID("9", "等待验证"), @EnumItem(name = "12")
	STOP("12", "禁用"),;

	@JsonValue
	private final String code;

	private final String description;

	public static final Map<String, UsersStatus> codeMappings;

	public static final Map<String, UsersStatus> descMappings;

	static {
		codeMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getCode, e -> e)));
		descMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getDescription, e -> e)));
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static UsersStatus create(Object code) {
		return codeMappings.get(code.toString());
	}

}
