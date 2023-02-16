//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代替 mybatis sql 语句打印;
 *
 * @author zengyufei
 * @date 2023/02/01
 */
public class P6spySlf4JLogger extends FormattedLogger {
    private Logger log = LoggerFactory.getLogger("p6spy");

    public P6spySlf4JLogger() {
    }

    @Override
    public void logException(Exception e) {
        this.log.info("", e);
    }

    @Override
    public void logText(String text) {
        this.log.info(text);
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        String msg = this.strategy.formatMessage(connectionId, now, elapsed, category.toString(), prepared, sql, url);
        if (prepared.equalsIgnoreCase("SELECT 'x'")) {
            this.log.trace(msg);
        } else if (Category.ERROR.equals(category)) {
            this.log.error(msg);
        } else if (Category.WARN.equals(category)) {
            this.log.warn(msg);
        } else if (Category.DEBUG.equals(category)) {
            this.log.debug(msg);
        } else {
            this.log.info(msg);
        }

    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.ERROR.equals(category)) {
            return this.log.isErrorEnabled();
        } else if (Category.WARN.equals(category)) {
            return this.log.isWarnEnabled();
        } else {
            return Category.DEBUG.equals(category) ? this.log.isDebugEnabled() : this.log.isInfoEnabled();
        }
    }
}
