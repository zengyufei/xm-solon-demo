package com.xunmo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.solon.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.solon.plugins.inner.PaginationInnerInterceptor;
import com.xunmo.config.mybatisplus.XmLogicSqlInjector;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.EventBus;

@Slf4j
public class XmOrmPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        final Props props = context.cfg();
        props.loadAddIfAbsent(XmPackageConstants.XM_ORM_MP_MYSQL + ".yml");
//        final SolonApp app = Solon.app();

        EventBus.subscribe(MybatisConfiguration.class, cfg -> {
            MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
            plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
            cfg.setCacheEnabled(false);
            // 下划线自动转驼峰
            cfg.setMapUnderscoreToCamelCase(true);
//            cfg.setLogImpl(XmMybatisStdOutImpl.class);

            cfg.addInterceptor(plusInterceptor);

            GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(cfg);
            globalConfig.setBanner(false);
            globalConfig.setEnableSqlRunner(true);
            globalConfig.setSqlInjector(new XmLogicSqlInjector());
            final GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
            dbConfig.setLogicDeleteField(XmConstants.field_disabled);
            dbConfig.setLogicDeleteValue(XmConstants.DISABLED.toString());
        });

        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 包加载完毕!", XmPackageConstants.XM_ORM_MP_MYSQL);
        } else {
            System.out.println(XmPackageConstants.XM_ORM_MP_MYSQL + " 包加载完毕!");
        }
    }

    @Override
    public void stop() throws Throwable {
        if (XmPackageConstants.IS_CONSOLE_LOG) {
            log.info("{} 插件关闭!", XmPackageConstants.XM_ORM_MP_MYSQL);
        } else {
            System.out.println(XmPackageConstants.XM_ORM_MP_MYSQL + " 插件关闭!");
        }
    }
}
