package com.xunmo.webs.dict.dto;

import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.entity.DictDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DictSaveDTO extends DictDO<Dict> {
}
