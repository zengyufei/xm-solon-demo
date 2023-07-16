package com.xunmo.common.utils;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.annotations.AuditFunc;
import com.xunmo.annotations.AuditMapFunc;
import com.xunmo.common.CustomException;
import com.xunmo.common.XmFunction;
import com.xunmo.core.AjaxError;
import com.xunmo.core.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.ValueChange;
import org.noear.solon.core.handle.Context;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * 寻墨工具类
 *
 * @author zengyufei
 * @date 2021/11/22
 */
@Slf4j
public class XmUtil extends CheckUtil {

	private final static Javers javers = JaversBuilder.javers()
		.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
		.build();

	public static String formatToDateStr(String parameter) {
		return XmDateUtil.formatToDateStr(parameter);
	}

	public static DateTime checkDateStr(String parameter, Consumer<String> errorMsg) {
		return XmDateUtil.checkDateStr(parameter, errorMsg);
	}

	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	/**
	 * 获取值到 request
	 * @return {@link String}
	 */
	public static String getSortVar() {
		return getParam("sort");
	}

	/**
	 * 获取值到 request
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
	 * @param key 关键
	 * @param o o
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
	 * @param key 关键
	 * @return {@link String}
	 */
	public static String getParam(String key) {
		final Context current = Context.current();
		return current.param(key);
	}

	/**
	 * 获取值到 request
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
	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	public static <T> void compare(T left, T right, AuditFunc auditFunc) {
		final Changes changes = javers.compare(left, right).getChanges();
		auditFunc.audit(changes);
	}

	public static <T> void compareMap(T left, T right, AuditMapFunc auditFunc) {
		final Changes changes = javers.compare(left, right).getChanges();
		final XmMap<String, ValueChange> changeEduMap = new XmMap<>();
		changes.stream()
			.filter(change -> change instanceof ValueChange)
			.map(change -> (ValueChange) change)
			.forEach(valueChange -> changeEduMap.put(valueChange.getPropertyName(), valueChange));
		auditFunc.audit(changeEduMap);
	}

	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	public static <T> List<T>[] getChangeCudAttr(List<T> oldList, List<T> newList) {
		// 交集
		final List<T> existsList = (List<T>) CollUtil.intersection(oldList, newList);
		// 待删除，与交集单差集
		final List<T> stayAddIds = CollUtil.subtractToList(newList, existsList);
		// 待删除，与交集单差集
		final List<T> stayDelIds = CollUtil.subtractToList(oldList, existsList);
		return new List[] { stayAddIds, stayDelIds, existsList };
	}

	/**
	 * 计算两个对象列表的交集、并集和差集
	 * @param oldList 旧对象列表
	 * @param newList 新对象列表
	 * @param mapper 判断两个对象是否相等的函数式接口
	 * @param <T> 对象类型
	 * @return 包含三个列表的数组，分别为差集中需要添加的对象列表、差集中需要删除的对象列表和交集的对象列表
	 */
	public static <T> List<T>[] getChangeCudAttr(List<T> oldList, List<T> newList, BiFunction<T, T, Boolean> mapper) {
		return LamUtil.getChangeCudAttr(oldList, newList, mapper);
	}

	public static Map getStaticValueAsMap(Class<?> targetClass, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafe.setAccessible(true);
		Unsafe unsafe = (Unsafe) theUnsafe.get(null);
		// 这里必须预先实例化对象,否则它的静态字段不会加载
		Field name = targetClass.getField(fieldName);
		// 注意，上面的Field实例是通过Class获取的，但是下面的获取静态属性的值没有依赖到Class
		return (Map) unsafe.getObject(unsafe.staticFieldBase(name), unsafe.staticFieldOffset(name));
	}

	public static String getStaticValueAsString(Class<?> targetClass, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafe.setAccessible(true);
		Unsafe unsafe = (Unsafe) theUnsafe.get(null);
		// 这里必须预先实例化对象,否则它的静态字段不会加载
		Field name = targetClass.getField(fieldName);
		// 注意，上面的Field实例是通过Class获取的，但是下面的获取静态属性的值没有依赖到Class
		return (String) unsafe.getObject(unsafe.staticFieldBase(name), unsafe.staticFieldOffset(name));
	}

