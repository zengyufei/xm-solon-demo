////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package com.xunmo.common.base;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.convert.Convert;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.enums.SqlMethod;
//import com.baomidou.mybatisplus.core.metadata.TableInfo;
//import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
//import com.baomidou.mybatisplus.core.toolkit.Assert;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.baomidou.mybatisplus.solon.toolkit.GenericTypeUtil;
//import com.baomidou.mybatisplus.solon.toolkit.SqlHelper;
//import com.github.pagehelper.ISelect;
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.xunmo.common.PageParam;
//import com.xunmo.utils.LamUtil;
//import org.apache.ibatis.binding.MapperMethod.ParamMap;
//import org.apache.ibatis.logging.Log;
//import org.apache.ibatis.logging.LogFactory;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.solon.annotation.Db;
//import org.noear.solon.annotation.Inject;
//import org.noear.solon.core.handle.Context;
//import org.noear.solon.data.annotation.Tran;
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//
//public class XmServiceImplBack<M extends XmMapper<T>, T> implements XmService<T> {
//    protected Log log = LogFactory.getLog(this.getClass());
//
//    @Inject("${page.pageNumName}")
//    private String pageNumName;
//
//    @Inject("${page.pageSizeName}")
//    private String pageSizeName;
//
//    @Db
//    protected M baseMapper;
//
//    protected Class<T> entityClass = this.currentModelClass();
//    protected Class<M> mapperClass = this.currentMapperClass();
//
//    public XmServiceImplBack() {
//    }
//
//    @Override
//    public M getBaseMapper() {
//        return this.baseMapper;
//    }
//
//    @Override
//    public Class<T> getEntityClass() {
//        return this.entityClass;
//    }
//
//    protected <S> S toBean(Object obj, Class<S> clazz) {
//        return BeanUtil.copyProperties(obj, clazz);
//    }
//
//    protected <E> Page<E> startPage(ISelect select) {
//        final Context context = Context.current();
//        final PageParam pageParam = new PageParam();
//        if (context != null) {
//            /*
//            page :
//                pageNumName: current
//                pageSizeName: size
//             */
//            final String pageNumVal = context.param(pageNumName);
//            final String pageSizeVal = context.param(pageSizeName);
//            if (StrUtil.isAllNotBlank(pageNumVal, pageSizeVal)) {
//                pageParam.setPageNo(Convert.toInt(pageNumVal, 1));
//                pageParam.setPageSize(Convert.toInt(pageSizeVal, 10));
//            }
//        }
//        if (!pageParam.hasPageParam()) {
//            pageParam.setPageNo(1);
//            pageParam.setPageSize(0);
//        }
//
//        final Integer pageSize = pageParam.getPageSize();
//        final String orderBy = pageParam.getOrderBy();
//        final Page<E> page = PageHelper.startPage(pageParam.getPageNo(), pageSize, pageSize != 0)
//                .setOrderBy(LamUtil.getOrderByStr(orderBy, entityClass))
//                .doSelectPage(select);
//        context.attrSet("page", page);
//        return page;
//    }
//
//    protected <E> Page<E> startPage() {
//        final Context context = Context.current();
//        final PageParam pageParam = new PageParam();
//        if (context != null) {
//            /*
//            page :
//                pageNumName: current
//                pageSizeName: size
//             */
//            final String pageNumVal = context.param(pageNumName);
//            final String pageSizeVal = context.param(pageSizeName);
//            if (StrUtil.isAllNotBlank(pageNumVal, pageSizeVal)) {
//                pageParam.setPageNo(Convert.toInt(pageNumVal, 1));
//                pageParam.setPageSize(Convert.toInt(pageSizeVal, 10));
//            }
//        }
//        if (!pageParam.hasPageParam()) {
//            pageParam.setPageNo(1);
//            pageParam.setPageSize(0);
//        }
//
//        final Integer pageSize = pageParam.getPageSize();
//        final Page<E> page = PageHelper.startPage(pageParam.getPageNo(), pageSize, pageSize != 0);
//        context.attrSet("page", page);
//        return page;
//    }
//
//    protected Class<M> currentMapperClass() {
//        return (Class<M>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImplBack.class, 0);
//    }
//
//    protected Class<T> currentModelClass() {
//        return (Class<T>) GenericTypeUtil.getSuperClassGenericType(this.getClass(), XmServiceImplBack.class, 1);
//    }
//
//    @Override
//    @Tran
//    public boolean saveBatch(Collection<T> entityList, int batchSize) {
//        String sqlStatement = this.getSqlStatement(SqlMethod.INSERT_ONE);
//        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
//            sqlSession.insert(sqlStatement, entity);
//        });
//    }
//
//    protected String getSqlStatement(SqlMethod sqlMethod) {
//        return SqlHelper.getSqlStatement(this.mapperClass, sqlMethod);
//    }
//
//    @Override
//    @Tran
//    public boolean saveOrUpdate(T entity) {
//        if (null == entity) {
//            return false;
//        } else {
//            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
//            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
//            String keyProperty = tableInfo.getKeyProperty();
//            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
//            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
//            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.getById((Serializable)idVal)) ? this.updateNotNullById(entity) : this.save(entity);
//        }
//    }
//
//    @Override
//    @Tran
//    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
//        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
//        String keyProperty = tableInfo.getKeyProperty();
//        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
//        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
//            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
//            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(this.getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
//        }, (sqlSession, entity) -> {
//            ParamMap<T> param = new ParamMap();
//            param.put("et", entity);
//            sqlSession.update(this.getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
//        });
//    }
//
//    @Override
//    @Tran
//    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
//        String sqlStatement = this.getSqlStatement(SqlMethod.UPDATE_BY_ID);
//        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
//            ParamMap<T> param = new ParamMap();
//            param.put("et", entity);
//            sqlSession.update(sqlStatement, param);
//        });
//    }
//
//    @Override
//    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
//        return throwEx ? this.baseMapper.selectOne(queryWrapper) : SqlHelper.getObject(this.log, this.baseMapper.selectList(queryWrapper));
//    }
//
//    @Override
//    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
//        return SqlHelper.getObject(this.log, this.baseMapper.selectMaps(queryWrapper));
//    }
//
//    @Override
//    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
//        return SqlHelper.getObject(this.log, this.listObjs(queryWrapper, mapper));
//    }
//
//    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
//        return SqlHelper.executeBatch(this.entityClass, this.log, list, batchSize, consumer);
//    }
//
//    protected <E> boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
//        return this.executeBatch(list, 1000, consumer);
//    }
//
//    @Override
//    public boolean removeById(Serializable id) {
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
//        return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeById(id, true) : SqlHelper.retBool(this.getBaseMapper().deleteById(id));
//    }
//
//    @Override
//    @Tran
//    public boolean removeByIds(Collection<T> list) {
//        if (CollectionUtils.isEmpty(list)) {
//            return false;
//        } else {
//            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
//            return tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill() ? this.removeBatchByIds(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIds(list));
//        }
//    }
//
//    @Override
//    public boolean removeById(Serializable id, boolean useFill) {
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
//        if (useFill && tableInfo.isWithLogicDelete() && !this.entityClass.isAssignableFrom(id.getClass())) {
//            T instance = tableInfo.newInstance();
//            tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{id});
//            return this.removeById(instance);
//        } else {
//            return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
//        }
//    }
//
//    @Override
//    @Tran
//    public boolean removeBatchByIds(Collection<T> list, int batchSize) {
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
//        return this.removeBatchByIds(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
//    }
//
//    @Override
//    @Tran
//    public boolean removeBatchByIds(Collection<T> list, int batchSize, boolean useFill) {
//        String sqlStatement = this.getSqlStatement(SqlMethod.DELETE_BY_ID);
//        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
//        return this.executeBatch(list, batchSize, (sqlSession, e) -> {
//            if (useFill && tableInfo.isWithLogicDelete()) {
//                if (this.entityClass.isAssignableFrom(e.getClass())) {
//                    sqlSession.update(sqlStatement, e);
//                } else {
//                    T instance = tableInfo.newInstance();
//                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{e});
//                    sqlSession.update(sqlStatement, instance);
//                }
//            } else {
//                sqlSession.update(sqlStatement, e);
//            }
//
//        });
//    }
//}
