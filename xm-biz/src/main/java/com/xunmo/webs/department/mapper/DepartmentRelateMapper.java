package com.xunmo.webs.department.mapper;


import com.xunmo.base.XmMapper;
import com.xunmo.webs.department.entity.DepartmentRelate;

import java.util.List;

/**
 * 部门关系
 */
public interface DepartmentRelateMapper extends XmMapper<DepartmentRelate> {

    /**
     * 删除部门 > 删除所有关联此部门子节点的闭包关系
     *
     * @param code 部门ID
     */
    void deleteDeptRelationsByDeptId(String code);

    /**
     * 删除节点数据
     *
     * @param departmentRelate 关系节点
     */
    void deleteDeptRelations(DepartmentRelate departmentRelate);

    /**
     * 节点数据
     *
     * @param departmentRelate 关系节点
     */
    List<DepartmentRelate> getDeptRelations(DepartmentRelate departmentRelate);

    /**
     * 新增节点数据
     *
     * @param departmentRelate 关系节点
     */
    //void insertDeptRelations(DepartmentRelate departmentRelate);
}
