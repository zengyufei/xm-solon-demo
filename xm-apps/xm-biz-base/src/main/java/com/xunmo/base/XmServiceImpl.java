//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.solon.toolkit.GenericTypeUtil;
import com.baomidou.mybatisplus.solon.toolkit.SqlHelper;
import com.xunmo.annotations.AutoTran;
import com.xunmo.core.utils.XmMap;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.data.annotation.Tran;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AutoTran
public class XmServiceImpl<M extends XmMapper<T>, T> implements XmService<T> {
    protected Log log = LogFactory.getLog(this.getClass());

    @Db
    protected M baseMapper;

    protected Class<T> entityClass = this.currentModelClass();
    protected Class<M> mapperClass = this.currentMapperClass();

    public XmServiceImpl() {
    }

    int public_BATCH_SIZE = 1000;

    @Override
    @Tran
    public boolean save(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().insert(entity));
    }

    @Override
    @Tran
    public boolean saveBatch(Collection<T> entityList) {
        return this.saveBatch(entityList, 1000);
    }

    @Override
    @Tran
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return this.saveOrUpdateBatch(entityList, 1000);
    }

    @Override
    @Tran
    public boolean removeById(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().deleteById(entity));
    }

    @Override
    @Tran
    public boolean removeByMap(XmMap<String, Object> columnMap) {
        Assert.notEmpty(columnMap, "error: columnMap must not be empty", new Object[0]);
        return SqlHelper.retBool(this.getBaseMapper().deleteByMap(columnMap));
    }

    @Override
    @Tran
    public boolean remove(Wrapper<T> queryWrapper) {
        return SqlHelper.retBool(this.getBaseMapper().delete(queryWrapper));
    }

    @Override
    @Tran
    public boolean removeByIds(Collection<T> list, boolean useFill) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else {
            return useFill ? this.removeBatchByIds(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
        }
    }

    @Override
    @Tran
    public boolean removeBatchByIds(Collection<T> list) {
        return this.removeBatchByIds(list, 1000);
    }

    @Override
    @Tran
    public boolean removeBatchByIds(Collection<T> list, boolean useFill) {
        return this.removeBatchByIds(list, 1000, useFill);
    }

