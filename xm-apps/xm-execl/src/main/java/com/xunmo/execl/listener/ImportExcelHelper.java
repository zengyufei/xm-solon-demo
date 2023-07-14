package com.xunmo.execl.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.xunmo.core.AjaxError;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.UploadedFile;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 导入excel工具类
 *
 * @param <T>
 * @author wenjinhang
 * @date 2022/08/27
 */
@Slf4j
public class ImportExcelHelper<T> {

	/**
	 * 检查Excel是否存在
	 * @param file 文件
	 * @throws Exception 自定义异常
	 */
	public static void checkFile(UploadedFile file) throws Exception {
		AjaxError.throwByIsNull(file, () -> "参数 file 不能为空");

		String originalFilename = file.getName();
		String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
		AjaxError.throwBy(!".xls".equals(substring) && !".xlsx".equals(substring), () -> "只能上传格式为xls或xlsx的文件");
	}

	/**
	 * 返回解析后的List
	 * @return java.util.List<T> 解析后的List
	 * @param: fileName 文件名
	 * @param: clazz Excel对应属性名
	 * @param: sheetNo 要解析的sheet
	 * @param: headRowNumber 正文起始行
	 */
	public List<T> getList(UploadedFile file, String sheetNo, Integer headRowNumber) {
		return getList(file, null, sheetNo, headRowNumber, null);
	}

	/**
	 * 返回解析后的List
	 * @return java.util.List<T> 解析后的List
	 * @param: fileName 文件名
	 * @param: clazz Excel对应属性名
	 * @param: sheetNo 要解析的sheet
	 * @param: headRowNumber 正文起始行
	 */
	public List<T> getList(UploadedFile file, Class<T> clazz, String sheetNo, Integer headRowNumber) {
		return getList(file, clazz, sheetNo, headRowNumber, null);
	}

	/**
	 * 返回解析后的List
	 * @param endRowNumber
	 * @return java.util.List<T> 解析后的List
	 * @param: fileName 文件名
	 * @param: clazz Excel对应属性名
	 * @param: sheetNo 要解析的sheet
	 * @param: headRowNumber 正文起始行
	 */
	public List<T> getList(UploadedFile file, String sheetNo, Integer headRowNumber, Integer endRowNumber) {
		return getList(file, null, sheetNo, headRowNumber, endRowNumber);
	}

	/**
	 * 返回解析后的List
	 * @param endRowNumber
	 * @return java.util.List<T> 解析后的List
	 * @param: fileName 文件名
	 * @param: clazz Excel对应属性名
	 * @param: sheetNo 要解析的sheet
	 * @param: headRowNumber 正文起始行
	 */
	public List<T> getList(UploadedFile file, Class<T> clazz, String sheetNo, Integer headRowNumber,
			Integer endRowNumber) {
		ImportExcelListener<T> listener;
		if (endRowNumber == null) {
			listener = new ImportExcelListener<>(headRowNumber);
		}
		else {
			listener = new ImportExcelListener<>(headRowNumber, endRowNumber);
		}
		ExcelReaderBuilder builder;
		if (clazz == null) {
			builder = EasyExcel.read(file.getContent(), listener);
		}
		else {
			builder = EasyExcel.read(file.getContent(), clazz, listener);
		}
		builder.extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();
		List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
		// 没有合并单元格情况，直接返回即可
		if (CollUtil.isEmpty(extraMergeInfoList)) {
			return listener.getData();
		}
		// 存在有合并单元格时，自动获取值，并校对
		return explainMergeData(listener, extraMergeInfoList, headRowNumber);
	}

	/**
	 * 返回解析后的List 试试不开线程的情况
	 * @param inputStream
	 * @param sheetNo
	 * @param headRowNumber
	 * @return
	 * @throws Exception
	 * @param: clazz Excel对应属性名
	 * @author feitan
	 * @since v1.0 2022/12/12 15:06
	 */
	public List<T> getList(InputStream inputStream, String sheetNo, Integer headRowNumber) {
		return getList(inputStream, null, sheetNo, headRowNumber, null);
	}

	/**
	 * 返回解析后的List 试试不开线程的情况
	 * @param inputStream
	 * @param sheetNo
	 * @param headRowNumber
	 * @param endRowNumber
	 * @return
	 * @throws Exception
	 * @param: clazz Excel对应属性名
	 * @author feitan
	 * @since v1.0 2022/12/12 15:06
	 */
	public List<T> getList(InputStream inputStream, String sheetNo, Integer headRowNumber, Integer endRowNumber) {
		return getList(inputStream, null, sheetNo, headRowNumber, endRowNumber);
	}

	/**
	 * 返回解析后的List 试试不开线程的情况
	 * @param inputStream
	 * @param sheetNo
	 * @param headRowNumber
	 * @return
	 * @throws Exception
	 * @param: clazz Excel对应属性名
	 * @author feitan
	 * @since v1.0 2022/12/12 15:06
	 */
	public List<T> getList(InputStream inputStream, Class<T> clazz, String sheetNo, Integer headRowNumber) {
		return getList(inputStream, clazz, sheetNo, headRowNumber, null);
	}

