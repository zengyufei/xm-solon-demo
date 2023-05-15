package com.xunmo.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MpBuildMapperMethodUtil {

    public static String getAllSqlSelect(TableInfo tableInfo) {
        String selectColumns = "*";
        if (tableInfo.getResultMap() == null || tableInfo.isAutoInitResultMap()) {
            selectColumns = tableInfo.getAllSqlSelect();
        }
        return selectColumns;
    }

    public static <T> void addMapperStatement(Class<T> modelClass, String currentNamespace, Configuration configuration, String id, SqlCommandType sqlCommandType, String sql) {
        String finalSql = StrUtil.format("<script>{}</script>", sql);
        MappedStatement.Builder builder = getMapperBuilder(configuration, currentNamespace, id, sqlCommandType, finalSql);

        // 创建返回映射
        ArrayList<ResultMap> resultMaps = getResultMaps(modelClass, configuration);
        builder.resultMaps(resultMaps);

        MappedStatement mappedStatement = builder.build();
        // 将创建的MappedStatement注册到配置中
        configuration.addMappedStatement(mappedStatement);
    }

    @NotNull
    public static MappedStatement.Builder getMapperBuilder(Configuration configuration, String currentNamespace, String id, SqlCommandType sqlCommandType, String finalSql) {
        final LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        final String databaseId = configuration.getDatabaseId();
        XPathParser parser = new XPathParser(finalSql, false, configuration.getVariables(), new XMLMapperEntityResolver());
        final SqlSource sqlSource = languageDriver.createSqlSource(configuration, parser.evalNode("/script"), Object.class);
        return new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .databaseId(databaseId)
                .lang(languageDriver)
                .resultSetType(null);
    }

    private static <T> T valueOrDefault(T value, T defaultValue) {
        return value == null
               ? defaultValue
               : value;
    }

    @NotNull
    public static <T> ArrayList<ResultMap> getResultMaps(Class<T> modelClass, Configuration configuration) {
        ArrayList<ResultMap> resultMaps = new ArrayList<>();
        ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, IdUtil.fastUUID(), modelClass, new ArrayList<>());
        ResultMap resultMap = resultMapBuilder.build();
        resultMaps.add(resultMap);
        return resultMaps;
    }


  private List<ResultMap> getStatementResultMaps(
          Configuration configuration,
      String currentNamespace,
      String resultMap,
      Class<?> resultType,
      String statementId) {
    resultMap = applyCurrentNamespace(currentNamespace, resultMap, true);

    List<ResultMap> resultMaps = new ArrayList<>();
    if (resultMap != null) {
      String[] resultMapNames = resultMap.split(",");
      for (String resultMapName : resultMapNames) {
        try {
          resultMaps.add(configuration.getResultMap(resultMapName.trim()));
        } catch (IllegalArgumentException e) {
          throw new IncompleteElementException("Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'", e);
        }
      }
    } else if (resultType != null) {
      ResultMap inlineResultMap = new ResultMap.Builder(
          configuration,
          statementId + "-Inline",
          resultType,
          new ArrayList<>(),
          null).build();
      resultMaps.add(inlineResultMap);
    }
    return resultMaps;
  }

  public String applyCurrentNamespace(String currentNamespace, String base, boolean isReference) {
    if (base == null) {
      return null;
    }
    if (isReference) {
      // is it qualified with any namespace yet?
      if (base.contains(".")) {
        return base;
      }
    } else {
      // is it qualified with this namespace yet?
      if (base.startsWith(currentNamespace + ".")) {
        return base;
      }
      if (base.contains(".")) {
        throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
      }
    }
    return currentNamespace + "." + base;
  }
}
