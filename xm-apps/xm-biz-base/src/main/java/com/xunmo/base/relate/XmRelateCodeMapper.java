package com.xunmo.base.relate;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.solon.toolkit.SqlHelper;
import com.xunmo.base.move.XmSimpleMoveMapper;
import com.xunmo.utils.MpBuildMapperMethodUtil;
import com.xunmo.utils.MpSFunctionUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Mapper
public interface XmRelateCodeMapper<T extends XmRelateCodeEntity> extends XmSimpleMoveMapper<T> {

    default T checkAndGetByCode(String code) {
        return checkAndGetByCode(code, () -> "数据不存在!");
    }

    default T checkAndGetByCode(String code, Supplier<String> errorMsg) {
        final Class<T> clazz = currentModelClass();
        final SFunction<T, ?> codeFunction = MpSFunctionUtil.getSFunction(clazz, T::getCode);
        final T t = this.selectOne(Wrappers.<T>lambdaQuery().eq(codeFunction, code));
        if (Objects.isNull(t)) {
            throw new NullPointerException(errorMsg.get());
        }
        return t;
    }

    /**
     * 根据下级code获取上级部门对象
     *
     * @param childDeptCode 下级部门编码
     */
    default T getParentByChildCode(String childDeptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getParentByChildCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code = (select parent_code from {tableName} where code = #{code})", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("code", childDeptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectOne(id, paramMap);
        }
    }


    /**
     * 根据下级id获取上级部门对象
     *
     * @param deptId 下级部门id
     */
    default T getParentByChildId(String deptId){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getParentByChildId");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);

                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code = ( select parent_code from {tableName} where id = #{deptId} )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("deptId", deptId);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectOne(id, paramMap);
        }
    }

