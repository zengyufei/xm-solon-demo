package com.xunmo.webs.department.dto;

import com.xunmo.base.XmIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DepartmentGetDTO  extends XmIdEntity {

    private static final long serialVersionUID = -5516536408975170253L;
}
