package com.xunmo.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.xunmo.entity.PageParam;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.core.handle.Context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmPageUtil {

    public static <E> Page<E> startPage(Class<?> entityClass, ISelect select) {
        final Context context = Context.current();
        final SolonProps cfg = Solon.cfg();
        String pageNumName = cfg.get("page.pageNumName", "pageNum");
        String pageSizeName = cfg.get("page.pageSizeName", "pageSize");
        String orderByName = cfg.get("page.orderByName", "orderBy");

        final PageParam pageParam = new PageParam();
        if (context != null) {
            final String pageNumVal = context.param(pageNumName);
            final String pageSizeVal = context.param(pageSizeName);
            final String orderBy = context.param(orderByName);
            if (StrUtil.isAllNotBlank(pageNumVal, pageSizeVal)) {
                pageParam.setPageNo(Convert.toInt(pageNumVal, 1));
                pageParam.setPageSize(Convert.toInt(pageSizeVal, 10));
                pageParam.setOrderBy(orderBy);
            }
        }
        if (!pageParam.hasPageParam()) {
            pageParam.setPageNo(1);
            pageParam.setPageSize(0);
        }

        final Integer pageSize = pageParam.getPageSize();
        final String orderBy = pageParam.getOrderBy();
        final com.github.pagehelper.Page<E> page = PageHelper.startPage(pageParam.getPageNo(), pageSize, pageSize != 0);
        if (entityClass != null) {
            page.setOrderBy(getOrderByStr(orderBy, entityClass));
        }
        if (select != null) {
            page.doSelectPage(select);
        }
        if (context != null) {
            context.attrSet("page", page);
        }
        return page;
    }

    public static <E> Page<E> startPage(ISelect select) {
        return startPage(null, select);
    }

    public static <E> Page<E> startPage() {
        return startPage(null, null);
    }

    public static <T> boolean addOrderBy(QueryWrapper<T> queryWrapper) {
        boolean isAdd = false;
        final String orderByName = getOrderByName();
        final String orderByValue = getOrderByValue(orderByName);
        if (StrUtil.isNotBlank(orderByValue)) {
            final List<String> split = StrUtil.split(orderByValue, ',');
            for (final String orderColumnAndSort : split) {
                if (StrUtil.isBlank(orderColumnAndSort)) {
                    continue;
                }
                final List<String> keyVal = getOrderBySplit(orderColumnAndSort);
                final String requestProperty = keyVal.get(0);
                String requestOrder = keyVal.get(1);


                // 判断列名称的合法性，防止SQL注入。只能是【字母，数字，下划线】
                checkSql(requestProperty);

                requestOrder = checkOrderType(requestOrder);
                queryWrapper.orderBy(true, StrUtil.equalsIgnoreCase("asc", requestOrder), requestProperty);
                isAdd = true;
            }
        }
        return isAdd;
    }

    public static <T> boolean addOrderBy(LambdaQueryWrapper<T> lambda, Class<T> clazz) {
        if (clazz == null) {
            throw new RuntimeException("class 不能为空");
        }
        boolean isAdd = false;
        final String orderByName = getOrderByName();
        final String orderByValue = getOrderByValue(orderByName);
        if (StrUtil.isNotBlank(orderByValue)) {
            final List<String> split = StrUtil.split(orderByValue, ',');
            for (final String orderColumnAndSort : split) {
                if (StrUtil.isBlank(orderColumnAndSort)) {
                    continue;
                }
                final List<String> keyVal = getOrderBySplit(orderColumnAndSort);
                final String requestProperty = keyVal.get(0);
                String requestOrder = keyVal.get(1);

                checkSql(requestProperty);

                requestOrder = checkOrderType(requestOrder);

                final Method getMethod = BeanUtil.getBeanDesc(clazz).getGetter(requestProperty);
                final SFunction<T, ?> tsFunction = MpSFunctionUtil.create(clazz, getMethod);
                lambda.orderBy(true, StrUtil.equalsIgnoreCase("asc", requestOrder), tsFunction);
                isAdd = true;
            }
        }
        return isAdd;
    }


    public static void setOrderByStr(String orderBy, Class<?> clazz) {
        PageMethod.orderBy(getOrderByStr(orderBy, clazz));
    }

    public static String getOrderByStr(String orderBy, Class<?> clazz) {
        if (StrUtil.isBlank(orderBy)) {
            return null;
        }
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        final List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        final Map<String, String> fieldNameByTableFieldMap = new HashMap<>();
        fieldNameByTableFieldMap.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
        for (TableFieldInfo tableFieldInfo : fieldList) {
            fieldNameByTableFieldMap.put(tableFieldInfo.getProperty(), tableFieldInfo.getColumn());
        }

        StringBuilder stringBuilder = new StringBuilder();
        final List<String> split = StrUtil.split(orderBy, ',');

        boolean isAppend = false;
        for (final String orderColumnAndSort : split) {
            if (StrUtil.isBlank(orderColumnAndSort)) {
                continue;
            }
            final List<String> keyVal = getOrderBySplit(orderColumnAndSort);
            final String requestProperty = keyVal.get(0);
            String requestOrder = keyVal.get(1);

            if (StrUtil.isBlank(requestOrder)) {
                requestOrder = "ASC";
            } else if (!(StrUtil.equalsIgnoreCase(requestOrder, "ASC") || StrUtil.equalsIgnoreCase(requestOrder, "DESC"))) {
                throw new IllegalArgumentException("非法的排序策略：" + requestProperty);
            }

            // 判断列名称的合法性，防止SQL注入。只能是【字母，数字，下划线】
            checkSql(requestProperty);

            if (isAppend) {
                stringBuilder.append(", ");
            }

            if (fieldNameByTableFieldMap.containsKey(requestProperty)) {
                final String column = fieldNameByTableFieldMap.get(requestProperty);
                stringBuilder.append(column).append(" ").append(requestOrder);
                isAppend = true;
            }
        }
        return stringBuilder.toString();
    }


    public static List<String> getOrderBySplit(String orderColumnAndSort) {
        final List<String> keyVal;
        if (StrUtil.contains(orderColumnAndSort, ":")) {
            keyVal = StrUtil.split(orderColumnAndSort, ':');
        } else if (StrUtil.contains(orderColumnAndSort, "-")) {
            keyVal = StrUtil.split(orderColumnAndSort, '-');
        } else if (StrUtil.contains(orderColumnAndSort, "_")) {
            keyVal = StrUtil.split(orderColumnAndSort, '_');
        } else {
            throw new RuntimeException("没有匹配到分隔符 : - _ 三种之一");
        }
        return keyVal;
    }


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================
    private static String checkOrderType(String requestOrder) {
        if (StrUtil.isBlank(requestOrder)) {
            requestOrder = "asc";
        } else {
            if (!(StrUtil.equalsIgnoreCase(requestOrder, "ASC") || StrUtil.equalsIgnoreCase(requestOrder, "DESC"))) {
                throw new IllegalArgumentException("非法的排序策略：" + requestOrder);
            }
        }
        return requestOrder;
    }

    private static String getOrderByName() {
        final SolonProps cfg = Solon.cfg();
        return cfg.get("orderByName", "orderBy");
    }

    private static String getOrderByValue(String orderByName) {
        final Context current = Context.current();
        return current.param(orderByName);
    }

    private static void checkSql(String requestProperty) {
        // 判断列名称的合法性，防止SQL注入。只能是【字母，数字，下划线】
        if (!requestProperty.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("非法的排序字段名称：" + requestProperty);
        }
    }
}
