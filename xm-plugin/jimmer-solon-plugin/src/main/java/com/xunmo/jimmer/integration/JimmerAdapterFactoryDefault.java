package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.JimmerAdapterFactory;
import org.noear.solon.core.BeanWrap;

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
}
