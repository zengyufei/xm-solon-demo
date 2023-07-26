package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.JimmerAdapterFactory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * Jimmer 适配器工厂默认实现
 *
 * @author noear
 * @since 1.5
 */
public class JimmerAdapterFactoryDefault implements JimmerAdapterFactory {

	@Override
	public JimmerAdapter create(BeanWrap dsWrap) {
		return new JimmerAdapterDefault(dsWrap);
	}

	@Override
	public JimmerAdapter create(BeanWrap dsWrap, Props dsProps) {
		return new JimmerAdapterDefault(dsWrap, dsProps);
	}

}
