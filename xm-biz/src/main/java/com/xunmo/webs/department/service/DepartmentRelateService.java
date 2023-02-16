package com.xunmo.webs.department.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.base.XmService;
import com.xunmo.base.XmServiceImpl;
import com.xunmo.webs.department.entity.Department;
import com.xunmo.webs.department.entity.DepartmentRelate;
import com.xunmo.webs.department.mapper.DepartmentRelateMapper;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.data.annotation.Tran;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门关系表
 */
@Slf4j
@Service
public class DepartmentRelateService extends XmServiceImpl<DepartmentRelateMapper, DepartmentRelate> implements XmService<DepartmentRelate> {

    /**
     * 维护部门关系
     * @param department 部门
     */
    @Tran
    public void insertDeptRelation(Department department) {
        // 增加部门关系表
        List<DepartmentRelate> relationList = this.baseMapper.selectList(
                Wrappers.<DepartmentRelate>query().lambda().eq(DepartmentRelate::getChildCode, department.getParentCode()))
                .stream().peek(relation -> {
                    relation.setId(null);
                    relation.setChildCode(department.getCode());
                }).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(relationList)) {
            relationList.forEach(ele -> this.baseMapper.insert(ele));
        }

        // 自己也要维护到关系表中
        DepartmentRelate own = new DepartmentRelate();
        own.setChildCode(department.getCode());
        own.setParentCode(department.getCode());
        this.baseMapper.insert(own);
    }

    /**
     * 通过ID删除部门关系
     * @param code
     */
    public void deleteAllDeptRealtion(String code) {
        this.baseMapper.deleteDeptRelationsByDeptId(code);
    }

    /**
     * 更新部门关系
     * @param relation
     */
    @Tran
    public void updateDeptRealtion(DepartmentRelate relation) {
        this.baseMapper.deleteDeptRelations(relation);
        List<DepartmentRelate> list = this.baseMapper.getDeptRelations(relation);
        for (DepartmentRelate departmentRelate : list) {
            this.baseMapper.insert(departmentRelate);
        }
    }



}