    /**
     * 指定区间获取部门（不包括beginCode和endCode）
     *
     * @param beginDeptCode 开始部门编码
     * @param endDeptCode   结束部门编码
     */
    default List<T> getAllNotSelfByBeginEndCode(String beginDeptCode, String endDeptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllNotSelfByBeginEndCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format(" SELECT {select}\n" +
                        "        from {tableName}\n" +
                        "        where code in (\n" +
                        "            SELECT child_code\n" +
                        "            FROM {tableNameRelate} r\n" +
                        "            WHERE disabled = 0\n" +
                        "            AND parent_code = #{beginDeptCode}\n" +
                        "            AND child_code != #{beginDeptCode}\n" +
                        "            AND child_code not in (\n" +
                        "                select child_code\n" +
                        "                from {tableNameRelate} r\n" +
                        "                where child_code = r.child_code\n" +
                        "                AND parent_code = #{endDeptCode}\n" +
                        "                AND disabled = 0\n" +
                        "            )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("beginDeptCode", beginDeptCode);
            paramMap.put("endDeptCode", endDeptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 指定区间获取部门（包括beginCode和endCode）
     *
     * @param beginDeptCode 开始部门编码
     * @param endDeptCode   结束部门编码
     */
    default List<T> getAllAndSelfByBeginEndCode(String beginDeptCode, String endDeptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllAndSelfByBeginEndCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format(" SELECT {select}\n" +
                        "        from {tableName}\n" +
                        "        where code in (\n" +
                        "            SELECT child_code\n" +
                        "            FROM {tableNameRelate} r\n" +
                        "            WHERE disabled = 0\n" +
                        "                AND parent_code = #{beginDeptCode}\n" +
                        "                AND child_code not in (\n" +
                        "                    select child_code\n" +
                        "                    from {tableNameRelate} r\n" +
                        "                    where child_code = r.child_code\n" +
                        "                        AND parent_code = #{endDeptCode}\n" +
                        "                        AND child_code != #{endDeptCode}\n" +
                        "                        AND disabled = 0\n" +
                        "            )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("beginDeptCode", beginDeptCode);
            paramMap.put("endDeptCode", endDeptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    // -------------------------------------------------------------------------------

    /**
     * 获取直接下级部门
     *
     * @param deptId 部门id
     */
    default List<T> getDirectChildById(String deptId) {
        final Class<T> clazz = currentModelClass();
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        final SFunction<T, ?> parentCodeFunction = MpSFunctionUtil.getSFunction(clazz, T::getParentCode);
        return this.selectList(Wrappers.<T>lambdaQuery()
                .inSql(parentCodeFunction, StrUtil.format("select code from {} where id = '{}'",tableInfo.getTableName(),  deptId)));
    }


    /**
     * 获取直接下级部门
     *
     * @param deptCode 部门编码
     */
    default List<T> getDirectChildByCode(String deptCode){
        final Class<T> clazz = currentModelClass();
        final SFunction<T, ?> parentCodeFunction = MpSFunctionUtil.getSFunction(clazz, T::getParentCode);
        return this.selectList(Wrappers.<T>lambdaQuery()
                .eq(parentCodeFunction, deptCode));
    }

    // -------------------------------------------------------------------------------

    /**
     * 获取所有上级部门（不包括自己）
     *
     * @param deptId 部门id
     */
    default List<T> getAllParentNotSelfById(String deptId) {
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllParentNotSelfById");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT parent_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and child_code = ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        and parent_code != ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("deptId", deptId);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有上级部门（包括自己）
     *
     * @param deptId 部门id
     */
    default List<T> getAllParentAndSelfById(String deptId){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllParentAndSelfById");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format(" select {select} from {tableName} where code in (\n" +
                        "        SELECT parent_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and child_code = ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("deptId", deptId);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（不包括自己）
     *
     * @param deptId 部门id
     */
    default List<T> getAllChildNotSelfById(String deptId) {
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildNotSelfById");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code = ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        and child_code != ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("deptId", deptId);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（包括自己）
     *
     * @param deptId 部门id
     */
    default List<T> getAllChildAndSelfById(String deptId) {
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildAndSelfById");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format(" select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code = ( select CODE from {tableName} where id = #{deptId} )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("deptId", deptId);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }


    // -------------------------------------------------------------------------------


    /**
     * 获取所有上级部门（不包括自己）
     *
     * @param deptCode 部门编码
     */
    default List<T> getAllParentNotSelfByCode(String deptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllParentNotSelfByCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT parent_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and child_code = #{code}\n" +
                        "        and parent_code != #{code}\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("code", deptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有上级部门（包括自己）
     *
     * @param deptCode 部门编码
     */
    default List<T> getAllParentAndSelfByCode(String deptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllParentAndSelfByCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT parent_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and child_code = #{code}\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("code", deptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（不包括自己）
     *
     * @param deptCode 部门编码
     */
    default List<T> getAllChildNotSelfByCode(String deptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildNotSelfByCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "            SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "            and disabled = 0\n" +
                        "            and parent_code = #{code}\n" +
                        "            and child_code != #{code}\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("code", deptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（包括自己）
     *
     * @param deptCode 部门编码
     */
    default List<T> getAllChildAndSelfByCode(String deptCode){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildAndSelfByCode");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code = #{code}\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("code", deptCode);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }


    // -------------------------------------------------------------------------------


    /**
     * 获取所有下级部门（不包括自己）
     *
     * @param deptIds 部门id集合
     */
    default List<T> getAllChildNotSelfByIds(List<String> deptIds){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildNotSelfByIds");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code in ( select CODE from {tableName} where id in\n" +
                        "                <foreach collection=\"deptIds\" item=\"deptId\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                    #{deptId}\n" +
                        "                </foreach>\n" +
                        "            )\n" +
                        "        and child_code not in ( select CODE from {tableName} where id in\n" +
                        "                <foreach collection=\"deptIds\" item=\"deptId\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                    #{deptId}\n" +
                        "                </foreach>\n" +
                        "            )\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, List<String>> paramMap = new HashMap<>();
            paramMap.put("deptIds", deptIds);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（包括自己）
     *
     * @param deptIds 部门id集合
     */
    default List<T> getAllChildAndSelfByIds(List<String> deptIds){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildAndSelfByIds");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code in\n" +
                        "            ( select CODE from {tableName} where id in\n" +
                        "            <foreach collection=\"deptIds\" item=\"deptId\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                #{deptId}\n" +
                        "            </foreach>)\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, List<String>> paramMap = new HashMap<>();
            paramMap.put("deptIds", deptIds);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    /**
     * 获取所有下级部门（不包括自己）
     *
     * @param deptCodes 部门编码集合
     */
    default List<T> getAllChildNotSelfByCodes(List<String> deptCodes){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildNotSelfByCodes");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code in\n" +
                        "        <foreach collection=\"deptCodes\" item=\"deptCode\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "            #{deptCode}\n" +
                        "        </foreach>\n" +
                        "        and child_code not in\n" +
                        "        <foreach collection=\"deptCodes\" item=\"deptCode\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "            #{deptCode}\n" +
                        "        </foreach>\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, List<String>> paramMap = new HashMap<>();
            paramMap.put("deptCodes", deptCodes);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }

    @NotNull
    default String getMapperId(String getAllChildNotSelfByCodes) {
        return this.getClass().getName() + "." + getAllChildNotSelfByCodes;
    }

    /**
     * 获取所有下级部门（包括自己）
     *
     * @param deptCodes 部门编码集合
     */
    default List<T> getAllChildAndSelfByCodes(List<String> deptCodes){
        final Class<T> modelClass = currentModelClass();
        // 使用MybatisPlus自己的SqlHelper获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SqlHelper.sqlSessionFactory(modelClass);
        // 通过SqlSessionFactory创建一个新的SqlSession，并获取全局配置
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            // 制作命名空间，用于将这个SQL创建的MappedStatement注册到MybatisPlus中
            final String id = getMapperId("getAllChildAndSelfByCodes");
            if (!configuration.hasStatement(id, false)) {
                final TableInfo tableInfo = TableInfoHelper.getTableInfo(modelClass);
                final String tableName = tableInfo.getTableName();

                Map<String, String> columnNameMap = new HashMap<>();
                columnNameMap.put("tableName", tableName);
                columnNameMap.put("tableNameRelate", tableName + "_relate");

                String selectColumns = MpBuildMapperMethodUtil.getAllSqlSelect(tableInfo);
                columnNameMap.put("select", selectColumns);
                String sql = StrUtil.format("select {select} from {tableName} where code in (\n" +
                        "        SELECT child_code FROM {tableNameRelate} WHERE 1=1\n" +
                        "        and disabled = 0\n" +
                        "        and parent_code in\n" +
                        "        <foreach collection=\"deptCodes\" item=\"deptCode\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "            #{deptCode}\n" +
                        "        </foreach>\n" +
                        "        )", columnNameMap);
                MpBuildMapperMethodUtil.addMapperStatement(modelClass, configuration, id, sql);
            }

            Map<String, List<String>> paramMap = new HashMap<>();
            paramMap.put("deptCodes", deptCodes);

            // 使用SqlSession查询原生SQL
            return sqlSession.selectList(id, paramMap);
        }
    }


    // -------------------------------------------------------------------------------

    /**
     * 删除部门 > 删除所有关联此部门子节点的闭包关系
     *
     * @param codes 部门ID
     */
    default void removeByCodes(List<String> codes) {
        final Class<T> clazz = currentModelClass();
        final SFunction<T, ?> disabledFunction = MpSFunctionUtil.getSFunction(clazz, T::getDisabled);
        final SFunction<T, ?> codeFunction = MpSFunctionUtil.getSFunction(clazz, T::getCode);
        this.delete(Wrappers.<T>lambdaQuery()
                .eq(disabledFunction, 0)
                .in(codeFunction, codes));
    }
}
