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
public enum ApprovalStatus implements BaseEnum<String> {

	@EnumItem(name = "3")
	DRAFT("3", "草稿"), @EnumItem(name = "6")
	WAIT("6", "审核中"), @EnumItem(name = "9")
	SUCCESS("9", "审核通过"), @EnumItem(name = "12")
	FAIL("12", "审核失败"),;

	@JsonValue
	private final String code;

	private final String description;

	public static final Map<String, ApprovalStatus> codeMappings;

	public static final Map<String, ApprovalStatus> descMappings;

	static {
		codeMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getCode, e -> e)));
		descMappings = Collections
			.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(BaseEnum::getDescription, e -> e)));
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING) // 支持post请求转换枚举
	public static ApprovalStatus create(Object code) {
		return codeMappings.get(code.toString());
	}

}
