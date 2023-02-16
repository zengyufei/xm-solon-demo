package com.xunmo.biz.table;

import com.xunmo.base.XmMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Mapper: 系统角色表
 * @author kong
 */
@Mapper
public interface GenTableMapper  extends XmMapper<GenTable> {


	/**
	 * @return
	 */
	long countAllTables();

	/**
	 * @return
	 */
	List<TableInfo> queryAllTables(Map<String, Object> map);

	/**
	 * @return
	 */
	TableInfo queryTable(String tableName);

	List<Map<String, Object>> getAllFieldByTableName(String tableName);

	void addGenTable(@Param("tables") List<GenTable> tables);
	void delGenTable(Long id);
	void updateGenTable(GenTable genTable);
	GenTable getGenTableById(Long genTableId);
	long countGenTable(Map<String, Object> map);
	List<GenTable> getGenTable(Map<String, Object> map);

}
