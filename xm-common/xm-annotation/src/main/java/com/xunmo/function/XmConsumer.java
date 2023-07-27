//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.function;

import java.util.Objects;

@FunctionalInterface
public interface XmConsumer<T> {

	void accept(T var1) throws Exception;

	default XmConsumer<T> andThen(XmConsumer<? super T> var1) throws Exception {
		Objects.requireNonNull(var1);
		return (var2) -> {
			this.accept(var2);
			var1.accept(var2);
		};
	}

}
