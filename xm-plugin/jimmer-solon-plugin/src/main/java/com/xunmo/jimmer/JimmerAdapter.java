package com.xunmo.jimmer;

import org.babyfish.jimmer.sql.JSqlClient;

/**
 * 适配器
 * <p>
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear
 * @since 1.5
 */
public interface JimmerAdapter {

	JSqlClient sqlClient();

}
