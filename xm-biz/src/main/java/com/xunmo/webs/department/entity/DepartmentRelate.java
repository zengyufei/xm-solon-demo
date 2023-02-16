package com.xunmo.webs.department.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xunmo.base.XmTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门关系表
 * </p>
 *
 * @author zengyufei
 * @since 2022-09-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_department_relate")
@EqualsAndHashCode(callSuper = true)
public class DepartmentRelate extends XmTenantEntity {

    private static final long serialVersionUID = 1L;

    private String parentCode;
    private String childCode;
}
