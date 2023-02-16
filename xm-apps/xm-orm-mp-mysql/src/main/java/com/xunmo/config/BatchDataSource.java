//package com.xunmo.config;
//
//import cn.hutool.core.util.ReflectUtil;
//import com.zaxxer.hikari.HikariDataSource;
//import org.noear.solon.annotation.Bean;
//import org.noear.solon.annotation.Configuration;
//import org.noear.solon.annotation.Inject;
//import org.noear.solon.core.AopContext;
//import org.noear.solon.core.BeanWrap;
//import org.noear.solon.data.datasource.DsUtils;
//
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * 批量建数据源 bean 的示例
// *
// * @author zengyufei
// * @date 2022/11/25
// */
//@Configuration
//public class BatchDataSource {
//
//    @Inject
//    AopContext aopContext;
//
//    @Bean
//    public void dsInit(@Inject("${test}") Properties props) {
//        Map<String, DataSource> dsMap = DsUtils.buildDsMap(props, HikariDataSource.class);
//        for (Map.Entry<String, DataSource> kv : dsMap.entrySet()) {
//            final String key = kv.getKey();
//            final DataSource value = kv.getValue();
//            BeanWrap dsWrap = aopContext.wrap(key, value);
//            aopContext.putWrap(key, dsWrap);
//            if ("db1".equals(key)) {
//                ReflectUtil.setFieldValue(dsWrap, "typed", true);
//            }
//            aopContext.putWrap(DataSource.class, dsWrap);
//        }
//    }
//}
