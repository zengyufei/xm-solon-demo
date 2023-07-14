package com.xunmo.execl.converts;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Date;

/**
 * 自定义时间格式转换器
 * <p>
 * 格式：yyyy-MM-dd
 *
 * @author Greenarrow
 */
public class ExeclDateTimeConverter implements Converter<Date> {

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
		return DateUtil.parse(cellData.getStringValue());
	}

	@Override
	public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty,
			GlobalConfiguration globalConfiguration) throws Exception {
		return new WriteCellData(DateUtil.format(value, "yyyy-MM-dd HH:mm:ss"));
	}

}
