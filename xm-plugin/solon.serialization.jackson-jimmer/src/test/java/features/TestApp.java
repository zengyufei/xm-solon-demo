package features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.serialization.JsonConverter;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class TestApp {

	public static void main(String[] args) {
		Solon.start(TestApp.class, args, app -> {
			app.onEvent(JacksonRenderFactory.class, factory -> initMvcJsonCustom(factory));
		});
	}

	/**
	 * 初始化json定制（需要在插件运行前定制）
	 */
	private static void initMvcJsonCustom(JacksonRenderFactory factory) {
		// 通过转换器，做简单类型的定制
		factory.addConvertor(Date.class, s -> s.getTime());

		factory.addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		factory.addConvertor(LocalDateTime.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		factory.addEncoder(Date.class, new JsonSerializer<Date>() {
			@Override
			public void serialize(Date date, JsonGenerator out, SerializerProvider sp) throws IOException {
				out.writeNumber(date.getTime());
			}
		});

		// factory.config().addHandler();
	}

	@Mapping("/")
	public Object home() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("time1", LocalDateTime.now());
		data.put("time2", LocalDate.now());
		data.put("time3", new Date());

		return data;
	}

	@Mapping("/hello")
	public Object hello(String name) {
		return name;
	}

}
