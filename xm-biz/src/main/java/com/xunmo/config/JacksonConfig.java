package com.xunmo.config;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xunmo.core.utils.XmDateUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.io.IOException;
import java.util.Date;

@Configuration
public class JacksonConfig {

	@Bean
	@Condition(onClass = ObjectMapper.class)
	public void initJackson(@Inject ObjectMapper objectMapper) {
		SimpleModule simpleModule = new SimpleModule();
		initModule(simpleModule);
		objectMapper.registerModule(simpleModule);
	}

	private void initModule(SimpleModule simpleModule) {

		simpleModule.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			@Override
			public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				if (jsonParser == null) {
					return null;
				}
				final String valueAsString = jsonParser.getValueAsString();
				if (StrUtil.isBlank(valueAsString)) {
					return null;
				}
				return StrUtil.trim(EscapeUtil.unescapeHtml4(valueAsString));
			}
		});
		simpleModule.addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
			private static final long serialVersionUID = -2186517763342421483L;

			@Override
			public Date deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
				if (jsonParser == null) {
					return null;
				}
				String text = jsonParser.getText();
				if (StrUtil.isBlank(text)) {
					return null;
				}
				final DateTime dateTime = XmDateUtil.checkDateStr(text);
				if (dateTime == null) {
					throw new JsonMappingException("输入的数不合法, 请输入合法的数值");
				}
				return dateTime;
			}

		});
	}

}
