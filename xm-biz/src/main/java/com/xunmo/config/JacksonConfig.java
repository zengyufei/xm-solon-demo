package com.xunmo.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.io.IOException;
import java.util.Date;

@Configuration
public class JacksonConfig {
	@Bean
	public void initJackson(@Inject ObjectMapper objectMapper) {
		SimpleModule simpleModule = new SimpleModule();
		initModule(simpleModule);
		objectMapper.registerModule(simpleModule);
	}

	private void initModule(SimpleModule immutableModule) {

		immutableModule.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			@Override
			public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				if (StrUtil.isBlank(jsonParser.getValueAsString())) {
					return null;
				}
				return StrUtil.trim(jsonParser.getValueAsString());
			}
		});
		immutableModule.addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			private final String[] DATE_FORMAT_STRS = new String[]{"yyyy",

					"yyyy-M", "yyyy/M", "yyyy.M",

					"yyyy-M", "yyyy/M", "yyyy.M",

					"yyyy-MM", "yyyy/MM", "yyyy.MM",

					"yyyy年M月", "yyyy年M月", "yyyy年MM月",

					"yyyyM", "yyyyM", "yyyyMM",

					"yyyy-M-dd", "yyyy/M/dd", "yyyy.M.dd",

					"yyyy-M-d", "yyyy/M/d", "yyyy.M.d",

					"yyyy-MM-d", "yyyy/MM/d", "yyyy.MM.d",

					"yyyy年M月dd日", "yyyy年M月d日", "yyyy年MM月d日",

					"yyyyMdd", "yyyyMd", "yyyyMMd",};

			@Override
			public Date deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				String text = jsonParser.getText();
				if (StrUtil.isBlank(text)) {
					return null;
				}
				return formatToDate(text);
			}

			public Date formatToDate(String parameter) {
				try {
					return DateUtil.parse(parameter);
				} catch (Exception e) {
					for (String dateFormatStr : DATE_FORMAT_STRS) {
						try {
							return DateUtil.parse(parameter, dateFormatStr);
						} catch (Exception ignored2) {
						}
					}
					throw e;
				}
			}
		});
	}

}