	/**
	 * 文件名称排序 排序规则： 优先排序阿拉伯数字，其次排序中文数字，
	 * @param filenames 文件名
	 */
	public static void sortFileName(List<String> filenames) {
		final Map<Character, Integer> WEIGHTS_MAP = new HashMap<Character, Integer>() {
			{
				put('0', 0);
				put('1', 10);
				put('2', 20);
				put('3', 30);
				put('4', 40);
				put('5', 50);
				put('6', 60);
				put('7', 70);
				put('8', 80);
				put('9', 90);
				put('零', 1);
				put('一', 11);
				put('二', 21);
				put('三', 31);
				put('四', 41);
				put('五', 51);
				put('六', 61);
				put('七', 71);
				put('八', 81);
				put('九', 91);
				put('十', 100);
			}
		};
		filenames.sort((o1, o2) -> {
			int len1 = o1.length(), len2 = o2.length();
			int minLen = Math.min(len1, len2);
			for (int i = 0; i < minLen; i++) {
				char c1 = o1.charAt(i);
				char c2 = o2.charAt(i);
				if (c1 == c2) {
					continue;
				}
				Integer w1 = WEIGHTS_MAP.getOrDefault(c1, Integer.MAX_VALUE);
				Integer w2 = WEIGHTS_MAP.getOrDefault(c2, Integer.MAX_VALUE);
				if (!Objects.equals(w1, w2)) {
					return Integer.compare(w1, w2);
				}
				else {
					if (w1 % 10 == 0) {// w1 是0123456789中的一个
						return Integer.compare(c1, c2);
					}
				}
			}
			// 如果前面都相同，比较长度，长度小的排前面
			return Integer.compare(len1, len2);
		});
	}

	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	/**
	 * 拼接
	 * @param originList 源列表
	 * @param delimiter 分隔符
	 * @return {@link String}
	 */
	public static String listJoinToStr(List<String> originList, String delimiter) {
		return LamUtil.join(originList, delimiter);
	}

