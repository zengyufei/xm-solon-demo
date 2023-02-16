package com.xunmo.webs.department.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xunmo.base.relate.XmRelateCodeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_department")
@EqualsAndHashCode(callSuper = true)
public class Department extends XmRelateCodeEntity {

    private String shortName;
    private String name;
    private String type;
    private String description;
    private String isHide;
    private String isShow;
    private Integer level;

}
