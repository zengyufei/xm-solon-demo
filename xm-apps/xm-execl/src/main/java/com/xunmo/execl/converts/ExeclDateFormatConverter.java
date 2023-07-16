package com.xunmo.execl.converts;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xunmo.core.AjaxError;
import com.xunmo.core.utils.XmDateUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 自定义时间格式转换器
 * <p>
 * 格式：yyyy-MM-dd
 *
 * @author zengyufei
 */
public class ExeclDateFormatConverter implements Converter<String> {

	@Override
	public CellDataTypeEnum supportExcelTypeKey() {
		return CellDataTypeEnum.STRING;
	}

	@Override
	public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		final CellDataTypeEnum type = cellData.getType();
		if (CellDataTypeEnum.NUMBER.equals(type)) {
			final BigDecimal numberValue = cellData.getNumberValue();

			final long second = numberValue.multiply(new BigDecimal("86400")).longValue();
			final Instant instant = Instant.ofEpochSecond(second - 2209190400L);
			return DateUtil.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()), "yyyy-MM-dd");
		}
		else {
			final String stringValue = cellData.getStringValue();
			if (StrUtil.isBlank(stringValue)) {
				return null;
			}
			final DateTime dateTime = XmDateUtil.checkDateStr(stringValue, str -> {
				AjaxError.getAndThrow("日期 {} 输入非法", stringValue);
			});
			if (dateTime != null) {
				return dateTime.toDateStr();
			}
			return null;
		}
	}

	@Override
	public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		if (value == null) {
			return null;
		}
		return new WriteCellData(value);
	}

	@Override
	public Class<?> supportJavaTypeKey() {
		return String.class;
	}

}
