package com.xunmo.p6spy;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * P6spy SQL 日志格式化
 *
 * @author Caratacus
 */
public class P6spyLogFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
        if (StrUtil.equalsIgnoreCase(category, "commit")) {
            return "commit sql!";
        }
        if (StrUtil.equalsIgnoreCase(category, "rollback")) {
            return "rollback sql!";
        }
        final String finaSql = StrUtil.isBlank(sql) ? prepared : sql;
        return new StringBuilder().append(" Execute").append(" ").append(category).append(" ").append("SQL：").append(finaSql.replaceAll("[\\s]+", StringPool.SPACE)).toString();
//        return StrUtil.isNotBlank(sql) ? new StringBuilder().append(" Execute").append(" ").append(category).append(" ").append("SQL：").append(sql.replaceAll("[\\s]+", StringPool.SPACE)).toString() : null;
    }
}
