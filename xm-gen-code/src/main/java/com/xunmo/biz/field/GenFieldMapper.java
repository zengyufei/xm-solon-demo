package com.xunmo.biz.field;

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
public interface GenFieldMapper  extends XmMapper<GenField> {

	void addGenField(@Param("fields") List<GenField> fields);
	void delGenField(Long genTableId);
	void updateGenField(GenField genField);
	long countGenField(Map<String, Object> map);
	List<GenField> getGenField(Map<String, Object> map);

}
