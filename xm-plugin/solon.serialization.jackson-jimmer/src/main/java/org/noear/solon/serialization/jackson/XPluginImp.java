package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

public class XPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		JsonProps jsonProps = JsonProps.create(context);

		// ::renderTypedFactory
		JacksonRenderTypedFactory renderTypedFactory = new JacksonRenderTypedFactory();
		applyProps(renderTypedFactory, jsonProps);
		context.wrapAndPut(JacksonRenderTypedFactory.class, renderTypedFactory);

		final ObjectMapper objectMapper = renderTypedFactory.config();

		// ::renderFactory
		// 绑定属性
		JacksonRenderFactory renderFactory = new JacksonRenderFactory(objectMapper);

		// 事件扩展
		context.wrapAndPut(JacksonRenderFactory.class, renderFactory);
		EventBus.push(renderFactory);

		context.lifecycle(-99, () -> {
			final Render render = renderFactory.create();
			RenderManager.mapping("@json", render);
			RenderManager.mapping("@type_json", render);
		});

		// 支持 json 内容类型执行
		JacksonActionExecutor actionExecutor = new JacksonActionExecutor(objectMapper);
		context.wrapAndPut(JacksonActionExecutor.class, actionExecutor);
		EventBus.push(actionExecutor);
		context.wrapAndPut(ObjectMapper.class, objectMapper);

		Solon.app().chainManager().addExecuteHandler(actionExecutor);
		EventBus.push(objectMapper);
	}

	private void applyProps(JacksonRenderTypedFactory factory, JsonProps jsonProps) {
		boolean writeNulls = false;
		if (jsonProps != null) {
			writeNulls = jsonProps.nullAsWriteable || jsonProps.nullNumberAsZero || jsonProps.nullArrayAsEmpty
					|| jsonProps.nullBoolAsFalse || jsonProps.nullStringAsEmpty;

			if (writeNulls) {
				factory.config().getSerializerProvider().setNullValueSerializer(new NullValueSerializer(jsonProps));
			}

			if (jsonProps.enumAsName) {
				factory.config().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
			}
		}

		if (!writeNulls) {
			factory.config().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		}

		// 启用 transient 关键字
		factory.config().configure(PROPAGATE_TRANSIENT_MARKER, true);
		// 启用排序（即使用 LinkedHashMap）
		factory.config().configure(SORT_PROPERTIES_ALPHABETICALLY, true);
		// 是否识别不带引号的key
		factory.config().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 是否识别单引号的key
		factory.config().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 浮点数默认类型（dubbod 转 BigDecimal）
		factory.config().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

		// 反序列化时候遇到不匹配的属性并不抛出异常
		factory.config().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 序列化时候遇到空对象不抛出异常
		factory.config().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 反序列化的时候如果是无效子类型,不抛出异常
		factory.config().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
	}

}
