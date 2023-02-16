package com.xunmo.webs.dict.convert;

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.mapper.DictMapper;
import org.noear.solon.Solon;

public class FindDictParentIdConvert implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        final String stringValue = cellData.getStringValue();
        if (StrUtil.isBlank(stringValue)) {
            return null;
        }
        final DictMapper mapper = Solon.context().getBean(DictMapper.class);
        final Dict selectOne = mapper.selectOne(Wrappers.<Dict>lambdaQuery().eq(Dict::getDicDescription, stringValue));
        if (selectOne == null) {
            return null;
        }
        return selectOne.getParentId();
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (value == null) {
            return null;
        }
        final DictMapper mapper = Solon.context().getBean(DictMapper.class);
        final Dict selectOne = mapper.selectById(value);
        if (selectOne == null) {
            return null;
        }
        return new WriteCellData(selectOne.getDicDescription());
    }
}
