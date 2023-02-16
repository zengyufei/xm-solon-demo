package com.xunmo.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.xunmo.annotations.AuditFunc;
import com.xunmo.annotations.AuditMapFunc;
import com.xunmo.core.utils.AjaxError;
import com.xunmo.core.utils.LamUtil;
import com.xunmo.core.utils.XmMap;
import com.xunmo.utils.XmPageUtil;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.ValueChange;
import org.noear.solon.core.handle.Context;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 寻墨工具类
 *
 * @author zengyufei
 * @date 2021/11/22
 */
@Slf4j
public class XmUtil {

    private final static Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
            .build();


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    /**
     * 获取值到 request
     *
     * @return {@link String}
     */
    public static String getSortVar() {
        return getParam("sort");
    }

    /**
     * 获取值到 request
     *
     * @param key 关键
     * @return {@link T}
     */
    public static <T> T getVar(String key) {
        final Context current = Context.current();
        return current.attr(key);
    }

    public static <T> T getVar(String key, T defaultValue) {
        final Context current = Context.current();
        final T attribute = current.attr(key, defaultValue);
        if (attribute == null) {
            return defaultValue;
        }
        return attribute;
    }
    /**
     * 设置值到 request
     *
     * @param key 关键
     * @param o   o
     */
    public static void setVar(String key, Object o) {
        final Context current = Context.current();
        current.attrSet(key, o);
    }

    public static void removeVar(String key) {
        final Context current = Context.current();
    }

    /**
     * 获取值到 request
     *
     * @param key 关键
     * @return {@link String}
     */
    public static String getParam(String key) {
        final Context current = Context.current();
        return current.param(key);
    }

    /**
     * 获取值到 request
     *
     * @param key 关键
     * @return {@link String}
     */
    public static String getParam(String key, String defaultValue) {
        final Context current = Context.current();
        return current.param(key, defaultValue);
    }


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    public static <T> void compare(T left, T right, AuditFunc auditFunc) {
        final Changes changes = javers.compare(left, right).getChanges();
        final List<ValueChange> valueChanges = LamUtil.mapToList(changes, change -> (ValueChange) change);
        auditFunc.audit(valueChanges);
    }

    public static <T> void compareMap(T left, T right, AuditMapFunc auditFunc) {
        final Changes changes = javers.compare(left, right).getChanges();
        final XmMap<String, ValueChange> changeEduMap = new XmMap<>();
        changes.stream()
                .map(change -> (ValueChange) change)
                .forEach(valueChange -> changeEduMap.put(valueChange.getPropertyName(), valueChange));
        auditFunc.audit(changeEduMap);
    }


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    public static <T> List<T>[] getChangeAttr(List<T> oldList, List<T> newList) {
        // 交集
        final List<T> existsList = (List<T>) CollUtil.intersection(oldList, newList);
        // 待删除，与交集单差集
        final List<T> stayAddIds = CollUtil.subtractToList(newList, existsList);
        // 待删除，与交集单差集
        final List<T> stayDelIds = CollUtil.subtractToList(oldList, existsList);
        return new List[]{stayAddIds, stayDelIds, existsList};
    }

    public static Map getStaticValueAsMap(Class<?> targetClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        // 这里必须预先实例化对象,否则它的静态字段不会加载
        Field name = targetClass.getField(fieldName);
        // 注意，上面的Field实例是通过Class获取的，但是下面的获取静态属性的值没有依赖到Class
        return (Map) unsafe.getObject(unsafe.staticFieldBase(name), unsafe.staticFieldOffset(name));
    }

    public static String getStaticValueAsString(Class<?> targetClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        // 这里必须预先实例化对象,否则它的静态字段不会加载
        Field name = targetClass.getField(fieldName);
        // 注意，上面的Field实例是通过Class获取的，但是下面的获取静态属性的值没有依赖到Class
        return (String) unsafe.getObject(unsafe.staticFieldBase(name), unsafe.staticFieldOffset(name));
    }