	/**
	 * 返回解析后的List 试试不开线程的情况
	 * @param inputStream
	 * @param sheetNo
	 * @param headRowNumber
	 * @param endRowNumber
	 * @return
	 * @throws Exception
	 * @param: clazz Excel对应属性名
	 * @author feitan
	 * @since v1.0 2022/12/12 15:06
	 */
	public List<T> getList(InputStream inputStream, Class<T> clazz, String sheetNo, Integer headRowNumber,
			Integer endRowNumber) {
		ImportExcelListener<T> listener;
		if (endRowNumber == null) {
			listener = new ImportExcelListener<>(headRowNumber);
		}
		else {
			listener = new ImportExcelListener<>(headRowNumber, endRowNumber);
		}
		ExcelReaderBuilder builder;
		if (clazz == null) {
			builder = EasyExcel.read(inputStream, listener);
		}
		else {
			builder = EasyExcel.read(inputStream, clazz, listener);
		}
		builder.extraRead(CellExtraTypeEnum.MERGE);

		ExcelReaderSheetBuilder excelReaderSheetBuilder;
		if (sheetNo == null) {
			excelReaderSheetBuilder = builder.sheet();
		}
		else {
			excelReaderSheetBuilder = builder.sheet(sheetNo);
		}
		excelReaderSheetBuilder.headRowNumber(headRowNumber).doRead();
		List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
		// 没有合并单元格情况，直接返回即可
		if (CollUtil.isEmpty(extraMergeInfoList)) {
			return listener.getData();
		}
		// 存在有合并单元格时，自动获取值，并校对
		return explainMergeData(listener, extraMergeInfoList, headRowNumber);
	}

	/**
	 * 处理合并单元格
	 * @param listener 解析数据
	 * @param extraMergeInfoList 合并单元格信息
	 * @param headRowNumber 起始行
	 * @return 填充好的解析数据
	 */
	private List<T> explainMergeData(ImportExcelListener<T> listener, List<CellExtra> extraMergeInfoList,
			Integer headRowNumber) {
		final Map<String, Integer> propNameByColumnIndexMap = listener.getPropNameByColumnIndexMap();
		final List<T> data = listener.getData();
		final int size = data.size();
		// 循环所有合并单元格信息
		extraMergeInfoList.forEach(cellExtra -> {
			int firstRowIndex = cellExtra.getFirstRowIndex() - headRowNumber;
			int lastRowIndex = cellExtra.getLastRowIndex() - headRowNumber;
			int firstColumnIndex = cellExtra.getFirstColumnIndex();
			int lastColumnIndex = cellExtra.getLastColumnIndex();
			if (firstRowIndex > size - 1) {
				return;
			}
			if (lastRowIndex > size - 1) {
				lastRowIndex = size - 1;
			}
			// 获取初始值
			Object initValue = getInitValueFromList(propNameByColumnIndexMap, firstRowIndex, firstColumnIndex, data);
			// 设置值
			for (int i = firstRowIndex; i <= lastRowIndex; i++) {
				for (int j = firstColumnIndex; j <= lastColumnIndex; j++) {
					setInitValueToList(propNameByColumnIndexMap, initValue, i, j, data);
				}
			}
		});
		return data;
	}

	/**
	 * 设置合并单元格的值
	 * @param filedValue 值
	 * @param rowIndex 行
	 * @param columnIndex 列
	 * @param data 解析数据
	 */
	private void setInitValueToList(Map<String, Integer> propNameByColumnIndexMap, Object filedValue, Integer rowIndex,
			Integer columnIndex, List<T> data) {
		T object = data.get(rowIndex);
		if (object instanceof Map) {
			final Map<Integer, String> map = (Map<Integer, String>) object;
			final Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
			for (Map.Entry<Integer, String> entry : entrySet) {
				final Integer key = entry.getKey();
				if (key.equals(columnIndex)) {
					map.put(key, (String) filedValue);
					break;
				}
			}
		}
		else {
			for (Field field : ClassUtil.getDeclaredFields(object.getClass())) {
				// 提升反射性能，关闭安全检查
				field.setAccessible(true);
				ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
				if (annotation != null) {
					final String[] valueAttr = annotation.value();
					final String last = valueAttr[valueAttr.length - 1];
					int indexed = annotation.index();
					if (indexed == -1 && propNameByColumnIndexMap.containsKey(last)) {
						indexed = propNameByColumnIndexMap.get(last);
					}
					if (indexed == columnIndex) {
						try {
							field.set(object, filedValue);
							break;
						}
						catch (IllegalAccessException e) {
							log.error("设置合并单元格的值异常：{}", e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * 获取合并单元格的初始值 rowIndex对应list的索引 columnIndex对应实体内的字段
	 * @param firstRowIndex 起始行
	 * @param firstColumnIndex 起始列
	 * @param data 列数据
	 * @return 初始值
	 */
	private Object getInitValueFromList(Map<String, Integer> propNameByColumnIndexMap, Integer firstRowIndex,
			Integer firstColumnIndex, List<T> data) {
		Object filedValue = null;
		T object = data.get(firstRowIndex);
		if (object instanceof Map) {
			final Map<Integer, String> map = (Map<Integer, String>) object;
			final Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
			for (Map.Entry<Integer, String> entry : entrySet) {
				final Integer key = entry.getKey();
				if (key.equals(firstColumnIndex)) {
					filedValue = map.get(key);
					break;
				}
			}
		}
		else {
			for (Field field : ClassUtil.getDeclaredFields(object.getClass())) {
				// 提升反射性能，关闭安全检查
				field.setAccessible(true);
				ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
				if (annotation != null) {
					final String[] valueAttr = annotation.value();
					final String last = valueAttr[valueAttr.length - 1];
					int indexed = annotation.index();
					if (indexed == -1 && propNameByColumnIndexMap.containsKey(last)) {
						indexed = propNameByColumnIndexMap.get(last);
					}
					if (indexed == firstColumnIndex) {
						try {
							filedValue = field.get(object);
							break;
						}
						catch (IllegalAccessException e) {
							log.error("设置合并单元格的初始值异常：{}", e.getMessage());
						}
					}
				}
			}
		}
		return filedValue;
	}

}
