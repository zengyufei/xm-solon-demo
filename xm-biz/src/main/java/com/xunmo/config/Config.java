//package com.xunmo.config;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import org.noear.solon.annotation.Bean;
//import org.noear.solon.annotation.Configuration;
//import org.noear.solon.annotation.Inject;
//import org.noear.solon.serialization.jackson.JacksonActionExecutor;
//import org.noear.solon.serialization.jackson.JacksonRenderFactory;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
//@Configuration
//public class Config {
//	@Bean
//	public void jsonInit(@Inject JacksonRenderFactory factory, @Inject JacksonActionExecutor executor) {
//		//方式1：通过转换器，做简单类型的定制
//		factory.addConvertor(Date.class, s -> s.getTime());
//
//		factory.addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//
//		factory.addConvertor(Long.class, s -> String.valueOf(s));
//
//		//方式2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
//		factory.addEncoder(Date.class, new JsonSerializer<Date>() {
//			@Override
//			public void serialize(Date date, JsonGenerator out, SerializerProvider sp) throws IOException {
//				out.writeNumber(date.getTime());
//			}
//		});
//
//
//		//factory.config()...
//
//		//executor.config()...
//	}
//}
