package com.xunmo.webs;

import cn.hutool.json.JSONUtil;
import com.xunmo.BizApp;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.List;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestJim {

    @Inject
    JSqlClient sqlClient;

    @Test
    public void testQuery() throws Exception {
        BookTable book = BookTable.$;

        List<Book> books = sqlClient
                .createQuery(book)
                .where(book.name().like("GraphQL"))
                .select(book)
                .execute();
        System.out.println(JSONUtil.toJsonStr(books));
    }
}
