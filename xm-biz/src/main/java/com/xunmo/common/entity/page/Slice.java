//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.common.entity.page;

import java.util.List;
import java.util.function.Function;

public interface Slice<T> extends Streamable<T> {

	Integer getNumber();

	Integer getSize();

	List<T> getContent();

	Sort getSort();

	boolean hasNext();

	boolean hasPrevious();

	default Pageable getPageable() {
		return PageRequest.of(this.getNumber(), this.getSize(), this.getSort());
	}

	<U> Slice<U> map(Function<? super T, ? extends U> converter);

}
