package com.xunmo.valid;

import cn.hutool.core.date.DateTime;
import com.xunmo.annotations.valid.IsDate;
import com.xunmo.core.utils.XmDateUtil;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.time.format.DateTimeFormatter;

public class IsDateValidator implements Validator<IsDate> {

	public static final IsDateValidator instance = new IsDateValidator();

	@Override
	public String message(IsDate anno) {
		return anno.message();
	}

	/**
	 * 校验强类型值
	 */
	@Override
	public Result validateOfValue(IsDate anno, Object val0, StringBuilder tmp) {
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
	public Result validateOfContext(Context ctx, IsDate anno, String name, StringBuilder tmp) {
		String val = ctx.param(name);

		if (!verify(anno, val)) {
			return Result.failure(name);
		}
		else {
			return Result.succeed();
		}
	}

	private boolean verify(IsDate anno, String val) {
		// 如果为空，算通过（交由@NotEmpty之类，进一步控制）
		if (Utils.isEmpty(val)) {
			return true;
		}

		try {
			if (Utils.isEmpty(anno.value())) {
				final DateTime dateTime = XmDateUtil.checkDateStr(val);
				return dateTime != null;
			}
			else {
				DateTimeFormatter.ofPattern(anno.value()).parse(val);
			}

			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

}
