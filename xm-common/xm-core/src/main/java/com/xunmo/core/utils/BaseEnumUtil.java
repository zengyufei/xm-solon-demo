package com.xunmo.core.utils;

import cn.hutool.core.util.ObjectUtil;
import com.xunmo.common.BaseEnum;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举工具类。
 */
public class BaseEnumUtil {

	/**
	 * 作用于实现了ICodeEnum接口的枚举类。 用于通过code获取Description
	 * 通过遍历所有的枚举值，比对每个枚举的code，返回枚举值的Description
	 * @param code Integer类型
	 * @param t 枚举类.class
	 * @param <T>
	 * @return
	 */
	public static <T extends BaseEnum<S>, S> String getDescriptionByCode(S code, Class<T> t) {
		for (T item : t.getEnumConstants()) {
			if (code instanceof Number) {
				if (item.getCode() == code) {
					return item.getDescription();
				}
			}
			else if (code instanceof CharSequence) {
				if (item.getCode().equals(code)) {
					return item.getDescription();
				}
			}
			else {
				if (ObjectUtil.equal(item.getCode(), code)) {
					return item.getDescription();
				}
			}
		}
		throw new IllegalArgumentException("没有此枚举code！[" + code + "]");
	}

	/**
	 * 通过code来获取枚举对象
	 * @param code Integer类型
	 * @param t 枚举类.class
	 * @param <T>
	 * @return
	 * @author zengyufei
	 * @date 2020/4/14 14:19
	 */
	public static <T extends BaseEnum<S>, S> T getEnumByCode(S code, Class<T> t) {
		for (T item : t.getEnumConstants()) {
			if (code instanceof Number) {
				if (item.getCode() == code) {
					return item;
				}
			}
			else if (code instanceof CharSequence) {
				if (item.getCode().equals(code)) {
					return item;
				}
			}
			else {
				if (ObjectUtil.equal(item.getCode(), code)) {
					return item;
				}
			}
		}
		throw new IllegalArgumentException("没有此枚举code！[" + code + "]");
	}

	/**
	 * BaseEnum 枚举类型转 List<Item>
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> List<BaseEnum<T>> toList(Class<? extends BaseEnum<T>> clazz) {
		return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
	}

	/**
	 * BaseEnum 枚举类型转 Map<code,Enum>
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> HashMap<T, Object> toMap(Class<? extends BaseEnum<T>> clazz) {
		HashMap<T, Object> cache = new HashMap<>();
		Stream.of(clazz.getEnumConstants()).forEach(e -> {
			cache.put(e.getCode(), e);
		});
		return cache;
	}

	/**
	 * 根据 值 获取枚举
	 * @param clazz 枚举类
	 * @param code 枚举的值
	 * @param <T> 枚举值的类型
	 * @return 枚举
	 */
	public static <T> BaseEnum<T> getByCode(Class<? extends BaseEnum<T>> clazz, T code) {
		Objects.requireNonNull(code);
		List<BaseEnum<T>> list = Stream.of(clazz.getEnumConstants())
			.filter(iEnum -> iEnum.getCode().equals(code))
			.collect(Collectors.toList());
		return list.isEmpty() ? null : list.get(0);
	}

}
