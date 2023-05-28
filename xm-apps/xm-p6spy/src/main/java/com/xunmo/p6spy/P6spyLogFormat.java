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
            return "提交 sql!";
        }
        if (StrUtil.equalsIgnoreCase(category, "rollback")) {
            return "回滚 sql!";
        }
        final String finaSql = StrUtil.isBlank(sql)
                               ? prepared
                               : sql;
        String operType = "【查询】";
        if (StrUtil.startWithIgnoreCase(finaSql, "insert")) {
            operType = "【新增】";
        }
        else if (StrUtil.startWithIgnoreCase(finaSql, "update")) {
            operType = "【修改】";
        }
        else if (StrUtil.startWithIgnoreCase(finaSql, "delete")) {
            operType = "【删除】";
        }
        return new StringBuilder().append(" 执行").append(" ").append(operType).append(" ").append("SQL ").append(elapsed).append("：").append(finaSql.replaceAll("[\\s]+", StringPool.SPACE)).toString();
    }
}
