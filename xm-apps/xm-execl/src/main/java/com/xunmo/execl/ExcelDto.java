package com.xunmo.execl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelDto {

	private List<List<String>> head = new ArrayList<>();// 表头

	private List<List<Object>> tmpContent = new ArrayList<>();// 临时数据

	private List<List<Object>> content = new ArrayList<>();// 保存全量数据 高并发或者数据量较大时不建议使用
															// [如需开启 重写 enableContent()方法
															// 返回true]

	private boolean isHaveError;// 默认false

	private int count = 3000;// 三千条数据 提交一次

	private String url;// 文件上传后的地址

	private String filePath;// 文件上传后的地址

	private String sheetName;// 文件上传后的地址

}
