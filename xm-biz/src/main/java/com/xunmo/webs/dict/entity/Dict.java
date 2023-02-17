package com.xunmo.webs.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xunmo.annotations.SortUni;
import com.xunmo.base.move.XmSimpleMoveEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_dictionaries")
@EqualsAndHashCode(callSuper = true)
public class Dict extends XmSimpleMoveEntity {

    private Integer isUse;

    /**
     * 字典编码
     */
    private String dicCode;
    /**
     * 字典名
     */
    private String dicDescription;
    /**
     * 字典值
     */
    private String dicValue;
    /**
     * 字典层级
     */
    private Integer dicLevel;

    @SortUni(isNull = true)
    private String parentId;

    private String parentName;

    private Integer valueType;

}
