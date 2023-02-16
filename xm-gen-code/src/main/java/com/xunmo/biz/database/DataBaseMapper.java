package com.xunmo.biz.database;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper: 系统角色表
 * @author kong
 */
@Mapper
public interface DataBaseMapper {

	List<Database> queryAllDatabases();
	void addDatabase(Database database);
	Database getDatabaseById(Long databaseId);
	void delDatabase(Long id);
	void updateDatabase(Database database);
}
