package com.xunmo.jackson.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Created by EalenXie on 2021/8/9 9:03 脱敏序列化器
 */
public class ObjectDesensitizeSerializer extends StdSerializer<Object> implements ContextualSerializer {

	private transient Desensitization<Object> desensitization;

	public ObjectDesensitizeSerializer() {
		super(Object.class);
	}

	public Desensitization<Object> getDesensitization() {
		return desensitization;
	}

	public void setDesensitization(Desensitization<Object> desensitization) {
		this.desensitization = desensitization;
	}

	@Override
	public JsonSerializer<Object> createContextual(SerializerProvider prov, BeanProperty property)
			throws JsonMappingException {
		Desensitize annotation = property.getAnnotation(Desensitize.class);
		return createContextual(annotation.desensitization());
	}

	@SuppressWarnings("unchecked")
	public JsonSerializer<Object> createContextual(Class<? extends Desensitization<?>> clazz) {
		ObjectDesensitizeSerializer serializer = new ObjectDesensitizeSerializer();
		if (clazz != StringDesensitization.class) {
			serializer.setDesensitization((Desensitization<Object>) DesensitizationFactory.getDesensitization(clazz));
		}
		return serializer;
	}

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		Desensitization<Object> objectDesensitization = getDesensitization();
		if (objectDesensitization != null) {
			try {
				gen.writeObject(objectDesensitization.desensitize(value));
			}
			catch (Exception e) {
				gen.writeObject(value);
			}
		}
		else if (value instanceof String) {
			gen.writeString((String) value);
		}
		else {
			gen.writeObject(value);
		}
	}

}
