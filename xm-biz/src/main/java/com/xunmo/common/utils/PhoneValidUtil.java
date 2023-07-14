package com.xunmo.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证手机号码是否有效
 *
 * @author zengyufei
 * @date 2022/11/05
 */
public class PhoneValidUtil {

	public static boolean isValid(String phone) {
		String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[5|8|9]))\\d{8}$";
		if (phone.length() != 11) {
			return false;
		}
		else {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phone);
			return m.matches();
		}
	}

}
