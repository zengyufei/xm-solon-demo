package com.xunmo.second.webs.user.dto;

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
public class UserGetDTO  extends XmIdEntity {

    private static final long serialVersionUID = -3337912534280145923L;
}
