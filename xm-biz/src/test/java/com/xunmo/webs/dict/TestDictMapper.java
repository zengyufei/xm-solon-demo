package com.xunmo.webs.dict;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.BizApp;
import com.xunmo.core.utils.XmMap;
import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.mapper.DictMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.List;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestDictMapper extends HttpTestBase {

    @Inject
    private DictMapper dictMapper;

    @Test
    public void testMoveMoveMove() throws Exception {
        testDel();
    }

    @Test
    public void testDel() throws Exception {
        final XmMap<String, Object> xmMap = new XmMap<>();
        xmMap.put(Dict::getId, 123);
        dictMapper.deleteByMap(xmMap);
    }

    @Test
    public void testList() throws Exception {
        final List<Dict> dicts = dictMapper.selectList(Wrappers.<Dict>lambdaQuery()
                .eq(Dict::getParentId, "")
                .or()
                .isNull(Dict::getParentId)
                .orderByAsc(Dict::getId)
        );
    }



}
