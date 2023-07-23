package features2.test3;

import features2.model.UserDo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.annotation.TestPropertySource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2023/1/16 created
 */
@TestPropertySource("classpath:features2_test3.yml")
@RunWith(SolonJUnit4ClassRunner.class)
public class TestQuickConfig {

	@Inject
	JacksonRenderFactory renderFactory;

	@Test
	public void hello2() throws Throwable {
		UserDo userDo = new UserDo();

		Map<String, Object> data = new HashMap<>();
		data.put("time", new Date(1673861993477L));
		data.put("long", 12L);
		data.put("int", 12);
		data.put("null", null);

		userDo.setMap1(data);

		ContextEmpty ctx = new ContextEmpty();
		renderFactory.create().render(userDo, ctx);
		String output = ctx.attr("output");

		System.out.println(output);

		// error 会有多余的 null (和所有 null 打印开启一样)
		assert "{\"s0\":\"\",\"s1\":\"noear\",\"b0\":0,\"b1\":true,\"n0\":\"0\",\"n1\":\"1\",\"d0\":0.0,\"d1\":1.0,\"obj0\":null,\"list0\":[],\"map0\":null,\"map1\":{\"null\":null,\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12}}"
			.equals(output);
	}

}
