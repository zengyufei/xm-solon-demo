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
public enum IsImported implements BaseEnum<String> {

	@EnumItem(name = "3")
	NO("3", "否"), @EnumItem(name = "6")
	YES("6", "是"), @EnumItem(name = "9")
	OTHER("9", "其他"),;

	@JsonValue
	private final String code;

	private final String description;

	public static final Map<String, IsImported> codeMappings;

	public static final Map<String, IsImported> descMappings;

	static {
		codeMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getCode, e -> e)));
		descMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getDescription, e -> e)));
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static IsImported create(Object code) {
		return codeMappings.get(code.toString());
	}

}
