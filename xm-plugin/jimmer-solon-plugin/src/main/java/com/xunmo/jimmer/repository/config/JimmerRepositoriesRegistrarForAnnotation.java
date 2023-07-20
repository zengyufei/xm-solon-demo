package com.xunmo.jimmer.repository.config;

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class JimmerRepositoriesRegistrarForAnnotation extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJimmerRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JimmerRepositoryConfigExtension();
    }
}
