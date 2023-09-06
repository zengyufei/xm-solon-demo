package com.xunmo.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import com.xunmo.core.utils.XmDateUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

		// ======================= 时间序列化规则 ===============================
		// yyyy-MM-dd HH:mm:ss
		simpleModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
		// yyyy-MM-dd
		simpleModule.addSerializer(LocalDate.class,
				new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
		// HH:mm:ss
		simpleModule.addSerializer(LocalTime.class,
				new LocalTimeSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
		// yyyy
		simpleModule.addSerializer(Year.class, new YearSerializer(DateTimeFormatter.ofPattern("yyyy")));
		// MM
		simpleModule.addSerializer(YearMonth.class, new YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")));

		// Instant 类型序列化
		simpleModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);

		// ======================= 时间反序列化规则 ==============================
		// yyyy-MM-dd HH:mm:ss
		simpleModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
		// yyyy-MM-dd
		simpleModule.addDeserializer(LocalDate.class,
				new LocalDateDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
		// HH:mm:ss
		simpleModule.addDeserializer(LocalTime.class,
				new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
		// yyyy
		simpleModule.addDeserializer(Year.class, new YearDeserializer(DateTimeFormatter.ofPattern("yyyy")));
		// MM
		simpleModule.addDeserializer(YearMonth.class,
				new YearMonthDeserializer(DateTimeFormatter.ofPattern("yyyy-MM")));

		// Instant 反序列化
		simpleModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);

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
