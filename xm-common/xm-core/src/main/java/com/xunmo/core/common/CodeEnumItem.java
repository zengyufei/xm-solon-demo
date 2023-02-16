package com.xunmo.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeEnumItem<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private T code;
    private String description;
}
