package com.xunmo.execl.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 读取excel监听器
 *
 * @author wenjinhang
 * @date 2022/08/27
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ImportExcelListener<T> extends AnalysisEventListener<T> {

	/**
	 * 解析的数据
	 */
	private List<T> list = new ArrayList<>();

	/**
	 * 正文起始行
	 */
	private Integer headRowNumber;

	/**
	 * 正文结束行
	 */
	private Integer endRowNumber;

	/**
	 * 合并单元格
	 */
	private List<CellExtra> extraMergeInfoList = new ArrayList<>();

	@Getter
	private Map<String, Integer> propNameByColumnIndexMap = new HashMap<>();

	public ImportExcelListener(Integer headRowNumber) {
		this.headRowNumber = headRowNumber;
	}

	public ImportExcelListener(Integer headRowNumber, Integer endRowNumber) {
		this.headRowNumber = headRowNumber;
		this.endRowNumber = endRowNumber;
	}

	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		super.invokeHeadMap(headMap, context);
		// final Integer rowIndex = context.readRowHolder().getRowIndex();
		// if ((this.headRowNumber - 1) != rowIndex) {
		// return;
		// }
		for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
			final Integer key = entry.getKey();
			final String value = entry.getValue();
			if (StrUtil.isBlankOrUndefined(value)) {
				continue;
			}
			propNameByColumnIndexMap.put(value, key);
		}
	}

	/**
	 * 这个每一条数据解析都会来调用
	 * @param data one row value. Is is same as {@link AnalysisContext#readRowHolder()}
	 * @param context context
	 */
	@Override
	public void invoke(T data, AnalysisContext context) {
		final Integer rowIndex = context.readRowHolder().getRowIndex();
		if (this.endRowNumber != null && this.endRowNumber > 0 && rowIndex > this.endRowNumber) {
			return;
		}
		if (isLineNullValue(data)) {
			return;
		}
		list.add(data);
	}

	/**
	 * 所有数据解析完成了 都会来调用
	 * @param context context
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
	}

	/**
	 * 返回解析出来的List
	 */
	public List<T> getData() {
		return list;
	}

	/**
	 * 读取额外信息：合并单元格
	 */
	@Override
	public void extra(CellExtra extra, AnalysisContext context) {
		switch (extra.getType()) {
			case MERGE: {
				if (extra.getRowIndex() >= headRowNumber) {
					extraMergeInfoList.add(extra);
				}
				break;
			}
			default:
		}
	}

	/**
	 * 返回解析出来的合并单元格List
	 */
	public List<CellExtra> getExtraMergeInfoList() {
		return extraMergeInfoList;
	}

	/**
	 * 判断整行单元格数据是否均为空
	 */
	private boolean isLineNullValue(T data) {
		if (data instanceof String) {
			return Objects.isNull(data);
		}
		if (data instanceof Map) {
			return ((Map<Integer, String>) data).entrySet()
				.parallelStream()
				.allMatch(entry -> StrUtil.isBlank(entry.getValue()));
		}
		try {
			List<Field> fields = Arrays.stream(data.getClass().getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(ExcelProperty.class))
				.collect(Collectors.toList());
			List<Boolean> lineNullList = new ArrayList<>(fields.size());
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(data);
				if (Objects.isNull(value)) {
					lineNullList.add(Boolean.TRUE);
				}
				else {
					lineNullList.add(Boolean.FALSE);
				}
			}
			return lineNullList.stream().allMatch(Boolean.TRUE::equals);
		}
		catch (Exception e) {
			log.error("读取数据行[{}]解析失败: {}", data, e.getMessage());
		}
		return true;
	}

}
