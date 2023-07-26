//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.jimmer;

import com.xunmo.jimmer.page.Page;
import com.xunmo.jimmer.page.Pageable;
import com.xunmo.jimmer.page.Sort;

public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {

	Iterable<T> findAll(Sort sort);

	Page<T> findAll(Pageable pageable);

}
