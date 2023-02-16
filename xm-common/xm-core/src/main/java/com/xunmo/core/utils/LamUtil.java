package com.xunmo.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 寻墨工具类
 *
 * @author zengyufei
 * @date 2021/11/22
 */
@Slf4j
public class LamUtil {


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================


    /**
     * 拼接
     *
     * @param originList 源列表
     * @param delimiter  分隔符
     * @param mapper     方法
     * @return {@link String}
     */
    public static <T> String join(List<T> originList, String delimiter, Function<T, String> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return null;
        }
        List<String> valueList = new ArrayList<>();
        for (T obj : originList) {
            valueList.add(mapper.apply(obj));
        }
        return String.join(delimiter, valueList);
    }

    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    @SafeVarargs
    public static <T> List<T> filterToList(List<T> originList,
                                           Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        Stream<T> stream = originList.stream();
        for (Predicate<T> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    @SafeVarargs
    public static <T> List<T> filterToDistinctList(List<T> originList,
                                                   Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        boolean isDistinct = false;
        Stream<T> stream = originList.stream();
        for (Predicate<T> mapper : mappers) {
            if (!isDistinct) {
                isDistinct = true;
                stream = stream.distinct();
            }
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    @SafeVarargs
    public static <T, R> List<R> filtersToMapList(List<T> originList,
                                                  Function<T, R> function,
                                                  Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        List<T> finalOriginList = removeNull(originList);
        if (CollUtil.isEmpty(finalOriginList)) {
            return Collections.emptyList();
        }
        Stream<T> stream = finalOriginList.stream();
        for (Predicate<T> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.collectingAndThen(Collectors.toList(), ts -> {
            List<R> result = new ArrayList<>(finalOriginList.size());
            for (T t : ts) {
                result.add(function.apply(t));
            }
            return result;
        }));
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    @SafeVarargs
    public static <T, R> List<R> filtersMapToDistinctList(List<T> originList,
                                                          Function<T, R> function,
                                                          Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        List<T> finalOriginList = removeNull(originList);
        if (CollUtil.isEmpty(finalOriginList)) {
            return Collections.emptyList();
        }
        Stream<T> stream = finalOriginList.stream();
        for (Predicate<T> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.collectingAndThen(Collectors.toList(), ts -> {
            List<R> result = new ArrayList<>(finalOriginList.size());
            for (T t : ts) {
                result.add(function.apply(t));
            }
            return result.stream().distinct().collect(Collectors.toList());
        }));
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @return List<T>
     */
    @SafeVarargs
    public static <R> List<R> filterBlankDistinctToMapList(List<String> originList,
                                                           Function<String, R> function,
                                                           Predicate<String>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        List<String> finalOriginList = removeFilter(originList, StrUtil::isBlank);
        if (CollUtil.isEmpty(finalOriginList)) {
            return Collections.emptyList();
        }
        Stream<String> stream = finalOriginList.stream();
        for (Predicate<String> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.collectingAndThen(Collectors.toList(), ts -> {
            List<R> result = new ArrayList<>(finalOriginList.size());
            for (String t : ts) {
                result.add(function.apply(t));
            }
            return result.stream().distinct().collect(Collectors.toList());
        }));
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    @SafeVarargs
    public static <T> List<String> mapToFiltersBlankDistinctList(List<T> originList,
                                                                 Function<T, String> function,
                                                                 Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        List<T> finalOriginList = removeNull(originList);
        if (CollUtil.isEmpty(finalOriginList)) {
            return Collections.emptyList();
        }
        Stream<T> stream = finalOriginList.stream();
        for (Predicate<T> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.collectingAndThen(Collectors.toList(), ts -> {
            List<String> result = new ArrayList<>(finalOriginList.size());
            for (T t : ts) {
                result.add(function.apply(t));
            }
            return result.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        }));
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @return List<T>
     */
    @SafeVarargs
    public static List<String> filterBlankToDistinctList(List<String> originList,
                                                         Predicate<String>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        List<String> finalOriginList = removeFilter(originList, StrUtil::isBlank);
        if (CollUtil.isEmpty(finalOriginList)) {
            return Collections.emptyList();
        }
        Stream<String> stream = finalOriginList.stream();
        for (Predicate<String> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.distinct().collect(Collectors.toList());
    }

    /**
     * 删除空
     *
     * @param list 列表
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> removeNull(List<T> list) {
        return removeFilter(list, Objects::isNull);
    }

    /**
     * 过滤移除
     *
     * @param originList       源列表
     * @param removeConditions 删除条件
     * @return {@link List}<{@link T}>
     */
    @SafeVarargs
    public static <T> List<T> removeFilter(List<T> originList, Predicate<? super T>... removeConditions) {
        if (!Objects.isNull(originList) && !originList.isEmpty()) {
            Stream<T> stream = originList.stream();
            for (Predicate<? super T> removeCondition : removeConditions) {
                stream = stream.filter(item -> !removeCondition.test(item));
            }
            return stream.collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 找到
     *
     * @param originList 原数据
     * @param mappers    映射规则集合
     * @param <T>        原数据的元素类型
     * @return Optional<T>
     */
    @SafeVarargs
    public static <T> Optional<T> findFirst(List<T> originList,
                                            Predicate<T>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return Optional.empty();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Optional.empty();
        }
        Stream<T> stream = originList.stream();
        for (Predicate<T> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.findFirst();
    }


    /**
     * 匹配
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @return boolean
     */
    public static <T> boolean anyMatch(List<T> originList,
                                       Predicate<T> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        return originList.stream().anyMatch(mapper);
    }

    /**
     * map后match
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return boolean
     */
    public static <T, R> boolean mapAfterAnyMatch(List<T> originList,
                                                  Function<T, R> mapper,
                                                  Predicate<R> predicate) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        return originList.stream().map(mapper).anyMatch(predicate);
    }

    /**
     * map后match
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return boolean
     */
    public static <T, R> boolean mapAfterDistinctAnyMatch(List<T> originList,
                                                          Function<T, R> mapper,
                                                          Predicate<R> predicate) {
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return false;
        }
        return originList.stream().map(mapper).distinct().anyMatch(predicate);
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return List<R>
     */
    public static <T, R> List<R> mapToList(List<T> originList,
                                           Function<T, R> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        return originList.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return List<R>
     */
    public static <T, R> List<R> mapToDistinctList(List<T> originList,
                                                   Function<T, R> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        return originList.stream().map(mapper).distinct().collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return List<R>
     */
    public static <T, R> List<R> mapFilterToDistinctList(List<T> originList,
                                                         Function<T, R> mapper,
                                                         Predicate<R> filter) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        return originList.stream().map(mapper).filter(filter).distinct().collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return List<R>
     */
    @SuppressWarnings({"all"})
    public static <T, R> List<R> mapToFiltersList(List<T> originList,
                                                  Function<T, R> mapper,
                                                  Predicate<R>... filters) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        Stream<R> stream = originList.stream().map(mapper);
        for (Predicate<R> predicate : filters) {
            stream = stream.filter(predicate);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将List映射为List，比如List<Person> personList转为List<String> nameList
     *
     * @param originList 原数据
     * @param mapper     映射规则
     * @param <T>        原数据的元素类型
     * @param <R>        新数据的元素类型
     * @return List<R>
     */
    @SuppressWarnings({"all"})
    public static <T, R> List<R> mapDistinctToFiltersList(List<T> originList,
                                                          Function<T, R> mapper,
                                                          Predicate<R>... filters) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        Stream<R> stream = originList.stream().map(mapper).distinct();
        for (Predicate<R> predicate : filters) {
            stream = stream.filter(predicate);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V> Map<K, V> listToBeanMap(List<V> originList,
                                                 Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().collect(Collectors.toMap(keyExtractor, v -> v, (k1, k2) -> k1));
    }

    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V, S> Map<K, S> listToMap(List<V> originList,
                                                Function<V, K> keyExtractor,
                                                Function<V, S> valueExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().collect(Collectors.toMap(keyExtractor, valueExtractor, (k1, k2) -> k1));
    }


    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V> Map<K, V> listToBeanMap(List<V> originList,
                                                 Function<V, K> keyExtractor,
                                                 BinaryOperator<V> operator) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().collect(Collectors.toMap(keyExtractor, v -> v, operator));
    }

    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V, S> Map<K, S> listToMap(List<V> originList,
                                                Function<V, K> keyExtractor,
                                                Function<V, S> valueExtractor,
                                                BinaryOperator<S> operator) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().collect(Collectors.toMap(keyExtractor, valueExtractor, operator));
    }

    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V, S> Map<K, S> listFilterToMap(List<V> originList,
                                                      Predicate<V> filter,
                                                      Function<V, K> keyExtractor,
                                                      Function<V, S> valueExtractor,
                                                      BinaryOperator<S> operator) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().filter(filter).collect(Collectors.toMap(keyExtractor, valueExtractor, operator));
    }

    /**
     * 将List转为Map
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, V>
     */
    public static <K, V, S> Map<K, S> listFiltersToMap(List<V> originList,
                                                       Function<V, K> keyExtractor,
                                                       Function<V, S> valueExtractor,
                                                       BinaryOperator<S> operator,
                                                       Predicate<V>... mappers) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        Stream<V> stream = originList.stream();
        for (Predicate<V> mapper : mappers) {
            stream = stream.filter(mapper);
        }
        return stream.collect(Collectors.toMap(keyExtractor, valueExtractor, operator));
    }

    /**
     * 将List分组
     *
     * @param originList   原数据
     * @param keyExtractor Key的抽取规则
     * @param <K>          Key
     * @param <V>          Value
     * @return Map<K, List < V>>
     */
    public static <K, V> Map<K, List<V>> groupByToMap(List<V> originList,
                                                      Function<V, K> keyExtractor) {
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return new HashMap<>();
        }
        return originList.stream().collect(Collectors.groupingBy(keyExtractor));
    }


    /**
     * 按照属性排序
     *
     * @param originList 原数据
     * @param <T>        原数据的元素类型
     * @return List<T>
     */
    public static <T, U extends Comparable<? super U>> List<T> sortAsc(List<T> originList,
                                                                       Function<? super T, ? extends U> function) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        return originList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(function))), ArrayList::new));
    }

    /**
     * 按照属性排序倒序
     *
     * @param originList 源列表
     * @param mapper     方法
     * @return {@link String}
     */
    public static <T, U extends Comparable<? super U>> List<T> sortDesc(List<T> originList,
                                                                        Function<? super T, ? extends U> mapper) {
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        originList = removeNull(originList);
        if (CollUtil.isEmpty(originList)) {
            return Collections.emptyList();
        }
        return originList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(mapper).reversed())), ArrayList::new));
    }

    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================


    // =================================================================================================================
    // =================================================================================================================
    // =================================================================================================================

    public static <T> String getFieldName(com.xunmo.ext.SFunction<T, ?> fn) {
        return XmMap.getField(fn);
    }

    public static <T> Method getGetter(Class<T> clazz, com.xunmo.ext.SFunction<T, Object> sFunction) {
        return BeanUtil.getBeanDesc(clazz).getGetter(getFieldName(sFunction));
    }

    public static <T> LambdaBuilder<T> build(Supplier<T> constructor) {
        return LambdaBuilder.builder(constructor);
    }


}


