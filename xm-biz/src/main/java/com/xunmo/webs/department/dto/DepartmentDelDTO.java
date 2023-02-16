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
public class DepartmentDelDTO  extends XmIdEntity {

    private static final long serialVersionUID = 2866205820470531523L;
}
