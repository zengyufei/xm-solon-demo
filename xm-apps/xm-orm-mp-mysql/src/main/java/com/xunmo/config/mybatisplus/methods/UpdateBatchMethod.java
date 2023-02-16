package com.xunmo.config.mybatisplus.methods;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 更新批处理方法
 * 方式最简单，就是用foreach组装成多条update语句
 * Mybatis映射文件中的sql语句默认是不支持以" ; " 结尾的，也就是不支持多条sql语句的执行。
 * 需要在连接mysql的url上加 &allowMultiQueries=true 这个才可以执行
 *
 * @author zengyufei
 * @date 2022/10/31
 */
@Slf4j
public class UpdateBatchMethod extends AbstractMethod {
    private static final long serialVersionUID = -6241261282453802611L;

    public UpdateBatchMethod() {
        super("updateBatch");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = "<script>\n<foreach collection=\"coll\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
        String additional = tableInfo.isWithVersion() ? tableInfo.getVersionFieldInfo().getVersionOli("item", "item.") : "" + tableInfo.getLogicDeleteSql(true, true);
        String setSql = sqlSet(tableInfo.isLogicDelete(), false, tableInfo, false, "item", "item.");
        String sqlResult = String.format(sql, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(), additional);
        // log.debug("sqlResult----->{}", sqlResult);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "updateBatch", sqlSource);
    }

}
