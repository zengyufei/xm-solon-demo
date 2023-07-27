package com.xunmo.function;

import java.util.Objects;

@FunctionalInterface
public interface XmBiConsumer<T, U> {

	void accept(T var1, U var2) throws Exception;

	default XmBiConsumer<T, U> andThen(XmBiConsumer<? super T, ? super U> var1) throws Exception {
		Objects.requireNonNull(var1);
		return (var2, var3) -> {
			this.accept(var2, var3);
			var1.accept(var2, var3);
		};
	}

}
