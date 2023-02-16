package com.xunmo.base.move;

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
public class XmChangeSortEntity implements Serializable {
    private static final long serialVersionUID = 585078164444030545L;

    @NotBlank(value = "preId 不能为空")
    @NotNull(value = "preId 不能为null")
    private String preId;

    @NotBlank(value = "nextId 不能为空")
    @NotNull(value = "nextId 不能为null")
    private String nextId;

}
