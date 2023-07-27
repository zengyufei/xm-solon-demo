package com.xunmo.valid;

import cn.hutool.core.util.StrUtil;
import com.xunmo.annotations.valid.IsEnum;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class IsEnumValidator implements Validator<IsEnum> {

	public static final IsEnumValidator instance = new IsEnumValidator();

	@Override
	public String message(IsEnum anno) {
		return anno.message();
	}

	/**
	 * 校验强类型值
	 */
	@Override
	public Result validateOfValue(IsEnum anno, Object val0, StringBuilder tmp) {
		if (val0 != null && !(val0 instanceof String)) {
			return Result.failure();
		}

		String val = (String) val0;

		if (!verify(anno, val)) {
			return Result.failure();
		}
		else {
			return Result.succeed();
		}
	}

	/**
	 * 校验上下文的参数
	 */
	@Override
	public Result validateOfContext(Context ctx, IsEnum anno, String name, StringBuilder tmp) {
		String val = ctx.param(name);

		if (!verify(anno, val)) {
			return Result.failure(name);
		}
		else {
			return Result.succeed();
		}
	}

	private boolean verify(IsEnum anno, String val) {
		// 如果为空，算通过（交由@NotEmpty之类，进一步控制）
		if (Utils.isEmpty(val)) {
			return true;
		}

		Class<? extends Enum<?>> enumClass = anno.value();
		Enum<?>[] enumValues = enumClass.getEnumConstants();
		for (Enum<?> enumValue : enumValues) {
			if (StrUtil.equalsIgnoreCase(enumValue.name(), val)) {
				return true;
			}
		}
		return false;
	}

}
