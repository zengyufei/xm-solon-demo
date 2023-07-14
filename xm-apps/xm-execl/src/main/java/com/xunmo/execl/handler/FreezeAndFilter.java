package com.xunmo.execl.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 筛选和冻结固定表头
 *
 * @author zengyufei
 * @date 2022/12/05
 */
public class FreezeAndFilter implements SheetWriteHandler {

	public int colSplit = 0, rowSplit = 1, leftmostColumn = 0, topRow = 2;

	public String autoFilterRange = "1:1";

	public FreezeAndFilter(int colSplit, int rowSplit, String autoFilterRange) {
		this.colSplit = colSplit;
		this.rowSplit = rowSplit;
		this.autoFilterRange = autoFilterRange;
	}

	public FreezeAndFilter(int colSplit, int rowSplit, int leftmostColumn, int topRow, String autoFilterRange) {
		this.colSplit = colSplit;
		this.rowSplit = rowSplit;
		this.leftmostColumn = leftmostColumn;
		this.topRow = topRow;
		this.autoFilterRange = autoFilterRange;
	}

	@Override
	public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

	}

	@Override
	public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
		Sheet sheet = writeSheetHolder.getSheet();
		sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
		sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
	}

}
