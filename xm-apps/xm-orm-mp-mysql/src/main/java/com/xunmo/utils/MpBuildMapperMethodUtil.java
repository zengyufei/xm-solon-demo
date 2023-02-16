package com.xunmo.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MpBuildMapperMethodUtil {

    public static String getAllSqlSelect(TableInfo tableInfo) {
        String selectColumns = "*";
        if (tableInfo.getResultMap() == null || tableInfo.isAutoInitResultMap()) {
            selectColumns = tableInfo.getAllSqlSelect();
        }
        return selectColumns;
    }

    public static <T> void addMapperStatement(Class<T> modelClass, Configuration configuration, String id, String sql) {
        String finalSql = StrUtil.format("<script>{}</script>", sql);
        MappedStatement.Builder builder = getMapperBuilder(configuration, id, finalSql);

        // 创建返回映射
        ArrayList<ResultMap> resultMaps = getResultMaps(modelClass, configuration);
        builder.resultMaps(resultMaps);

        MappedStatement mappedStatement = builder.build();
        // 将创建的MappedStatement注册到配置中
        configuration.addMappedStatement(mappedStatement);
    }

    @NotNull
    public static MappedStatement.Builder getMapperBuilder(Configuration configuration, String id, String finalSql) {
        XPathParser parser = new XPathParser(finalSql, false, configuration.getVariables(), new XMLMapperEntityResolver());
        XMLScriptBuilder scriptBuilder = new XMLScriptBuilder(configuration, parser.evalNode("/script"), Object.class);
        return new MappedStatement.Builder(configuration, id, scriptBuilder.parseScriptNode(), SqlCommandType.SELECT);
    }

    @NotNull
    public static <T> ArrayList<ResultMap> getResultMaps(Class<T> modelClass, Configuration configuration) {
        ArrayList<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, IdUtil.fastUUID(), modelClass, new ArrayList<>());
        ResultMap resultMap = resultMapBuilder.build();
        resultMaps.add(resultMap);
        return resultMaps;
    }
}
