package com.xunmo.annotations;

import cn.hutool.core.util.StrUtil;
import com.xunmo.jackson.desensitize.StringDesensitization;

/**
 *
 */
public class ThousandDesensitization implements StringDesensitization {

	@Override
	public String desensitize(String target) {
		if (StrUtil.isBlank(target)) {
			return target;
		}
		return StrUtil.replace(target, ",", "");
	}

}
