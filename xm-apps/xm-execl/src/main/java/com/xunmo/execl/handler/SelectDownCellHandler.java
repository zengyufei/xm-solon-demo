package com.xunmo.execl.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SelectDownCellHandler implements SheetWriteHandler {

	private boolean isDone = false;

	private Map<String, List<String>> listDataMap = new HashMap<>();

	private List<CellInfo> cellInfos = new ArrayList<>();

	@Setter
	private boolean showErrorBox = true;

	@Setter
	private boolean suppressDropDownArrow = true;

	@Setter
	private String msg = "此值与单元格定义格式不一致";

	public SelectDownCellHandler() {
	}

	public SelectDownCellHandler(Map<String, List<String>> listDataMap, List<CellInfo> cellInfos) {
		this.listDataMap = listDataMap;
		this.cellInfos = cellInfos;
	}

	public void add(CellInfo cellInfo) {
		cellInfos.add(cellInfo);
	}

	public void put(String sheetName, List<String> listData) {
		listDataMap.put(sheetName, listData);
	}

	@Override
	public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
		if (isDone) {
			return;
		}
		if (CollUtil.isNotEmpty(listDataMap)) {
			// 获取一个workbook
			final Workbook workbook = writeWorkbookHolder.getWorkbook();
			// k 为存在下拉数据集的单元格下表 v为下拉数据集
			listDataMap.forEach((sheetName, v) -> {
				// 创建sheet，突破下拉框255的限制
				// 定义sheet的名称
				final Sheet sheet = workbook.getSheet(sheetName);
				if (sheet == null) {
					// 1.创建一个隐藏的sheet 名称为 proviceSheet
					Sheet proviceSheet = workbook.createSheet(sheetName);
					// 设置隐藏
					workbook.setSheetHidden(workbook.getSheetIndex(proviceSheet), true);
					// 2.循环赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后）
					for (int i = 0, length = v.size(); i < length; i++) {
						// i:表示你开始的行数 0表示你开始的列数
						proviceSheet.createRow(i).createCell(0).setCellValue(v.get(i));
					}
					Name category1Name = workbook.createName();
					category1Name.setNameName(sheetName);
					// 4 $A$1:$A$N代表 以A列1行开始获取N行下拉数据
					category1Name.setRefersToFormula(sheetName + "!$A$1:$A$" + (v.size()));
				}
			});
		}
		if (CollUtil.isNotEmpty(cellInfos)) {
			final Sheet sheetHolderSheet = writeSheetHolder.getSheet();
			final Workbook workbook = writeWorkbookHolder.getWorkbook();
			final DataValidationHelper helper = sheetHolderSheet.getDataValidationHelper();
			for (CellInfo cellInfo : cellInfos) {
				final String sheetName = cellInfo.getSheetName();
				final List<Map<Integer, String>> columnSheetNameMapList = cellInfo.getColumnSheetNameMapList();
				final int effectStartRowNum = cellInfo.getEffectStartRowNum();
				final int effectLastRowNum = cellInfo.getEffectLastRowNum();
				final Boolean localShowErrorBox = cellInfo.getShowErrorBox();
				final Boolean localSuppressDropDownArrow = cellInfo.getSuppressDropDownArrow();
				final String localMsg = cellInfo.getMsg();
				if (StrUtil.isBlank(sheetName)) {
					continue;
				}
				if (CollUtil.isEmpty(columnSheetNameMapList)) {
					continue;
				}
				for (Map<Integer, String> map : columnSheetNameMapList) {
					map.forEach((columnIndex, targetSheetName) -> {
						final Sheet sheet = workbook.getSheet(targetSheetName);
						if (sheet == null) {
							return;
						}
						// 5
						// 将刚才设置的sheet引用到你的下拉列表中,1表示从行的序号1开始（开始行，通常行的序号为0的行是表头），effectLastRowNum
						// 表示行的序号effectLastRowNum（结束行），表示从行的序号1到50，k表示开始列序号和结束列序号
						CellRangeAddressList addressList = new CellRangeAddressList(effectStartRowNum, effectLastRowNum,
								columnIndex, columnIndex);
						DataValidationConstraint constraint = helper.createFormulaListConstraint(sheetName);
						DataValidation dataValidation = helper.createValidation(constraint, addressList);
						// 阻止输入非下拉选项的值
						dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
						dataValidation.setShowErrorBox(localShowErrorBox == null ? showErrorBox : localShowErrorBox);
						dataValidation.setSuppressDropDownArrow(localSuppressDropDownArrow == null
								? suppressDropDownArrow : localSuppressDropDownArrow);
						dataValidation.createErrorBox("提示", localMsg == null ? msg : localMsg);
						// validation.createPromptBox("填写说明：","填写内容只能为下拉数据集中的单位，其他单位将会导致无法入仓");
						sheet.addValidationData(dataValidation);
					});
				}
			}
		}
		isDone = true;
	}

	@Data
	@Accessors
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder(builderMethodName = "of")
	public static class CellInfo {

		private String sheetName;

		private String msg;

		private int effectStartRowNum = 1;

		private int effectLastRowNum = 1000;

		private List<Map<Integer, String>> columnSheetNameMapList;

		private Boolean showErrorBox;

		private Boolean suppressDropDownArrow;

	}

}
