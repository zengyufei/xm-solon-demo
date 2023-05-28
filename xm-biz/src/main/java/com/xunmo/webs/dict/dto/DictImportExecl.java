package com.xunmo.webs.dict.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DictImportExecl {

    @ExcelProperty(value = "上级code")
    private String parentCode;
    @ExcelProperty(value = "上级字典名")
    private String parentName;
    @ExcelProperty(value = "字典code")
    private String dicCode;
    @ExcelProperty(value = "字典名")
    private String dicDescription;
    @ExcelProperty(value = "字典值")
    private String dicValue;

}