class LambdaBuilder<T> {

    /**
     * 存储调用方 指定构造类的 构造器
     */
    private final Supplier<T> constructor;
    /**
     * 存储 指定类 所有需要初始化的类属性
     */
    private final List<Consumer<T>> dInjects = new ArrayList<>();

    private Consumer head = new Consumer() {
        @Override
        public void accept(Object o) {

        }
    };

    // V2: 构造函数私有化
    private LambdaBuilder(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> LambdaBuilder<T> builder(Supplier<T> constructor) {
        return new LambdaBuilder<>(constructor);
    }

    public <P1> LambdaBuilder<T> with(LambdaBuilder.DInjectConsumer<T, P1> consumer, P1 p1) {
        Consumer<T> c = instance -> consumer.accept(instance, p1);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }

    public <P1> LambdaBuilder<T> with(LambdaBuilder.DInjectConsumer<T, P1> consumer, P1 p1, Predicate<P1> predicate) {
        if (null != predicate && !predicate.test(p1)) {
            throw new RuntimeException(String.format("【%s】参数不符合通用业务规则！", p1));
        }
        Consumer<T> c = instance -> consumer.accept(instance, p1);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }


    public <P1, P2> LambdaBuilder<T> with(LambdaBuilder.DInjectConsumer2<T, P1, P2> consumer, P1 p1, P2 p2) {
        Consumer<T> c = instance -> consumer.accept(instance, p1, p2);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }

    public T build() {
        // 调用supplier 生成类实例
        T instance = constructor.get();
        // 调用传入的setter方法，完成属性初始化
//        dInjects.forEach(dInject -> dInject.accept(instance));
        head.accept(instance);
        // 返回 建造完成的类实例
        return instance;
    }

    @FunctionalInterface
    public interface DInjectConsumer<T, P1> {
        void accept(T t, P1 p1);
    }

    @FunctionalInterface
    public interface DInjectConsumer2<T, P1, P2> {
        void accept(T t, P1 p1, P2 p2);
    }

}