    public static List<String> sortChineseNumber(List<String> tempNames) {
        String sortAttr = "0123456789零一二三四五六七八九十";
        return CollUtil.sort(tempNames, (o1, o2) -> {
            final int o1Len = o1.length();
            final int o2Len = o2.length();
            final int length = sortAttr.length();

            int o1Scope = 0;
            int o2Scope = 0;
            for (int i = 0; i < Math.min(o1Len, o2Len); i++) {
                final char c1 = o1.charAt(i);
                final char c2 = o2.charAt(i);
                final int i1 = sortAttr.indexOf(c1);
                final int i2 = sortAttr.indexOf(c2);
                if (i1 > -1 && i2 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + i1;
                        o2Scope = o2Scope + i2;
                    } else {
                        o1Scope = o1Scope + i1 + (i * length);
                        o2Scope = o2Scope + i2 + (i * length);
                    }
                    if (i1 > 9) {
                        o1Scope = o1Scope + 100;
                    }
                    if (i2 > 9) {
                        o2Scope = o2Scope + 100;
                    }
                } else if (i1 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + i1;
                        o2Scope = o2Scope + 100000;
                    } else {
                        o1Scope = o1Scope + i1 + +(i * length);
                        o2Scope = o2Scope + 0;
                    }
                    if (i1 > 9) {
                        o1Scope = o1Scope + 100;
                    }
                    break;
                } else if (i2 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + 100000;
                        o2Scope = o2Scope + i2;
                    } else {
                        o1Scope = o1Scope + 0;
                        o2Scope = o2Scope + i2 + +(i * length);
                    }
                    if (i2 > 9) {
                        o2Scope = o2Scope + 100;
                    }
                    break;
                } else {
                    break;
                }
            }
//            System.out.println(o1 + ", " + o2 + " === " + o1Scope + ", " + o2Scope);
            if (o1Scope > o2Scope) {
                return 1; // o1后面
            } else {
                return -1; // o1前面
            }
        });
    }


    public static <T> List<T> sortChineseNumber(List<T> tempNames, com.xunmo.ext.SFunction<T, String> func) {
        String sortAttr = "0123456789零一二三四五六七八九十";
        return CollUtil.sort(tempNames, (t1, t2) -> {
            final String o1 = (String) ReflectUtil.getFieldValue(t1, XmMap.getField(func));
            final String o2 = (String) ReflectUtil.getFieldValue(t2, XmMap.getField(func));
            final int o1Len = o1.length();
            final int o2Len = o2.length();
            final int length = sortAttr.length();

            int o1Scope = 0;
            int o2Scope = 0;
            for (int i = 0; i < Math.min(o1Len, o2Len); i++) {
                final char c1 = o1.charAt(i);
                final char c2 = o2.charAt(i);
                final int i1 = sortAttr.indexOf(c1);
                final int i2 = sortAttr.indexOf(c2);
                if (i1 > -1 && i2 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + i1;
                        o2Scope = o2Scope + i2;
                    } else {
                        o1Scope = o1Scope + i1 + (i * length);
                        o2Scope = o2Scope + i2 + (i * length);
                    }
                    if (i1 > 9) {
                        o1Scope = o1Scope + 100;
                    }
                    if (i2 > 9) {
                        o2Scope = o2Scope + 100;
                    }
                } else if (i1 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + i1;
                        o2Scope = o2Scope + 100000;
                    } else {
                        o1Scope = o1Scope + i1 + +(i * length);
                        o2Scope = o2Scope + 0;
                    }
                    if (i1 > 9) {
                        o1Scope = o1Scope + 100;
                    }
                    break;
                } else if (i2 > -1) {
                    if (i == 0) {
                        o1Scope = o1Scope + 100000;
                        o2Scope = o2Scope + i2;
                    } else {
                        o1Scope = o1Scope + 0;
                        o2Scope = o2Scope + i2 + +(i * length);
                    }
                    if (i2 > 9) {
                        o2Scope = o2Scope + 100;
                    }
                    break;
                } else {
                    break;
                }
            }
//            System.out.println(o1 + ", " + o2 + " === " + o1Scope + ", " + o2Scope);
            if (o1Scope > o2Scope) {
                return 1; // o1后面
            } else {
                return -1; // o1前面
            }
        });
    }

    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    /**
     * 性能损耗少一点的抛异常方法
     *
     * @param codition    codition
     * @param msgSupplier 消息供应商
     * @throws CustomException 自定义异常
     */
    @SuppressWarnings({"all"})
    public static void throwParamError(boolean codition, Supplier<String> msgSupplier) throws AjaxError {
        if (codition) {
            throw new AjaxError(msgSupplier.get());
        }
    }


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================


    public static <K, V> XmMap<K, V> newXmMap() {
        return new XmMap<>();
    }

    public static <E> Page<E> startPage(Class<?> entityClass, ISelect select) {
        return XmPageUtil.startPage(entityClass, select);
    }

    public static <E> Page<E> startPage(ISelect select) {
        return XmPageUtil.startPage(null, select);
    }

    public static <E> Page<E> startPage() {
        return XmPageUtil.startPage(null, null);
    }


    public static <T> T getOneByMap(Map<String, T> map, String key, Supplier<T> func) {
        T t;
        if (map.containsKey(key)) {
            t = map.get(key);
        } else {
            t = func.get();
            map.put(key, t);
        }
        return t;
    }



    /**
     * 判断时间区间是否包含手动指定的月份
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @param months    个月
     * @return boolean
     */
    public static boolean isContainMonths(Date beginDate, Date endDate, Integer... months) {
        final List<DateTime> dateTimes = DateUtil.rangeToList(beginDate, endDate, DateField.MONTH);
        final List<Integer> monthList = LamUtil.mapToList(dateTimes, DateTime::monthBaseOne);
        return CollUtil.containsAll(monthList, Arrays.asList(months));
    }


    /**
     * 切片运行
     *
     * @return boolean
     */
    public static <S> void splitListThousand(List<S> lists, Consumer<List<S>> consumer) {
        if (CollUtil.isEmpty(lists)) {
            return;
        }
        for (List<S> list : CollUtil.split(lists, 1000)) {
            consumer.accept(list);
        }
    }
}
