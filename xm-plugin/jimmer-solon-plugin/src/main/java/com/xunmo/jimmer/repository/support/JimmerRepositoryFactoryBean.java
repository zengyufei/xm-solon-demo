package com.xunmo.jimmer.repository.support;


import com.xunmo.jimmer.Repository;

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
