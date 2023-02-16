package com.xunmo.base.relate;

import com.xunmo.annotations.SortUni;
import com.xunmo.base.move.XmSimpleMoveEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class XmRelateCodeEntity extends XmSimpleMoveEntity implements Serializable {

    private static final long serialVersionUID = 8314588241042936202L;
    private String code;
    @SortUni
    private String parentCode;
}
