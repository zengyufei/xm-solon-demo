package com.xunmo.jimmer;

import org.babyfish.jimmer.sql.JSqlClient;
import org.noear.solon.core.BeanWrap;

/**
 * 适配器工厂
 *
 * @author noear
 * @since 1.5
 */
public interface JimmerAdapterFactory {
	JSqlClient create(BeanWrap dsWrap);

}
