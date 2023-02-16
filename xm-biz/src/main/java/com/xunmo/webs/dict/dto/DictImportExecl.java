package com.xunmo.webs.dict.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xunmo.webs.dict.convert.FindDictParentIdConvert;
import lombok.Data;

@Data
public class DictImportExecl {

    @ExcelProperty(value = "字典名", index = 0)
    private String dicDescription;
    @ExcelProperty(value = "字典值", index = 1)
    private String dicValue;
    @ExcelProperty(value = "字典编码", index = 2)
    private String dicCode;
    @ExcelProperty(value = "上级字典", index = 3)
    private String parentId;

}
