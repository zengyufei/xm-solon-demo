package com.xunmo.webs.dict.dto;

import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.entity.DictDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DictUpdateDTO extends DictDO<Dict> {

    @NotBlank(value = "id 不能为空")
    @NotNull(value = "id 不能为null")
    private String id;
}
