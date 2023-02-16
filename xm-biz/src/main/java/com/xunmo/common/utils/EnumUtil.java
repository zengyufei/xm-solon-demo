package com.xunmo.common.utils;


import com.xunmo.core.common.CodeEnumItem;
import com.xunmo.ext.ICodeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举工具类。
 *
 * @auther tan
 * @date 2020/3/5
 */
public class EnumUtil {

    /**
     * 作用于实现了ICodeEnum接口的枚举类。
     * 用于通过code获取Description
     * 通过遍历所有的枚举值，比对每个枚举的code，返回枚举值的Description
     *
     * @param code Integer类型
     * @param t    枚举类.class
     * @param <T>
     * @return
     */
    public static <T extends ICodeEnum> String getDescriptionByCode(Integer code, Class<T> t) {
        for (T item : t.getEnumConstants()) {
            if (item.getCode().equals(code)) {
                return item.getDescription();
            }
        }
        return "";
    }

    /**
     * @param code string类型的code
     * @param t    枚举类.class
     * @param <T>
     * @return
     */
    public static <T extends ICodeEnum> String getDescriptionByCode(String code, Class<T> t) {
        for (T item : t.getEnumConstants()) {
            if (item.getCode().equals(code)) {
                return item.getDescription();
            }
        }
        return "";
    }

    /**
     * 通过code来获取枚举对象
     *
     * @param code Integer类型
     * @param t    枚举类.class
     * @param <T>
     * @return
     * @author zengyufei
     * @date 2020/4/14 14:19
     */
    public static <T extends ICodeEnum> T getEnumByCode(Integer code, Class<T> t) {
        for (T item : t.getEnumConstants()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        throw new IllegalArgumentException("没有此枚举code！[" + code + "]");
    }

    /**
     * ICodeEnum 枚举类型转 List<Item>
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<CodeEnumItem<T>> toList(Class<? extends ICodeEnum<T>> clazz) {
        List<CodeEnumItem<T>> list = new ArrayList<>();
        Stream.of(clazz.getEnumConstants()).forEach(e -> {
            list.add(new CodeEnumItem<T>(e.getCode(), e.getDescription()));
        });
        return list;
    }

    /**
     * ICodeEnum 枚举类型转 Map<code,Enum>
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> HashMap<T, Object> toMap(Class<? extends ICodeEnum<T>> clazz) {
        HashMap<T, Object> cache = new HashMap<>();
        Stream.of(clazz.getEnumConstants()).forEach(e -> {
            cache.put(e.getCode(), e);
        });
        return cache;
    }

    /**
     * 根据 值 获取枚举
     *
     * @param clazz 枚举类
     * @param code  枚举的值
     * @param <T>   枚举值的类型
     * @return 枚举
     */
    public static <T> ICodeEnum<T> getByCode(Class<? extends ICodeEnum<T>> clazz, T code) {
        Objects.requireNonNull(code);
        List<ICodeEnum<T>> list = Stream.of(clazz.getEnumConstants())
                .filter(iEnum -> iEnum.getCode().equals(code))
                .collect(Collectors.toList());
        return list.isEmpty() ? null : list.get(0);
    }

}
