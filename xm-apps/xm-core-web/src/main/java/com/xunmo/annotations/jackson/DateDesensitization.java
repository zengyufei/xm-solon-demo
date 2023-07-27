package com.xunmo.annotations.jackson;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.xunmo.jackson.desensitize.StringDesensitization;

/**
 * Created by EalenXie on 2021/9/24 15:56 手机号脱敏器 默认只保留前3位和后4位
 */
public class DateDesensitization implements StringDesensitization {

	@Override
	public String desensitize(String target) {
		try {
			final DateTime parse = DateUtil.parse(target);
			return parse.toDateStr();
		}
		catch (Exception e) {
			return target;
		}
	}

}
