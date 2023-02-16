package com.xunmo.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.solon.toolkit.GenericTypeUtil;
import com.xunmo.core.utils.XmMap;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface XmMapper<T> extends Mapper<T> {

    default Class<T> currentModelClass() {
        return (Class<T>) GenericTypeUtil.getSuperClassGenericType(this.getClass().getInterfaces()[0], XmMapper.class, 0);
    }

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteById(Serializable id);

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体对象
     * @since 3.4.4
     */
    int deleteById(T entity);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int delete(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idList 主键ID列表或实体列表(不能为 null 以及 empty)
     */
    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<?> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
//    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectById(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
    List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，查询一条记录
     * <p>查询一条记录，例如 qw.last("limit 1") 限制取一条记录, 注意：多条数据会报异常</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    default T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records");
            }
            return ts.get(0);
        }
        return null;
    }

    /**
     * 根据 Wrapper 条件，判断是否存在记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return
     */
    default boolean exists(Wrapper<T> queryWrapper) {
        Long count = this.selectCount(queryWrapper);
        return null != count && count > 0;
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Long selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    <P extends IPage<T>> P selectPage(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     */
    <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    int insertBatch(@Param("coll") Collection<T> entityList);

    int updateBatch(@Param("coll") List<T> list);

    int updateNotNullById(@Param("et") T entity);

    int updateAllColumnById(@Param("et") T entity);

    default T getById(Serializable id) {
        return this.selectById(id);
    }

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    default T checkAndGet(Serializable id) {
        final T e = this.selectById(id);
        if (Objects.isNull(e)) {
            throw new NullPointerException("执行对象查询为空");
        }
        return e;
    }

    /**
     * 检查存在并返回，不存在则抛异常
     *
     * @param id id
     */
    default T checkAndGet(Serializable id, Supplier<String> errorMsg) {
        final T e = this.selectById(id);
        if (Objects.isNull(e)) {
            throw new NullPointerException("执行对象查询为空");
        }
        return e;
    }

    int deleteByMap(@Param("cm") XmMap<String, Object> columnMap);

    default <R> R selectById(Serializable id, Function<T, R> mapper) {
        final T t = this.selectById(id);
        if (Objects.isNull(t)) {
            return null;
        } else {
            return mapper.apply(t);
        }
    }
    default <R> List<R> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList, Function<T, R> mapper) {
        return this.selectBatchIds(idList).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    List<T> selectByMap(@Param("cm") XmMap<String, Object> columnMap);

    default <R> R getById(Serializable id, Function<T, R> mapper) {
        final T t = this.selectById(id);
        if (Objects.isNull(t)) {
            return null;
        } else {
            return mapper.apply(t);
        }
    }

    default <R> R selectOne(@Param("ew") Wrapper<T> queryWrapper, Function<T, R> mapper) {
        List<T> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records", new Object[0]);
            } else {
                return mapper.apply(ts.get(0));
            }
        } else {
            return null;
        }
    }

    default <R> List<R> selectList(@Param("ew") Wrapper<T> queryWrapper, Function<T, R> mapper) {
        return this.selectList(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

}
