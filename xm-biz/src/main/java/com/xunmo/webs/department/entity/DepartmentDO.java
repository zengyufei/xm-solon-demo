package com.xunmo.webs.department.entity;

import com.xunmo.base.XmDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class DepartmentDO<T> extends XmDO<T> {

    private String shortName;
    private String name;
    private String code;
    private String type;
    private String description;
    private String isHide;
    private String isShow;
    private Integer level;

    private String parentCode;
    private Integer sort;

}
