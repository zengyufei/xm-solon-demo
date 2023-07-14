package com.xunmo.execl.converts;

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

import java.util.Date;

/**
 * 自定义时间格式转换器
 * <p>
 * 格式：yyyy-MM-dd
 *
 * @author Greenarrow
 */
public class ExeclDateConverter implements Converter<Date> {

	@Override
	public Class<?> supportJavaTypeKey() {
		return Date.class;
	}

	@Override
	public CellDataTypeEnum supportExcelTypeKey() {
		return CellDataTypeEnum.STRING;
	}

	@Override
	public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		final String stringValue = cellData.getStringValue();
		if (StrUtil.isBlank(stringValue)) {
			return null;
		}

		return XmDateUtil.checkDate(stringValue, str -> {
			AjaxError.getAndThrow("日期 {} 输入非法", stringValue);
		});
	}

	@Override
	public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		if (value == null) {
			return null;
		}
		return new WriteCellData(DateUtil.format(value, "yyyy-MM-dd"));
	}

}
