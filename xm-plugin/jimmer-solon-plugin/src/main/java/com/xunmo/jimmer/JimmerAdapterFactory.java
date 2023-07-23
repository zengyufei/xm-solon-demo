package com.xunmo.jimmer;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * 适配器工厂
 *
 * @author noear
 * @since 1.5
 */
public interface JimmerAdapterFactory {

	JimmerAdapter create(BeanWrap dsWrap);

	JimmerAdapter create(BeanWrap dsWrap, Props dsProps);

}
