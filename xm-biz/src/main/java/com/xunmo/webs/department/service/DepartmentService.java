package com.xunmo.webs.department.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.base.move.XmSimpleMoveService;
import com.xunmo.base.move.XmSimpleMoveServiceImpl;
import com.xunmo.common.utils.XmUtil;
import com.xunmo.core.utils.LamUtil;
import com.xunmo.webs.department.dto.DepartmentGetPageDTO;
import com.xunmo.webs.department.dto.DepartmentUpdateDTO;
import com.xunmo.webs.department.entity.Department;
import com.xunmo.webs.department.entity.DepartmentRelate;
import com.xunmo.webs.department.mapper.DepartmentMapper;
import com.xunmo.webs.department.mapper.DepartmentRelateMapper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.data.annotation.Tran;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 部门
 */
@Service
public class DepartmentService extends XmSimpleMoveServiceImpl<DepartmentMapper, Department> implements XmSimpleMoveService<Department> {

    @Inject
    private DepartmentRelateService departmentRelateService;
    @Inject
    private DepartmentRelateMapper departmentRelateMapper;

    /**
     * 获取树
     *
     * @param department department
     * @return {@link List}<{@link Tree}<{@link String}>>
     */
    public List<Tree<String>> getTree(Department department) {
        final String id = department.getId();
        final String code = department.getCode();
        final String parentCode = department.getParentCode();

        List<Department> departments;
        if (StrUtil.isNotBlank(id)) {
            departments = this.baseMapper.getAllChildAndSelfById(id);
        } else if (StrUtil.isNotBlank(code)) {
            departments = this.baseMapper.getAllChildAndSelfByCode(code);
        } else {
            departments = this.baseMapper.getAllChildAndSelfByCode(parentCode);
        }

        // 配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 ，即返回列表里对象的字段名
        treeNodeConfig.setIdKey(LamUtil.getFieldName(Department::getId));
        treeNodeConfig.setParentIdKey(LamUtil.getFieldName(Department::getParentCode));
        treeNodeConfig.setWeightKey(LamUtil.getFieldName(Department::getSort));
        treeNodeConfig.setNameKey(LamUtil.getFieldName(Department::getName));
        treeNodeConfig.setChildrenKey("children");

        return TreeUtil.build(departments, "-1", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentCode());
            tree.setWeight(treeNode.getSort());
            tree.setName(treeNode.getName());
            // 扩展属性 ...
//            tree.putExtra("sectionId", treeNode.getSectionId());
            for (Field field : ReflectUtil.getFields(Department.class)) {
                final String fieldName = field.getName();
                final Object fieldValue = ReflectUtil.getFieldValue(treeNode, field);
                tree.putExtra(fieldName, fieldValue);
            }
        });
    }

    /**
     * 获取列表
     *
     * @param departmentGetPageDTO department获取页面dto
     * @return {@link List}<{@link Department}>
     */
    public List<Department> getList(DepartmentGetPageDTO departmentGetPageDTO) {
        final Department department = departmentGetPageDTO.toEntity();
        return XmUtil.startPage(() -> this.baseMapper.selectList(Wrappers.lambdaQuery(department)));
    }

    /**
     * 添加
     *
     * @param inputDepartment department
     * @return {@link Department}
     */
    @Tran
    public Department add(Department inputDepartment) throws Exception {
        final String inputCode = inputDepartment.getCode();
        final Long existsCode = this.baseMapper.selectCount(Wrappers.<Department>lambdaQuery()
                .eq(Department::getCode, inputCode));
        XmUtil.throwParamError(existsCode > 0, () -> "code 已经被占用, 请重新填写一个");

        final String rootName = "root";
        final String inputParentCode = StrUtil.blankToDefault(inputDepartment.getParentCode(), rootName);
        if (StrUtil.equals(rootName, inputParentCode)) {
            inputDepartment.setLevel(1);
        } else {
            final Department parentDepartment = baseMapper.checkAndGetByCode(inputParentCode, () -> "上级部门不存在!");
            Integer parentLevel = parentDepartment.getLevel();
            inputDepartment.setLevel(++parentLevel);
        }

        inputDepartment.setParentCode(inputParentCode);
        int max = this.baseMapper.getMax(inputDepartment);
        inputDepartment.setSort(max + 1);
        this.baseMapper.insert(inputDepartment);
        this.departmentRelateService.insertDeptRelation(inputDepartment);
        return inputDepartment;
    }

    /**
     * 删除
     *
     * @param id id
     * @return boolean
     */
    @Tran
    public boolean del(String id) {
        final Department department = this.baseMapper.checkAndGet(id);
        final String code = department.getCode();
        // 删除下级部门
        List<String> codeList = this.departmentRelateService
                .list(Wrappers.<DepartmentRelate>query().lambda().eq(DepartmentRelate::getParentCode, code)).stream()
                .map(DepartmentRelate::getChildCode).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(codeList)) {
            this.baseMapper.removeByCodes(codeList);
        }
        // 删除部门级联关系
        this.departmentRelateService.deleteAllDeptRealtion(code);
        // 最后删除自己
        this.baseMapper.deleteById(id);
        return true;
    }

    /**
     * 更新
     *
     * @param departmentUpdateDTO department更新dto
     * @return boolean
     */
    @Tran
    public boolean updateBean(DepartmentUpdateDTO departmentUpdateDTO) throws Exception {
        final Department inputDepartment = toBean(departmentUpdateDTO, Department.class);
        final String id = inputDepartment.getId();
        final String inputCode = inputDepartment.getCode();

        final Department oldDepartment = checkAndGet(id);
        final String oldCode = oldDepartment.getCode();
        final String oldParentCode = oldDepartment.getParentCode();

        final String rootName = "root";
        final String inputParentCode = StrUtil.blankToDefault(inputDepartment.getParentCode(), rootName);

        // 是否发生code变化
        boolean isChangeCode = false;
        if (StrUtil.isNotBlank(inputCode) && !StrUtil.equalsIgnoreCase(inputCode, oldCode)) {
            isChangeCode = true;
        }

        // 是否发生上级变化
        boolean isChangeParentCode = false;
        if (StrUtil.isNotBlank(inputParentCode) && !StrUtil.equalsIgnoreCase(inputParentCode, oldParentCode)) {
            isChangeParentCode = true;
        }

        if (isChangeCode) {
            final Long existsCode = this.baseMapper.selectCount(Wrappers.<Department>lambdaQuery()
                    .eq(Department::getCode, inputCode));
            XmUtil.throwParamError(existsCode > 0, () -> "code 已经被占用, 请重新填写一个");
        }

        if (isChangeCode && isChangeParentCode) {
            // 删除旧的，本级及上级关系
            this.departmentRelateMapper.delete(Wrappers.<DepartmentRelate>lambdaQuery()
                    .eq(DepartmentRelate::getChildCode, oldCode));
            // 更新旧的，下级关系
            final DepartmentRelate updateChild = new DepartmentRelate();
            updateChild.setParentCode(inputCode);
            this.departmentRelateMapper.update(updateChild, Wrappers.<DepartmentRelate>lambdaUpdate()
                    .eq(DepartmentRelate::getParentCode, oldCode));
            // 新增自己
            final DepartmentRelate insertSelf = new DepartmentRelate();
            insertSelf.setChildCode(inputCode);
            insertSelf.setParentCode(inputCode);
            this.departmentRelateMapper.insert(insertSelf);
            // 新增新上级部门关系
            final List<DepartmentRelate> parentDepartmentRelates = this.departmentRelateMapper.selectList(Wrappers.<DepartmentRelate>lambdaQuery()
                    .eq(DepartmentRelate::getChildCode, inputParentCode));
            for (DepartmentRelate departmentRelate : parentDepartmentRelates) {
                departmentRelate.setId(null);
                departmentRelate.setChildCode(inputCode);
                this.departmentRelateMapper.insert(departmentRelate);
            }
            // 刷新部门表
            final Department updateDept = new Department();
            updateDept.setParentCode(inputCode);
            this.baseMapper.update(updateDept, Wrappers.<Department>lambdaUpdate()
                    .eq(Department::getParentCode, oldCode));
        } else if (isChangeCode) {
            // 更新自己的下级，包括自己
            final DepartmentRelate newEntity = new DepartmentRelate();
            newEntity.setChildCode(inputCode);
            this.departmentRelateMapper.update(newEntity, Wrappers.<DepartmentRelate>lambdaUpdate()
                    .eq(DepartmentRelate::getChildCode, oldCode));
            // 更新自己的上级，包括自己
            final DepartmentRelate newEntity2 = new DepartmentRelate();
            newEntity2.setParentCode(inputCode);
            this.departmentRelateMapper.update(newEntity2, Wrappers.<DepartmentRelate>lambdaUpdate()
                    .eq(DepartmentRelate::getParentCode, oldCode));
            // 刷新部门表
            final Department updateDept = new Department();
            updateDept.setParentCode(inputCode);
            this.baseMapper.update(updateDept, Wrappers.<Department>lambdaUpdate()
                    .eq(Department::getParentCode, oldCode));
        } else {
            // 设置排序值
            int max = this.baseMapper.getMax(inputDepartment);
            inputDepartment.setSort(max + 1);
            // 设置 level
            if (StrUtil.equals(rootName, inputParentCode)) {
                inputDepartment.setLevel(1);
            } else {
                final Department parentDepartment = this.baseMapper.checkAndGetByCode(inputParentCode);
                Integer parentLevel = parentDepartment.getLevel();
                inputDepartment.setLevel(++parentLevel);
            }
            // 父子关系闭包表处理
            // 删除旧的， 本级及上级
            this.departmentRelateMapper.delete(Wrappers.<DepartmentRelate>lambdaQuery()
                    .eq(DepartmentRelate::getChildCode, oldCode));
            // 新增自己
            final DepartmentRelate insertSelf = new DepartmentRelate();
            insertSelf.setChildCode(inputCode);
            insertSelf.setParentCode(inputCode);
            this.departmentRelateMapper.insert(insertSelf);
            // 新增新的，本级及上级关系
            // 先查出新上级的关系链，再拿下来改一下code
            final List<DepartmentRelate> parentDepartmentRelates = this.departmentRelateMapper.selectList(Wrappers.<DepartmentRelate>lambdaQuery()
                    .eq(DepartmentRelate::getChildCode, inputParentCode));
            for (DepartmentRelate departmentRelate : parentDepartmentRelates) {
                departmentRelate.setId(null);
                departmentRelate.setChildCode(inputCode);
                this.departmentRelateMapper.insert(departmentRelate);
            }
        }

        // 更新部门状态
        final int i = this.baseMapper.updateNotNullById(inputDepartment);
        if (isChangeCode && i > 0) {
            // TODO 编码变更逻辑
        }
        return true;
    }


}
