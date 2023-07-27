package com.xunmo.execl.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * It only merges once when create cell(firstRowIndex,lastRowIndex)
 *
 * @author Jiaju Zhuang
 */
public class OnceAbsoluteMergeStrategyLeft implements SheetWriteHandler, CellWriteHandler {
	/**
	 * First row
	 */
	private int firstRowIndex;
	/**
	 * Last row
	 */
	private int lastRowIndex;
	/**
	 * First column
	 */
	private int firstColumnIndex;
	/**
	 * Last row
	 */
	private int lastColumnIndex;

	private HorizontalAlignment horizontalAlignment;

	public OnceAbsoluteMergeStrategyLeft(int firstRowIndex, int lastRowIndex, int firstColumnIndex, int lastColumnIndex, HorizontalAlignment horizontalAlignment) {
		if (firstRowIndex < 0 || lastRowIndex < 0 || firstColumnIndex < 0 || lastColumnIndex < 0) {
			throw new IllegalArgumentException("All parameters must be greater than 0");
		}
		this.firstRowIndex = firstRowIndex;
		this.lastRowIndex = lastRowIndex;
		this.firstColumnIndex = firstColumnIndex;
		this.lastColumnIndex = lastColumnIndex;
		this.horizontalAlignment = horizontalAlignment;
	}

	@Override
	public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
		CellRangeAddress cellRangeAddress =
				new CellRangeAddress(firstRowIndex, lastRowIndex, firstColumnIndex, lastColumnIndex);
		final Sheet sheet = writeSheetHolder.getSheet();
		sheet.addMergedRegionUnsafe(cellRangeAddress);
	}

	@Override
	public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
		final int columnIndex = cell.getColumnIndex();
		final int rowIndex = cell.getRowIndex();
		if (rowIndex == firstRowIndex && columnIndex == firstColumnIndex) {
			for (WriteCellData<?> writeCellData : cellDataList) {
				final WriteCellStyle writeCellStyle = writeCellData.getWriteCellStyle();
				writeCellStyle.setHorizontalAlignment(horizontalAlignment);
			}
		}
	}
}
