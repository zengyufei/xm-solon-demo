//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.config.mybatisplus.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class UpdateNotNullById extends AbstractMethod {
    private static final long serialVersionUID = -1823623890546440710L;

    public UpdateNotNullById() {
        super("updateNotNullById");
    }

    public UpdateNotNullById(String name) {
        super("updateNotNullById");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        String additional = this.optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), this.sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "et", "et."), tableInfo.getKeyColumn(), "et." + tableInfo.getKeyProperty(), additional);
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, this.getMethod(sqlMethod), sqlSource);
    }
}
