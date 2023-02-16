package com.xunmo.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xunmo.core.utils.XmMap;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface XmService<T> {
    int _BATCH_SIZE = 1000;

    boolean save(T entity);

    boolean saveBatch(Collection<T> entityList);

    boolean saveBatch(Collection<T> entityList, int batchSize);

    boolean saveOrUpdateBatch(Collection<T> entityList);

    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

    boolean removeById(Serializable id);

    boolean removeById(Serializable id, boolean useFill);

    boolean removeById(T entity);

    boolean removeByMap(XmMap<String, Object> columnMap);

    boolean remove(Wrapper<T> queryWrapper);

    boolean removeByIds(Collection<T> list);

    boolean removeByIds(Collection<T> list, boolean useFill);

    boolean removeBatchByIds(Collection<T> list);

    boolean removeBatchByIds(Collection<T> list, boolean useFill);

    boolean removeBatchByIds(Collection<T> list, int batchSize);

    boolean removeBatchByIds(Collection<T> list, int batchSize, boolean useFill);

//     boolean updateById(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().updateNotNullById(entity));
//    }

    boolean updateNotNullById(T entity);

    boolean updateAllColumnById(T entity);

    boolean update(Wrapper<T> updateWrapper);

    boolean update(T entity, Wrapper<T> updateWrapper);

    boolean updateBatchById(Collection<T> entityList);

    boolean updateBatchById(Collection<T> entityList, int batchSize);

    boolean saveOrUpdate(T entity);

    T getById(Serializable id);

    <R> R getById(Serializable id, Function<T, R> mapper);

    List<T> listByIds(Collection<? extends Serializable> idList);

    <R> List<R> listByIds(@Param("coll") Collection<? extends Serializable> idList, Function<T, R> mapper);

    List<T> listByMap(XmMap<String, Object> columnMap);

    T getOne(Wrapper<T> queryWrapper);

    T getOne(Wrapper<T> queryWrapper, boolean throwEx);

    <R> R getOne(Wrapper<T> queryWrapper, Function<T, R> mapper);

    Map<String, Object> getMap(Wrapper<T> queryWrapper);

    <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    long count();

    long count(Wrapper<T> queryWrapper);

    List<T> list(Wrapper<T> queryWrapper);

    <R> List<R> selectList(Wrapper<T> queryWrapper, Function<T, R> mapper);

    List<T> list();

    <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper);

    <E extends IPage<T>> E page(E page);

    List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);

    List<Map<String, Object>> listMaps();

    List<Object> listObjs();

    <V> List<V> listObjs(Function<? super Object, V> mapper);

    List<Object> listObjs(Wrapper<T> queryWrapper);

    <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<T> queryWrapper);

    <E extends IPage<Map<String, Object>>> E pageMaps(E page);

    XmMapper<T> getBaseMapper();

    Class<T> getEntityClass();

//     QueryChainWrapper<T> query() ;
//
//     LambdaQueryChainWrapper<T> lambdaQuery();
//
//     UpdateChainWrapper<T> update() ;
//
//     LambdaUpdateChainWrapper<T> lambdaUpdate() ;

    boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper);

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    T checkAndGet(Serializable id);

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    T checkAndGet(Serializable id, Supplier<String> errorMsg);

}
