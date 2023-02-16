package com.xunmo.webs.department.dto;

import com.xunmo.webs.department.entity.Department;
import com.xunmo.webs.department.entity.DepartmentDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DepartmentGetPageDTO extends DepartmentDO<Department> {

}
