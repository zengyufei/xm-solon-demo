package com.xunmo.valid;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.annotations.valid.IsNumber;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class IsNumberValidator implements Validator<IsNumber> {

	public static final IsNumberValidator instance = new IsNumberValidator();

	@Override
	public String message(IsNumber anno) {
		return anno.message();
	}

	/**
	 * 校验强类型值
	 */
	@Override
	public Result validateOfValue(IsNumber anno, Object val0, StringBuilder tmp) {
		if (val0 != null && !(val0 instanceof String)) {
			return Result.failure();
		}

		String val = (String) val0;

		if (!verify(val)) {
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
	public Result validateOfContext(Context ctx, IsNumber anno, String name, StringBuilder tmp) {
		String val = ctx.param(name);

		if (!verify(val)) {
			return Result.failure(name);
		}
		else {
			return Result.succeed();
		}
	}

	private boolean verify(String val) {
		// 如果为空，算通过（交由@NotEmpty之类，进一步控制）
		if (Utils.isEmpty(val)) {
			return true;
		}
		if (StrUtil.isBlankOrUndefined(val)) {
			return true;
		}
		return NumberUtil.isNumber(val);
	}

}
