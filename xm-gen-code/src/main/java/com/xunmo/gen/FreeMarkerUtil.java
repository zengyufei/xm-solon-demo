package com.xunmo.gen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.io.file.FileReader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * freemarker操作 的工具类
 * @author kong
 *
 */
public class FreeMarkerUtil {

	private static final Configuration cfg;

	static {
		cfg = new Configuration(Configuration.getVersion());
		cfg.setDefaultEncoding("UTF-8");
		// FreeMarker会默认格式化数字，这样设置不再格式化数字
		cfg.setNumberFormat("#");
		cfg.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
	}

	/**
	 * 读取并返回
	 * @param flt_url flt的路径
	 * @return
	 */
	public static String getCode(String flt_url, Map<String, Object> parameMap) {
		// 1、从文件中读取字符串
		FileReader fileReader = new FileReader(flt_url);
		String str = fileReader.readString();

		// 2、让 freemarker解析遍历
		StringWriter result = new StringWriter();
		try {
			Template t = new Template("template", new StringReader(str), cfg);
			t.process(parameMap, result);
		} catch (Exception e) {
			System.err.println("------------------------------flt文件：" + flt_url);
			System.err.println("------------------------------dataModel：" + parameMap);
			throw new RuntimeException(e);
		}

		// 3、返回结果
		return result.toString();
	}

	/**
	 * 读取并返回
	 * @return
	 */
	public static String getCodeLine(String str, Map<String, Object> parameMap) {
		// 2、让 freemarker解析遍历
		StringWriter result = new StringWriter();
		try {
			Template t = new Template("template", new StringReader(str), cfg);
			t.process(parameMap, result);
		} catch (Exception e) {
			System.err.println("------------------------------dataModel：" + parameMap);
			throw new RuntimeException(e);
		}
		// 3、返回结果
		return result.toString();
	}


}
