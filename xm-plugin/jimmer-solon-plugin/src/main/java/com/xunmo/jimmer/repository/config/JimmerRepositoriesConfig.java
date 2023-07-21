package com.xunmo.jimmer.repository.config;


import com.xunmo.jimmer.repository.support.JimmerRepositoryFactoryBean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Import;

@Configuration
@ConditionalOnProperty(
		prefix = "spring.data.jimmer.repositories",
		name = "enabled",
		havingValue = "true",
		matchIfMissing = true
)
@Condition(onClass = JimmerRepositoryFactoryBean.class)
public class JimmerRepositoriesConfig {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(JimmerRepositoryConfigExtension.class)
    @Import(JimmerRepositoriesRegistrar.class)
    static class JimmerRepositoriesConfiguration {
    }
}
