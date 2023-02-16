//package com.xunmo.common.base;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.Assert;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.solon.toolkit.SqlHelper;
//import com.xunmo.utils.XmMap;
//import org.apache.ibatis.annotations.Param;
//import org.noear.solon.data.annotation.Tran;
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.Function;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
//public interface XmServiceBack<T> {
//    int DEFAULT_BATCH_SIZE = 1000;
//
//    default boolean save(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().insert(entity));
//    }
//
//    @Tran
//    default boolean saveBatch(Collection<T> entityList) {
//        return this.saveBatch(entityList, 1000);
//    }
//
//    boolean saveBatch(Collection<T> entityList, int batchSize);
//
//    @Tran
//    default boolean saveOrUpdateBatch(Collection<T> entityList) {
//        return this.saveOrUpdateBatch(entityList, 1000);
//    }
//
//    boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);
//
//    default boolean removeById(Serializable id) {
//        return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
//    }
//
//    default boolean removeById(Serializable id, boolean useFill) {
//        throw new UnsupportedOperationException("不支持的方法!");
//    }
//
//    default boolean removeById(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().deleteById(entity));
//    }
//
//    default boolean removeByMap(XmMap<String, Object> columnMap) {
//        Assert.notEmpty(columnMap, "error: columnMap must not be empty", new Object[0]);
//        return SqlHelper.retBool(this.getBaseMapper().deleteByMap(columnMap));
//    }
//
//    default boolean remove(Wrapper<T> queryWrapper) {
//        return SqlHelper.retBool(this.getBaseMapper().delete(queryWrapper));
//    }
//
//    default boolean removeByIds(Collection<T> list) {
//        return CollectionUtils.isEmpty(list) ? false : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
//    }
//
//    @Tran
//    default boolean removeByIds(Collection<T> list, boolean useFill) {
//        if (CollectionUtils.isEmpty(list)) {
//            return false;
//        } else {
//            return useFill ? this.removeBatchByIds(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
//        }
//    }
//
//    @Tran
//    default boolean removeBatchByIds(Collection<T> list) {
//        return this.removeBatchByIds(list, 1000);
//    }
//
//    @Tran
//    default boolean removeBatchByIds(Collection<T> list, boolean useFill) {
//        return this.removeBatchByIds(list, 1000, useFill);
//    }
//
//    default boolean removeBatchByIds(Collection<T> list, int batchSize) {
//        throw new UnsupportedOperationException("不支持的方法!");
//    }
//
//    default boolean removeBatchByIds(Collection<T> list, int batchSize, boolean useFill) {
//        throw new UnsupportedOperationException("不支持的方法!");
//    }
//
////    default boolean updateById(T entity) {
////        return SqlHelper.retBool(this.getBaseMapper().updateNotNullById(entity));
////    }
//
//    default boolean updateNotNullById(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().updateNotNullById(entity));
//    }
//
//    default boolean updateAllColumnById(T entity) {
//        return SqlHelper.retBool(this.getBaseMapper().updateAllColumnById(entity));
//    }
//
//    default boolean update(Wrapper<T> updateWrapper) {
//        return this.update(null, updateWrapper);
//    }
//
//    default boolean update(T entity, Wrapper<T> updateWrapper) {
//        return SqlHelper.retBool(this.getBaseMapper().update(entity, updateWrapper));
//    }
//
//    @Tran
//    default boolean updateBatchById(Collection<T> entityList) {
//        return this.updateBatchById(entityList, 1000);
//    }
//
//    boolean updateBatchById(Collection<T> entityList, int batchSize);
//
//    boolean saveOrUpdate(T entity);
//
//    default T getById(Serializable id) {
//        return this.getBaseMapper().selectById(id);
//    }
//
//    default <R> R getById(Serializable id, Function<T, R> mapper) {
//        final T t = getBaseMapper().selectById(id);
//        if (Objects.isNull(t)) {
//            return null;
//        } else {
//            return mapper.apply(t);
//        }
//    }
//
//    default List<T> listByIds(Collection<? extends Serializable> idList) {
//        return this.getBaseMapper().selectBatchIds(idList);
//    }
//
//    default <R> List<R> listByIds(@Param("coll") Collection<? extends Serializable> idList, Function<T, R> mapper) {
//        return getBaseMapper().selectBatchIds(idList).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
//    }
//
//    default List<T> listByMap(XmMap<String, Object> columnMap) {
//        return this.getBaseMapper().selectByMap(columnMap);
//    }
//
//    default T getOne(Wrapper<T> queryWrapper) {
//        return this.getOne(queryWrapper, true);
//    }
//
//    T getOne(Wrapper<T> queryWrapper, boolean throwEx);
//
//    default <R> R getOne(Wrapper<T> queryWrapper, Function<T, R> mapper) {
//        final T one = this.getOne(queryWrapper, true);
//        if (ObjectUtil.isNotNull(one)) {
//            return mapper.apply(one);
//        } else {
//            return null;
//        }
//    }
//
//    Map<String, Object> getMap(Wrapper<T> queryWrapper);
//
//    <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
//
//    default long count() {
//        return this.count(Wrappers.emptyWrapper());
//    }
//
//    default long count(Wrapper<T> queryWrapper) {
//        return SqlHelper.retCount(this.getBaseMapper().selectCount(queryWrapper));
//    }
//
//    default List<T> list(Wrapper<T> queryWrapper) {
//        return this.getBaseMapper().selectList(queryWrapper);
//    }
//
//    default <R> List<R> selectList(Wrapper<T> queryWrapper, Function<T, R> mapper) {
//        return getBaseMapper().selectList(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
//    }
//
//    default List<T> list() {
//        return this.list(Wrappers.emptyWrapper());
//    }
//
//    default <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
//        return this.getBaseMapper().selectPage(page, queryWrapper);
//    }
//
//    default <E extends IPage<T>> E page(E page) {
//        return this.page(page, Wrappers.emptyWrapper());
//    }
//
//    default List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
//        return this.getBaseMapper().selectMaps(queryWrapper);
//    }
//
//    default List<Map<String, Object>> listMaps() {
//        return this.listMaps(Wrappers.emptyWrapper());
//    }
//
//    default List<Object> listObjs() {
//        return this.listObjs(Function.identity());
//    }
//
//    default <V> List<V> listObjs(Function<? super Object, V> mapper) {
//        return this.listObjs(Wrappers.emptyWrapper(), mapper);
//    }
//
//    default List<Object> listObjs(Wrapper<T> queryWrapper) {
//        return this.listObjs(queryWrapper, Function.identity());
//    }
//
//    default <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
//        return (List)this.getBaseMapper().selectObjs(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
//    }
//
//    default <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<T> queryWrapper) {
//        return this.getBaseMapper().selectMapsPage(page, queryWrapper);
//    }
//
//    default <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
//        return this.pageMaps(page, Wrappers.emptyWrapper());
//    }
//
//    XmMapper<T> getBaseMapper();
//
//    Class<T> getEntityClass();
//
////    default QueryChainWrapper<T> query() {
////        return ChainWrappers.queryChain(this.getBaseMapper());
////    }
////
////    default LambdaQueryChainWrapper<T> lambdaQuery() {
////        return ChainWrappers.lambdaQueryChain(this.getBaseMapper());
////    }
////
////    default UpdateChainWrapper<T> update() {
////        return ChainWrappers.updateChain(this.getBaseMapper());
////    }
////
////    default LambdaUpdateChainWrapper<T> lambdaUpdate() {
////        return ChainWrappers.lambdaUpdateChain(this.getBaseMapper());
////    }
//
//    default boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper) {
//        return this.update(entity, updateWrapper) || this.saveOrUpdate(entity);
//    }
//
//    /**
//     * 检查存在并返回，不存在则抛异常
//     *
//     * @param id id
//     */
//    default T checkAndGet(Serializable id) {
//        return getBaseMapper().checkAndGet(id);
//    }
//
//    /**
//     * 检查存在并返回，不存在则抛异常
//     *
//     * @param id id
//     */
//    default T checkAndGet(Serializable id, Supplier<String> errorMsg) {
//        return getBaseMapper().checkAndGet(id, errorMsg);
//    }
//
//}
