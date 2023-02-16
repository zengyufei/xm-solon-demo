package com.xunmo.webs.department;

import cn.hutool.json.JSONUtil;
import com.xunmo.BizApp;
import com.xunmo.core.utils.XmMap;
import com.xunmo.webs.department.entity.Department;
import com.xunmo.webs.department.mapper.DepartmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BizApp.class)
public class TestDepartmentMapper extends HttpTestBase {

    @Inject
    private DepartmentMapper departmentMapper;

    @Test
    public void testList() throws Exception {
        final List<Department> departments = departmentMapper.getAllParentNotSelfById("1583289053387669505");
        System.out.println(JSONUtil.toJsonPrettyStr(departments));
    }

    @Test
    public void testDel() throws Exception {
        final XmMap<String, Object> xmMap = new XmMap<>();
        xmMap.put(Department::getId, 123);
        departmentMapper.deleteByMap(xmMap);
    }



    @Test
    public void testAll() throws Exception {

        {
            final Department department = this.departmentMapper.getParentByChildId("1583288786133266433");
            System.out.println(department.getName());
            System.out.println("---------------- getDirectChildById ---------------");
        }

        { // 获取直接下级，单一级
            final List<Department> departments = this.departmentMapper.getDirectChildById("1583288786133266433");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getDirectChildById ---------------");
        }
        { // 获取直接下级，单一级
            final List<Department> departments = this.departmentMapper.getDirectChildByCode("TEST");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getDirectChildByCode ---------------");
        }
        { // 获取所有上级，不包含自己
            final List<Department> departments = this.departmentMapper.getAllParentNotSelfById("1583289053387669505");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllParentNotSelfById ---------------");
        }
        { // 获取所有上级，不包含自己
            final List<Department> departments = this.departmentMapper.getAllParentNotSelfByCode("fd1");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllParentNotSelfByCode ---------------");
        }
        { // 获取所有上级，包含自己
            final List<Department> departments = this.departmentMapper.getAllParentAndSelfById("1583288786133266433");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllParentAndSelfById ---------------");
        }
        { // 获取所有上级，包含自己
            final List<Department> departments = this.departmentMapper.getAllParentAndSelfByCode("TEST");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllParentAndSelfByCode ---------------");
        }
        { // 获取所有下级，不包含自己
            final List<Department> departments = this.departmentMapper.getAllChildNotSelfById("1583288786133266433");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildNotSelfById ---------------");
        }
        { // 获取所有下级，不包含自己
            final List<Department> departments = this.departmentMapper.getAllChildNotSelfByCode("TEST");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildNotSelfByCode ---------------");
        }
        { // 获取所有下级，包含自己
            final List<Department> departments = this.departmentMapper.getAllChildAndSelfById("1583288786133266433");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildAndSelfById ---------------");
        }
        { // 获取所有下级，包含自己
            final List<Department> departments = this.departmentMapper.getAllChildAndSelfByCode("TEST");
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildAndSelfByCode ---------------");
        }
        { // 获取所有下级，不包括自己，多个
            final List<Department> departments = this.departmentMapper.getAllChildNotSelfByIds(Arrays.asList("1583288786133266433", "1583291019769659394"));
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildNotSelfByIds ---------------");
        }
        {// 获取所有下级，不包括自己，多个
            final List<Department> departments = this.departmentMapper.getAllChildNotSelfByCodes(Arrays.asList("TEST", "DL_HL"));
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildNotSelfByCodes ---------------");
        }
        { // 获取所有下级，包括自己，多个
            final List<Department> departments = this.departmentMapper.getAllChildAndSelfByIds(Arrays.asList("1583288786133266433", "1583291019769659394"));
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildAndSelfByIds ---------------");
        }
        {// 获取所有下级，包括自己，多个
            final List<Department> departments = this.departmentMapper.getAllChildAndSelfByCodes(Arrays.asList("TEST", "DL_HL"));
            departments.stream().map(Department::getName).forEach(System.out::println);
            System.out.println("---------------- getAllChildAndSelfByCodes ---------------");
        }
    }



}
