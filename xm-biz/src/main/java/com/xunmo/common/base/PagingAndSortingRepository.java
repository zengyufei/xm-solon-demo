//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.common.base;

import com.xunmo.common.entity.page.Page;
import com.xunmo.common.entity.page.Pageable;
import com.xunmo.common.entity.page.Sort;

public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {

	Iterable<T> findAll(Sort sort);

	Page<T> findAll(Pageable pageable);

}
