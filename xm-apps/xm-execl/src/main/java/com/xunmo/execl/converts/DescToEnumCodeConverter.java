package com.xunmo.execl.converts;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xunmo.annotations.DescToEnumCode;
import com.xunmo.common.BaseEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * desc枚举代码转换器
 *
 * @author zengyufei
 * @date 2022/08/29
 */
@Slf4j
public class DescToEnumCodeConverter implements Converter<String> {

	private static final FIFOCache<Class<? extends BaseEnum<?>>, List<BaseEnum<?>>> cache = CacheUtil.newFIFOCache(100);

	@Override
	public Class<?> supportJavaTypeKey() {
		return String.class;
	}

	@Override
	public CellDataTypeEnum supportExcelTypeKey() {
		return CellDataTypeEnum.STRING;
	}

	@Override
	public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		final DescToEnumCode descToEnumCode = contentProperty.getField().getDeclaredAnnotation(DescToEnumCode.class);
		final Class<? extends BaseEnum<?>> enumClass = descToEnumCode.value();
		List<BaseEnum<?>> codeEnums = cache.get(enumClass);
		if (CollUtil.isEmpty(codeEnums)) {
			try {
				final Method valuesFn = ReflectUtil.getMethod(enumClass, "values");
				final Object[] list = ReflectUtil.invokeStatic(valuesFn);
				codeEnums = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof BaseEnum) {
						final BaseEnum<?> codeEnum = (BaseEnum<?>) o;
						codeEnums.add(codeEnum);
					}
				}
			}
			catch (SecurityException | UtilException ignored) {
				log.error("execl 导出时, 反射获取枚举集合失败!! 相关枚举是: {}", enumClass.getName());
			}
		}
		for (BaseEnum<?> codeEnum : codeEnums) {
			final String description = codeEnum.getDescription();
			final Object code = codeEnum.getCode();
			if (StrUtil.equalsIgnoreCase(description, cellData.getStringValue())) {
				return (String) code;
			}
		}
		return null;
	}

	@Override
	public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		final DescToEnumCode descToEnumCode = contentProperty.getField().getDeclaredAnnotation(DescToEnumCode.class);
		final Class<? extends BaseEnum<?>> enumClass = descToEnumCode.value();
		List<BaseEnum<?>> codeEnums = cache.get(enumClass);
		if (CollUtil.isEmpty(codeEnums)) {
			try {
				final Method valuesFn = ReflectUtil.getMethod(enumClass, "values");
				final Object[] list = ReflectUtil.invokeStatic(valuesFn);
				codeEnums = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof BaseEnum) {
						final BaseEnum<?> codeEnum = (BaseEnum<?>) o;
						codeEnums.add(codeEnum);
					}
				}
			}
			catch (SecurityException | UtilException ignored) {
				log.error("execl 导出时, 反射获取枚举集合失败!! 相关枚举是: {}", enumClass.getName());
			}
		}
		for (BaseEnum<?> codeEnum : codeEnums) {
			final String code = (String) codeEnum.getCode();
			if (StrUtil.equalsIgnoreCase(code, value)) {
				String description = codeEnum.getDescription();
				return new WriteCellData<>(description);
			}
		}

		return new WriteCellData<>(value);
	}

}
