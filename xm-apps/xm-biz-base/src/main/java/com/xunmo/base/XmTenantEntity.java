package com.xunmo.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class XmTenantEntity extends XmIdEntity {

    private static final long serialVersionUID = 3248891901706251002L;
    @TableField(fill = FieldFill.INSERT)
    private String appId;
    @TableField(fill = FieldFill.INSERT)
    private String tenantId;

}
