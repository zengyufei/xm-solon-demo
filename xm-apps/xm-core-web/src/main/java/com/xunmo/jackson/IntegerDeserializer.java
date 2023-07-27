package com.xunmo.jackson;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.noear.solon.annotation.Component;

import java.io.IOException;

@Component
public class IntegerDeserializer extends JsonDeserializer<Integer> {

	@Override
	public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String text = p.getText();
		if (text == null || text.trim().length() == 0) {
			return null;
		}
		if (NumberUtil.isInteger(text.trim())) {
			return NumberUtil.parseInt(text.trim());
		}
		else {
			throw new JsonMappingException("输入的数不合法, 请输入合法的数值");
		}
	}

}
