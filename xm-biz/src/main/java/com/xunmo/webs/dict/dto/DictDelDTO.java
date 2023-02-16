package com.xunmo.webs.dict.dto;

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
public class DictDelDTO  extends XmIdEntity {

    private static final long serialVersionUID = 5148414539771362379L;
}
