package com.xunmo.common;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface XmFunction<T, R> extends Function<T, R>, Serializable {

}