	/**
	 * 拼接
	 * @param originList 源列表
	 * @param delimiter 分隔符
	 * @param mapper 方法
	 * @return {@link String}
	 */
	public static <T> String listJoinToStr(List<T> originList, String delimiter, Function<T, String> mapper) {
		return LamUtil.join(originList, delimiter, mapper);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T> List<T> listFiltersToList(List<T> originList, Predicate<T>... filters) {
		return LamUtil.filterToList(originList, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T> List<T> listFilterDistinctToList(List<T> originList, Predicate<T>... filters) {
		return LamUtil.filterDistinctToList(originList, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T, R> List<R> listFiltersMapToList(List<T> originList, Function<T, R> function,
			Predicate<T>... filters) {
		return LamUtil.filtersMapToList(originList, function, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T, R> List<R> listFiltersDistinctMapToList(List<T> originList, Function<T, R> function,
			Predicate<T>... filters) {
		return LamUtil.filtersDistinctMapToList(originList, function, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T, R> List<R> listFiltersMapDistinctToList(List<T> originList, Function<T, R> function,
			Predicate<T>... filters) {
		return LamUtil.filtersMapDistinctToList(originList, function, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @return List<T>
	 */
	@SafeVarargs
	public static <R> List<R> listFiltersBlankDistinctMapToList(List<String> originList, Function<String, R> function,
			Predicate<String>... filters) {
		return LamUtil.filterBlankDistinctMapToList(originList, function, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	@SafeVarargs
	public static <T> List<String> listMapFiltersBlankDistinctToList(List<T> originList, Function<T, String> function,
			Predicate<String>... filters) {
		return LamUtil.mapFiltersBlankDistinctToList(originList, function, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @return List<T>
	 */
	@SafeVarargs
	public static List<String> listFiltersBlankDistinctToList(List<String> originList, Predicate<String>... filters) {
		return LamUtil.filterBlankDistinctToList(originList, filters);
	}

	/**
	 * list根据对象指定属性去重
	 * @param list：要操作的list集合
	 * @param keyExtractor: 去重属性
	 * @return List<T>
	 */
	public static <T> List<T> listDistinctTolist(List<T> list, Function<? super T, ?> keyExtractor) {
		return LamUtil.distinctToList(list, keyExtractor);
	}

	/**
	 * 删除空
	 * @param list 列表
	 * @return {@link List}<{@link T}>
	 */
	public static <T> List<T> listRemoveNullToList(List<T> list) {
		return LamUtil.removeNull(list);
	}

	/**
	 * 过滤移除
	 * @param originList 源列表
	 * @param removeConditions 删除条件
	 * @return {@link List}<{@link T}>
	 */
	@SafeVarargs
	public static <T> List<T> listRemoveFiltersToList(List<T> originList, Predicate<? super T>... removeConditions) {
		return LamUtil.removeFilter(originList, removeConditions);
	}

	/**
	 * 找到
	 * @param originList 原数据
	 * @param filters 映射规则集合
	 * @param <T> 原数据的元素类型
	 * @return Optional<T>
	 */
	@SafeVarargs
	public static <T> Optional<T> listFiltersToFindFirstOptional(List<T> originList, Predicate<T>... filters) {
		return LamUtil.filtersToFindFirstOptional(originList, filters);
	}

	@SafeVarargs
	public static <T> T listFiltersToFindFirst(List<T> originList, Predicate<T>... filters) {
		return LamUtil.filtersToFindFirst(originList, filters);
	}

	@SafeVarargs
	public static <T, U> U listMapFiltersToFindFirst(List<T> originList, Function<T, U> mapper,
			Predicate<U>... filters) {
		return LamUtil.mapFiltersToFindFirst(originList, mapper, filters);
	}

	/**
	 * 匹配
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @return boolean
	 */
	public static <T> boolean listAnyMatch(List<T> originList, Predicate<T> mapper) {
		return LamUtil.anyMatch(originList, mapper);
	}

	/**
	 * 匹配
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @return boolean
	 */
	public static <T> boolean listNoneMatch(List<T> originList, Predicate<T> mapper) {
		return LamUtil.noneMatch(originList, mapper);
	}

	/**
	 * 不匹配
	 * @param str 字符
	 * @param fns 实体字段
	 * @param <T> 原数据的元素类型
	 * @return boolean
	 */
	@SafeVarargs
	public static <T> boolean strNotInList(String str, XmFunction<T, ?>... fns) {
		return !strInList(str, fns);
	}

	/**
	 * 匹配
	 * @param str 字符
	 * @param fns 实体字段
	 * @param <T> 原数据的元素类型
	 * @return boolean
	 */
	@SafeVarargs
	public static <T> boolean strInList(String str, XmFunction<T, ?>... fns) {
		return XmUtil.getFieldNames(fns).contains(str);
	}

	/**
	 * map后match
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return boolean
	 */
	public static <T, R> boolean listMapAnyMatch(List<T> originList, Function<T, R> mapper, Predicate<R> predicate) {
		return LamUtil.mapAnyMatch(originList, mapper, predicate);
	}

	/**
	 * map后match
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return boolean
	 */
	public static <T, R> boolean listMapDistinctAnyMatch(List<T> originList, Function<T, R> mapper,
			Predicate<R> predicate) {
		return LamUtil.mapDistinctAnyMatch(originList, mapper, predicate);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return List<R>
	 */
	@SafeVarargs
	public static <T, R> List<R> listMapToList(List<T> originList, Function<T, R>... filters) {
		return LamUtil.mapToList(originList, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param filters 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return List<R>
	 */
	@SafeVarargs
	public static <T, R> List<R> listMapDistinctToList(List<T> originList, Function<T, R>... filters) {
		return LamUtil.mapDistinctToList(originList, filters);
	}

	public static <T> List<T> listDistinctToList(List<T> originList) {
		return LamUtil.distinctToList(originList);
	}

	@SafeVarargs
	public static <T, R> List<R> listMapFiltersDistinctToList(List<T> originList, Function<T, R> mapper,
			Predicate<R>... filters) {
		return LamUtil.mapDistinctFiltersToList(originList, mapper, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return List<R>
	 */
	@SuppressWarnings({ "all" })
	public static <T, R> List<R> mapFiltersDistinctToList(List<T> originList, Function<T, R> mapper,
			Predicate<R>... filters) {
		return LamUtil.mapFiltersDistinctToList(originList, mapper, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return List<R>
	 */
	@SuppressWarnings({ "all" })
	public static <T, R> List<R> listMapFiltersToList(List<T> originList, Function<T, R> mapper,
			Predicate<R>... filters) {
		return LamUtil.mapFiltersToList(originList, mapper, filters);
	}

	/**
	 * 将List映射为List，比如List<Person> personList转为List<String> nameList
	 * @param originList 原数据
	 * @param mapper 映射规则
	 * @param <T> 原数据的元素类型
	 * @param <R> 新数据的元素类型
	 * @return List<R>
	 */
	@SuppressWarnings({ "all" })
	public static <T, R> List<R> listMapDistinctFiltersToList(List<T> originList, Function<T, R> mapper,
			Predicate<R>... filters) {
		return LamUtil.mapDistinctFiltersToList(originList, mapper, filters);
	}

	@SafeVarargs
	public static <T> long listCount(List<T> taskOrders, Predicate<T>... filters) {
		return LamUtil.count(taskOrders, filters);
	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	public static <K, V> Map<K, V> listToBeanMap(List<V> originList, Function<V, K> keyExtractor) {
		return LamUtil.toBeanMap(originList, keyExtractor);
	}

	public static <K, V> Map<K, V> listToBeanLinkedMap(List<V> originList, Function<V, K> keyExtractor) {

		return LamUtil.toBeanLinkedMap(originList, keyExtractor);
	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	public static <K, V, S> Map<K, S> listToMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor) {
		return LamUtil.toMap(originList, keyExtractor, valueExtractor);
	}

	public static <K, V, S> Map<K, S> listFilterToMap(List<V> originList, Predicate<V> filter,
			Function<V, K> keyExtractor, Function<V, S> valueExtractor) {
		return LamUtil.filterToMap(originList, filter, keyExtractor, valueExtractor);
	}

	public static <K, V> Map<K, V> listFilterToBeanMap(List<V> originList, Predicate<V> filter,
			Function<V, K> keyExtractor) {
		return LamUtil.filterToBeanMap(originList, filter, keyExtractor);
	}

	public static <K, V> Map<K, V> listFilterToBeanMergeMap(List<V> originList, Predicate<V> filter,
			Function<V, K> keyExtractor, BinaryOperator<V> mergeExtractor) {
		return LamUtil.filterToBeanMergeMap(originList, filter, keyExtractor, mergeExtractor);
	}

	public static <K, V> Map<K, V> listFiltersToBeanMap(List<V> originList, Function<V, K> keyExtractor,
			Predicate<V>... filters) {
		return LamUtil.filtersToBeanMap(originList, keyExtractor, filters);

	}

	public static <K, V, S> Map<K, S> listToLinkedMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor) {
		return LamUtil.toLinkedMap(originList, keyExtractor, valueExtractor);

	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	public static <K, V, S> Map<K, S> listToMergeMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
		return LamUtil.toMergeMap(originList, keyExtractor, valueExtractor, mergeExtractor);
	}

	public static <K, V, S> Map<K, S> listToMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
		return LamUtil.toMergeLinkedMap(originList, keyExtractor, valueExtractor, mergeExtractor);
	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	public static <K, V> Map<K, V> listToBeanMergeMap(List<V> originList, Function<V, K> keyExtractor,
			BinaryOperator<V> mergeExtractor) {
		return LamUtil.toBeanMergeMap(originList, keyExtractor, mergeExtractor);
	}

	public static <K, V> Map<K, V> listToBeanMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor,
			BinaryOperator<V> mergeExtractor) {
		return LamUtil.toBeanMergeLinkedMap(originList, keyExtractor, mergeExtractor);
	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	public static <K, V, S> Map<K, S> listFilterToMergeMap(List<V> originList, Predicate<V> filter,
			Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
		return LamUtil.filterToMergeMap(originList, filter, keyExtractor, valueExtractor, mergeExtractor);
	}

	public static <K, V, S> Map<K, S> listFilterToMergeLinkedMap(List<V> originList, Predicate<V> filter,
			Function<V, K> keyExtractor, Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor) {
		return LamUtil.filterToMergeLinkedMap(originList, filter, keyExtractor, valueExtractor, mergeExtractor);
	}

	/**
	 * 将List转为Map
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, V>
	 */
	@SafeVarargs
	public static <K, V, S> Map<K, S> listFiltersToMergeMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor, Predicate<V>... filters) {
		return LamUtil.filtersToMergeMap(originList, keyExtractor, valueExtractor, mergeExtractor, filters);
	}

	@SafeVarargs
	public static <K, V, S> Map<K, S> listFiltersToMergeLinkedMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, S> valueExtractor, BinaryOperator<S> mergeExtractor, Predicate<V>... filters) {
		return LamUtil.filtersToMergeLinkedMap(originList, keyExtractor, valueExtractor, mergeExtractor, filters);
	}

	/**
	 * 将List分组
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, List < V>>
	 */
	public static <K, V> Map<K, List<V>> listGroupByToBeanMap(List<V> originList, Function<V, K> keyExtractor) {
		return LamUtil.groupByToBeanMap(originList, keyExtractor);
	}

	public static <K, V, U extends Comparable<? super U>> List<V> listGroupByMaxValueToList(List<V> originList,
			Function<V, K> keyExtractor, Function<? super V, ? extends U> function) {
		return LamUtil.groupByMaxValueToList(originList, keyExtractor, function);
	}

	public static <K, V, U extends Comparable<? super U>> List<V> listGroupByMinValueToList(List<V> originList,
			Function<V, K> keyExtractor, Function<? super V, ? extends U> function) {
		return LamUtil.groupByMinValueToList(originList, keyExtractor, function);
	}

	/**
	 * 将List分组
	 * @param originList 原数据
	 * @param keyExtractor Key的抽取规则
	 * @param <K> Key
	 * @param <V> Value
	 * @return Map<K, List < V>>
	 */
	public static <K, V, U> Map<K, List<U>> listGroupByToMap(List<V> originList, Function<V, K> keyExtractor,
			Function<V, U> valueExtractor) {
		return LamUtil.groupByToMap(originList, keyExtractor, valueExtractor);
	}

	/**
	 * 按照属性排序
	 * @param originList 原数据
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	public static <T, U extends Comparable<? super U>> List<T> sortAscLastNullToList(List<T> originList,
			Function<? super T, ? extends U> function) {
		return LamUtil.sortAscLastNullToList(originList, function);
	}

	/**
	 * 按照属性排序
	 * @param originList 原数据
	 * @param <T> 原数据的元素类型
	 * @return List<T>
	 */
	public static <T, U extends Comparable<? super U>> List<T> sortAscFirstNullToList(List<T> originList,
			Function<? super T, ? extends U> function) {
		return LamUtil.sortAscFirstNullToList(originList, function);
	}

	/**
	 * 按照属性排序倒序
	 * @param originList 源列表
	 * @param mapper 方法
	 * @return {@link String}
	 */
	public static <T, U extends Comparable<? super U>> List<T> sortDescLastNullToList(List<T> originList,
			Function<? super T, ? extends U> mapper) {
		return LamUtil.sortDescLastNullToList(originList, mapper);
	}

	/**
	 * 按照属性排序倒序
	 * @param originList 源列表
	 * @param mapper 方法
	 * @return {@link String}
	 */
	public static <T, U extends Comparable<? super U>> List<T> sortDescFirstNullToList(List<T> originList,
			Function<? super T, ? extends U> mapper) {
		return LamUtil.sortDescFirstNullToList(originList, mapper);
	}

	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	/**
	 * 性能损耗少一点的抛异常方法
	 * @param codition codition
	 * @param msgSupplier 消息供应商
	 * @throws CustomException 自定义异常
	 */
	@SuppressWarnings({ "all" })
	public static void throwError(boolean codition, Supplier<String> msgSupplier) throws AjaxError {
		if (codition) {
			final String errorMsg = msgSupplier.get();
			log.error(errorMsg);
			throw new AjaxError(errorMsg);
		}
	}

	/**
	 * 性能损耗少一点的抛异常方法
	 * @param codition codition
	 * @param msgSupplier 消息供应商
	 * @throws CustomException 自定义异常
	 */
	@SuppressWarnings({ "all" })
	public static void throwError(boolean codition, Supplier<String> msgSupplier, String field) throws AjaxError {
		throwError(codition, msgSupplier, () -> field);
	}

	/**
	 * 性能损耗少一点的抛异常方法
	 * @param codition codition
	 * @param msgSupplier 消息供应商
	 * @throws CustomException 自定义异常
	 */
	@SuppressWarnings({ "all" })
	public static void throwError(boolean codition, Supplier<String> msgSupplier, Supplier<String> fieldSupplier)
			throws AjaxError {
		if (codition) {
			final String errorMsg = StrUtil.format(msgSupplier.get(), fieldSupplier.get());
			log.error(errorMsg);
			throw new AjaxError(errorMsg);
		}
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Supplier<String> msgSupplier) {
		throwByIsNull(value, null, msgSupplier, null);
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Supplier<String> msgSupplier, Supplier<String> fieldSupplier) {
		throwByIsNull(value, null, msgSupplier, fieldSupplier);
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Supplier<String> msgSupplier, String field) {
		throwByIsNull(value, null, msgSupplier, () -> field);
	}

	/**
	 * 抛出异常，根据: 是否为空
	 */
	public static void throwByIsNull(Object value, Integer code, Supplier<String> msgSupplier,
			Supplier<String> fieldSupplier) {
		if (isNull(value)) {
			String field = null;
			if (fieldSupplier != null) {
				field = fieldSupplier.get();
			}
			final String errorMsg = StrUtil.format(msgSupplier.get(), field);
			int errorCode = Objects.isNull(code) ? AjaxError.DEFAULT_ERROR_CODE : code;
			throw new AjaxError(errorCode, StrUtil.blankToDefault(errorMsg, StrUtil.format("{}不能为空", field)));
		}
	}

	// =================================================================================================================
	// =================================================================================================================
	// =================================================================================================================

	public static <K, V> XmMap<K, V> newXmMap() {
		return new XmMap<>();
	}

	public static <T> String getFieldName(XmFunction<T, ?> fn) {
		return XmMap.getField(fn);
	}

	@SafeVarargs
	public static <T> List<String> getFieldNames(XmFunction<T, ?>... fns) {
		return Arrays.stream(fns).map(XmMap::getField).collect(Collectors.toList());
	}

	@SafeVarargs
	public static <T> String[] getFieldNameAttr(XmFunction<T, ?>... fns) {
		return Arrays.stream(fns).map(XmMap::getField).toArray(String[]::new);
	}

	public static <T> Method getGetter(Class<T> clazz, XmFunction<T, ?> xmFunction) {
		final BeanDesc beanDesc = BeanUtil.getBeanDesc(clazz);
		return beanDesc.getGetter(XmUtil.getFieldName(xmFunction));
	}

	public static <T> XmFunction<T, ?> getSFunction(Class<T> clazz, XmFunction<T, ?> xmFunction) {
		final BeanDesc beanDesc = BeanUtil.getBeanDesc(clazz);
		final Method disabledMethod = beanDesc.getGetter(XmUtil.getFieldName(xmFunction));
		return XmFunctionUtil.create(clazz, disabledMethod);
	}

	public static boolean lazyBoolean(Supplier<Boolean> lazyFunc) {
		return lazyFunc.get();
	}

	public static <T> LambdaBuilder<T> build(Supplier<T> constructor) {
		return LambdaBuilder.builder(constructor);
	}

	public static <T> T getOneByMap(Map<String, T> map, String key, Supplier<T> func) {
		T t;
		if (map.containsKey(key)) {
			t = map.get(key);
		}
		else {
			t = func.get();
			map.put(key, t);
		}
		return t;
	}

	/**
	 * 判断时间区间是否包含手动指定的月份
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param months 个月
	 * @return boolean
	 */
	public static boolean isContainMonths(Date beginDate, Date endDate, Integer... months) {
		final List<DateTime> dateTimes = DateUtil.rangeToList(beginDate, endDate, DateField.MONTH);
		final List<Integer> monthList = listMapToList(dateTimes, DateTime::monthBaseOne);
		return CollUtil.containsAll(monthList, Arrays.asList(months));
	}

	/**
	 * 根据年份/月份和类型获取时间范围
	 * @param year 年份
	 * @param month 月份
	 * @param type 类型（eq等于，ge大于等于，le小于等于）
	 * @return 时间范围
	 */
	public static Date[] getDateRange(Integer year, Integer month, String type) {
		Date[] dateRange = new Date[2];

		if (month == null) {
			// 只有年份
			DateTime beginOfYear = DateUtil.parse(year.toString() + "-01-01");
			DateTime endOfYear = DateUtil.endOfYear(beginOfYear);
			switch (type) {
				case "eq":
					dateRange[0] = beginOfYear.toJdkDate();
					dateRange[1] = endOfYear.toJdkDate();
					break;
				case "ge":
					dateRange[0] = beginOfYear.toJdkDate();
					break;
				case "le":
					dateRange[1] = endOfYear.toJdkDate();
					break;
				default:
					break;
			}
		}
		else {
			// 年份和月份
			DateTime beginOfMonth = DateUtil.parse(year.toString() + "-" + month + "-01");
			DateTime endOfMonth = DateUtil.endOfMonth(beginOfMonth);
			switch (type) {
				case "eq":
					dateRange[0] = beginOfMonth.toJdkDate();
					dateRange[1] = endOfMonth.toJdkDate();
					break;
				case "ge":
					dateRange[0] = beginOfMonth.toJdkDate();
					break;
				case "le":
					dateRange[1] = endOfMonth.toJdkDate();
					break;
				default:
					break;
			}
		}

		return dateRange;
	}

	/**
	 * 判断时间区间是否包含手动指定的月份
	 */
	public static <S> void splitListThousand(List<S> lists, Consumer<List<S>> consumer) {
		if (CollUtil.isEmpty(lists)) {
			return;
		}
		for (List<S> list : CollUtil.split(lists, 1000)) {
			consumer.accept(list);
		}
	}

	public static void main(String[] args) {

	}

}
