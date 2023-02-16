package com.xunmo.base.move;

import com.xunmo.base.XmEntity;
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
public class XmSimpleMoveEntity extends XmEntity implements Serializable {

    private static final long serialVersionUID = 4124332115975669078L;

    private Integer sort;
}