//    public boolean updateById(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().updateNotNullById(entity));
//    }

    @Override
    @Tran
    public boolean updateNotNullById(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().updateNotNullById(entity));
    }

    @Override
    @Tran
    public boolean updateAllColumnById(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().updateAllColumnById(entity));
    }

    @Override
    @Tran
    public boolean update(Wrapper<T> updateWrapper) {
        return this.update(null, updateWrapper);
    }

    @Override
    @Tran
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return SqlHelper.retBool(this.getBaseMapper().update(entity, updateWrapper));
    }

    @Override
    @Tran
    public boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, 1000);
    }

    @Override
    public T getById(Serializable id) {
        return this.getBaseMapper().selectById(id);
    }

    @Override
    public <R> R getById(Serializable id, Function<T, R> mapper) {
        final T t = getBaseMapper().selectById(id);
        if (Objects.isNull(t)) {
            return null;
        } else {
            return mapper.apply(t);
        }
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        return this.getBaseMapper().selectBatchIds(idList);
    }

    @Override
    public <R> List<R> listByIds(@Param("coll") Collection<? extends Serializable> idList, Function<T, R> mapper) {
        return getBaseMapper().selectBatchIds(idList).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    @Override
    public List<T> listByMap(XmMap<String, Object> columnMap) {
        return this.getBaseMapper().selectByMap(columnMap);
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper) {
        return this.getOne(queryWrapper, true);
    }


    @Override
    public <R> R getOne(Wrapper<T> queryWrapper, Function<T, R> mapper) {
        final T one = this.getOne(queryWrapper, true);
        if (ObjectUtil.isNotNull(one)) {
            return mapper.apply(one);
        } else {
            return null;
        }
    }

    @Override
    public long count() {
        return this.count(Wrappers.emptyWrapper());
    }

    @Override
    public long count(Wrapper<T> queryWrapper) {
        return SqlHelper.retCount(this.getBaseMapper().selectCount(queryWrapper));
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectList(queryWrapper);
    }

    @Override
    public <R> List<R> selectList(Wrapper<T> queryWrapper, Function<T, R> mapper) {
        return getBaseMapper().selectList(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    @Override
    public List<T> list() {
        return this.list(Wrappers.emptyWrapper());
    }

    @Override
    public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectPage(page, queryWrapper);
    }

    @Override
    public <E extends IPage<T>> E page(E page) {
        return this.page(page, Wrappers.emptyWrapper());
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        return this.listMaps(Wrappers.emptyWrapper());
    }

    @Override
    public List<Object> listObjs() {
        return this.listObjs(Function.identity());
    }

    @Override
    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return this.listObjs(Wrappers.emptyWrapper(), mapper);
    }

    @Override
    public List<Object> listObjs(Wrapper<T> queryWrapper) {
        return this.listObjs(queryWrapper, Function.identity());
    }

    @Override
    public <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return (List)this.getBaseMapper().selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectMapsPage(page, queryWrapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
        return this.pageMaps(page, Wrappers.emptyWrapper());
    }

//    public QueryChainWrapper<T> query() {
//        return ChainWrappers.queryChain(this.getBaseMapper());
//    }
//
//    public LambdaQueryChainWrapper<T> lambdaQuery() {
//        return ChainWrappers.lambdaQueryChain(this.getBaseMapper());
//    }
//
//    public UpdateChainWrapper<T> update() {
//        return ChainWrappers.updateChain(this.getBaseMapper());
//    }
//
//    public LambdaUpdateChainWrapper<T> lambdaUpdate() {
//        return ChainWrappers.lambdaUpdateChain(this.getBaseMapper());
//    }

    @Override
    @Tran
    public boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper) {
        return this.update(entity, updateWrapper) || this.saveOrUpdate(entity);
    }

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    @Override
    public T checkAndGet(Serializable id) {
        return getBaseMapper().checkAndGet(id);
    }

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    @Override
    public T checkAndGet(Serializable id, Supplier<String> errorMsg) {
        return getBaseMapper().checkAndGet(id, errorMsg);
    }

    @Override
    public M getBaseMapper() {
        return this.baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    protected <S> S toBean(Object obj, Class<S> clazz) {
        return BeanUtil.copyProperties(obj, clazz);
    }

    protected Class<M> currentMapperClass() {
        return (Class<M>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImpl.class, 0);
    }

    protected Class<T> currentModelClass() {
        return (Class<T>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImpl.class, 1);
    }

    @Override
    @Tran
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = this.getSqlStatement(SqlMethod.INSERT_ONE);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);
        });
    }

    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(this.mapperClass, sqlMethod);
    }

    @Override
    @Tran
    public boolean saveOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.getById((Serializable)idVal)) ? this.updateNotNullById(entity) : this.save(entity);
        }
    }

    @Override
    @Tran
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(this.getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            ParamMap<T> param = new ParamMap();
            param.put("et", entity);
            sqlSession.update(this.getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Override
    @Tran
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = this.getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            ParamMap<T> param = new ParamMap();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return throwEx ? this.baseMapper.selectOne(queryWrapper) : SqlHelper.getObject(this.log, this.baseMapper.selectList(queryWrapper));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(this.log, this.baseMapper.selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(this.log, this.listObjs(queryWrapper, mapper));
    }

    @Tran
    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
    }

    @Tran
    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return this.executeBatch(list, 1000, consumer);
    }

    @Override
    @Tran
    public boolean removeById(Serializable id) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeById(id, true) : SqlHelper.retBool(this.getBaseMapper().deleteById(id));
    }

    @Override
    @Tran
    public boolean removeByIds(Collection<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
            return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeBatchByIds(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
        }
    }

    @Override
    @Tran
    public boolean removeById(Serializable id, boolean useFill) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        if (useFill && tableInfo.isWithLogicDelete() && !this.entityClass.isAssignableFrom(id.getClass())) {
            T instance = tableInfo.newInstance();
            tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{id});
            return this.removeById(instance);
        } else {
            return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
        }
    }

    @Override
    @Tran
    public boolean removeBatchByIds(Collection<T> list, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.removeBatchByIds(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
    }

    @Override
    @Tran
    public boolean removeBatchByIds(Collection<T> list, int batchSize, boolean useFill) {
        String sqlStatement = this.getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (this.entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{e});
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }

        });
    }
}
