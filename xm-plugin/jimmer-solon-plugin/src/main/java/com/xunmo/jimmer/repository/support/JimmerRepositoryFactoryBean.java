package com.xunmo.jimmer.repository.support;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class JimmerRepositoryFactoryBean<R extends Repository<E, ID>, E, ID> extends RepositoryFactoryBeanSupport<R, E, ID> {

    private Object sqlClient;

    public JimmerRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
        this.setLazyInit(false);
    }

    public void setSqlClient(Object sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new JimmerRepositoryFactory(sqlClient);
    }
}
