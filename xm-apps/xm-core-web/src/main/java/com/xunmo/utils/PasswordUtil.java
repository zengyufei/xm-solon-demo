package com.xunmo.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.util.List;

public class PasswordUtil {

	/**
	 * 枚举类型，表示密码加密方式
	 */
	public enum EncryptType {

		PLAIN_TEXT, // 明文
		CIPHER_TEXT, // 密文
		MD5, // MD5
		FULL // 不需要处理，直接返回原字符串

	}

	/**
	 * 获取密码字符串
	 * @param encryptType 密码加密方式枚举
	 * @param password 原始密码
	 * @param params 可变长度参数 - 当 encryptType 为 PLAIN_TEXT 时，params 数组第一个元素可选，表示 salt 的字符串值，
	 * - 当 encryptType 为 CIPHER_TEXT 或 MD5 时，params 数组第一个元素可选，表示 salt 的字符串值 - params
	 * 数组第二个元素可选布尔类型，如果为 false 则不拼接 |，直接返回 md5
	 * @return 密码字符串
	 */
	public static String getPasswordString(EncryptType encryptType, String userName, String password,
			Object... params) {
		String salt = null;
		Boolean isReturnFull = null;

		if (params != null && params.length > 0) {
			for (Object param : params) {
				if (param == null) {
					continue;
				}
				if (salt == null && (param instanceof String)) {
					salt = (String) param;
				}
				if (isReturnFull == null && (param instanceof Boolean)) {
					isReturnFull = (Boolean) param;
				}
				if (salt != null && isReturnFull != null) {
					break;
				}
			}
		}
		String md5;
		switch (encryptType) {
			case PLAIN_TEXT:
				String encryptedPassword = DigestUtil.sha256Hex(password);

				if (salt == null) {
					salt = RandomUtil.randomNumbers(6);
				}

				md5 = getPasswordMd5(userName, salt, encryptedPassword);
				if (isReturnFull != null && !isReturnFull) {
					return md5;
				}
				return salt + "|" + md5;

			case CIPHER_TEXT:

				if (salt == null) {
					salt = RandomUtil.randomNumbers(6);
				}

				md5 = getPasswordMd5(userName, salt, password);
				if (isReturnFull != null && !isReturnFull) {
					return md5;
				}
				return salt + "|" + md5;

			case MD5:
				if (salt == null) {
					salt = RandomUtil.randomNumbers(6);
				}

				md5 = password;
				if (isReturnFull != null && !isReturnFull) {
					return md5;
				}
				return salt + "|" + md5;

			case FULL:

				if (isReturnFull != null && !isReturnFull) {
					if (StrUtil.contains(password, "|")) {
						final List<String> split = StrUtil.split(password, "|");
						if (CollUtil.isNotEmpty(split) && split.size() > 1) {
							return split.get(1);
						}
					}
					throw new NullPointerException("无法正确返回 md5");
				}
				return password;

			default:
				throw new IllegalArgumentException("Unsupported encrypt type: " + encryptType);
		}
	}

	/**
	 * 返回md5加密后的密码，根据当前配置的salt 格式为： md5(salt + userid + password)
	 */
	private static String getPasswordMd5(String userName, String salt, String password) {
		String sb = userName + salt + password;
		return SecureUtil.md5(sb).toUpperCase();
	}

	public static void main(String[] args) {
		String username = "admin";
		String password = "123456";

		// 明文，自动生成 salt 盐
		String passwordString1 = PasswordUtil.getPasswordString(EncryptType.PLAIN_TEXT, username, password);
		System.out.println(passwordString1); // 输出：随机生成的6位数盐|对密码进行MD5加密后得到的32位密文

		// 明文，自动生成 salt 盐
		String passwordString2 = PasswordUtil.getPasswordString(EncryptType.PLAIN_TEXT, username, password, false);
		System.out.println(passwordString2); // 输出：对密码进行MD5加密后得到的32位密文

		// 明文，指定 salt 盐
		String passwordString3 = PasswordUtil.getPasswordString(EncryptType.PLAIN_TEXT, username, password, "abcdefg",
				true);
		System.out.println(passwordString3); // 输出：abcdefg|对密码进行SHA256加密后得到的64位密文

		// 明文，指定 salt 盐
		String passwordString4 = PasswordUtil.getPasswordString(EncryptType.PLAIN_TEXT, username, password, "abcdefg",
				false);
		System.out.println(passwordString4); // 输出：对密码进行SHA256加密后得到的64位密文

		// 密文，自动生成 salt 盐
		String passwordString5 = PasswordUtil.getPasswordString(EncryptType.CIPHER_TEXT, username,
				DigestUtil.sha256Hex(password));
		System.out.println(passwordString5); // 输出：随机生成的6位数盐|对密码进行MD5加密后得到的32位密文

		// 密文，自动生成 salt 盐
		String passwordString6 = PasswordUtil.getPasswordString(EncryptType.CIPHER_TEXT, username,
				DigestUtil.sha256Hex(password), false);
		System.out.println(passwordString6); // 输出：对密码进行MD5加密后得到的32位密文

		// 密文，指定 salt 盐
		String passwordString7 = PasswordUtil.getPasswordString(EncryptType.CIPHER_TEXT, username,
				DigestUtil.sha256Hex(password), "abcdefg");
		System.out.println(passwordString7); // 输出：abcdefg|对密码进行MD5加密后得到的32位密文

		// 密文，指定 salt 盐
		String passwordString8 = PasswordUtil.getPasswordString(EncryptType.CIPHER_TEXT, username,
				DigestUtil.sha256Hex(password), "abcdefg", false);
		System.out.println(passwordString8); // 输出：对密码进行MD5加密后得到的32位密文

		// MD5，自动生成 salt 盐
		String passwordString9 = PasswordUtil.getPasswordString(EncryptType.MD5, username,
				DigestUtil.sha256Hex(password));
		System.out.println(passwordString9); // 输出：随机生成的6位数盐|对密码进行SHA256加密后得到的64位密文

		// MD5，自动生成 salt 盐
		String passwordString10 = PasswordUtil.getPasswordString(EncryptType.MD5, username,
				DigestUtil.sha256Hex(password), false);
		System.out.println(passwordString10); // 输出：对密码进行SHA256加密后得到的64位密文

		// MD5，指定 salt 盐
		String passwordString11 = PasswordUtil.getPasswordString(EncryptType.MD5, username,
				DigestUtil.sha256Hex(password), "abcdefg");
		System.out.println(passwordString11); // 输出：abcdefg|SHA256加密后得到的64位密文

		// MD5，指定 salt 盐
		String passwordString12 = PasswordUtil.getPasswordString(EncryptType.MD5, username,
				DigestUtil.sha256Hex(password), "abcdefg", false);
		System.out.println(passwordString12); // 输出：SHA256加密后得到的64位密文

		// 不需要处理
		String passwordString13 = PasswordUtil.getPasswordString(EncryptType.FULL, username, password);
		System.out.println(passwordString13); // 输出：123456
	}

}
