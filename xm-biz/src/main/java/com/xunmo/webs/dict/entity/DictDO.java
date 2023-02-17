package com.xunmo.webs.dict.entity;

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
public abstract class DictDO<T> extends XmDO<T> {

    private Integer isUse;
    private String dicCode;
    private String dicDescription;
    private String dicValue;
    private Integer dicLevel;
    private Integer valueType;

    private String parentId;
    private String parentName;
    private Integer sort;

}
