//package com.xunmo.jimmer.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.babyfish.jimmer.jackson.ImmutableModule;
//import org.noear.solon.annotation.Bean;
//import org.noear.solon.annotation.Configuration;
//import org.noear.solon.annotation.Inject;
//import org.noear.solon.serialization.jackson.JacksonActionExecutor;
//import org.noear.solon.serialization.jackson.JacksonRenderFactory;
//
//@Configuration
//public class JimmerJacksonConfig {
//	@Bean
//	public void initJimmerJackson(@Inject JacksonRenderFactory factory, @Inject JacksonActionExecutor executor) {
//		final ImmutableModule immutableModule = new ImmutableModule();
//		{
//			final ObjectMapper objectMapper = factory.config();
//			objectMapper.registerModule(immutableModule);
//		}
//		{
//			final ObjectMapper objectMapper = executor.config();
//			objectMapper.registerModule(immutableModule);
//		}
//	}
//
//}
