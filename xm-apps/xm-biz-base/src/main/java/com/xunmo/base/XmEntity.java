package com.xunmo.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class XmEntity extends XmTenantEntity implements Serializable {

    private static final long serialVersionUID = 3310210074346282868L;

    @TableField(fill = FieldFill.INSERT)
    private int disabled;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT)
    private String createUser;
    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    @TableField(fill = FieldFill.UPDATE)
    private Date lastUpdateTime;
    @TableField(fill = FieldFill.UPDATE)
    private String lastUpdateUser;
    @TableField(fill = FieldFill.UPDATE)
    private String lastUpdateUserName;

    /**
     * 来源
     */
    @TableField(fill = FieldFill.INSERT)
    private String sourceType;
    /**
     * 备注
     */
    private String remark;
}
