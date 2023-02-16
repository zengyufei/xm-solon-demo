package com.xunmo.p6spy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;

/**
 * 接管 logback 日志打印 sql语句;
 *
 * @author zengyufei
 * @date 2023/02/01
 */
public class MapperLogbackFilter extends AbstractMatcherFilter<ILoggingEvent> {

    @Setter
    String packagePath;
    @Setter
    String noStr;

    static boolean isNoPackage = false;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        final String loggerName = event.getLoggerName();
        if (!isNoPackage && StrUtil.isAllNotBlank(packagePath, noStr)) {
            isNoPackage = true;
        }
        if (isNoPackage && StrUtil.isNotBlank(loggerName) && loggerName.startsWith(packagePath) && loggerName.contains(noStr)) {
            return FilterReply.DENY;
        } else {
            return FilterReply.ACCEPT;
        }
    }

}
