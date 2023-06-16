package com.xunmo.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.boot.smarthttp.http.SmartHttpContext;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;
import org.noear.solon.serialization.snack3.SnackActionExecutor;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.List;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestJim {

    @Inject
    JSqlClient sqlClient;

    @Inject
    JacksonRenderFactory renderFactory;

    @Test
    public void testQuery() throws Throwable {
        BookTable book = BookTable.$;

        List<Book> books = sqlClient
                .createQuery(book)
                .where(book.name().like("GraphQL"))
                .select(book)
                .execute();


        ContextEmpty ctx = new ContextEmpty();
        final String s = renderFactory.create().renderAndReturn(books, ctx);
        System.out.println(s);
    }
}
