package com.xunmo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.mapper.DictMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.List;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestCacheRedisson extends HttpTestBase {

    @Db
    DictMapper dictMapper;
    @Inject
    CacheService cacheService;

    @Test
    public void testRedisson() throws Exception {
        final List<Dict> dicts = dictMapper.selectList(Wrappers.<Dict>lambdaQuery()
                .eq(Dict::getDisabled, "0")
                .last(" limit 1"));
        final Dict dict = dicts.get(0);
        cacheService.store("123", dict, 30);

        final Dict getDict = (Dict) cacheService.get("123");
        System.out.println(JSONUtil.toJsonPrettyStr(getDict));
    }

}
