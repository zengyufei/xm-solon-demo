package com.xunmo.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.xunmo.base.validationGroups.DelValid;
import com.xunmo.base.validationGroups.GetValid;
import com.xunmo.base.validationGroups.IdValid;
import com.xunmo.base.validationGroups.MoveValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class XmIdEntity implements Serializable {
    private static final long serialVersionUID = 4034094991113845459L;
    @TableId
    @NotBlank(value = "id 不能为空", groups = {MoveValid.class, IdValid.class, GetValid.class, DelValid.class})
    @NotNull(value = "id 不能为null", groups = {MoveValid.class, IdValid.class, GetValid.class, DelValid.class})
    private String id;

}
