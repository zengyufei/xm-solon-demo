//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.common.entity.page;

import cn.hutool.core.lang.Assert;

import java.util.Optional;

public interface Pageable {
    static Pageable unpaged() {
        return Unpaged.INSTANCE;
    }

    static Pageable ofSize(Integer pageSize) {
        return PageRequest.of(0, pageSize);
    }

    default boolean isPaged() {
        return true;
    }

    default boolean isUnpaged() {
        return !this.isPaged();
    }

    Integer getPageNumber();

    Integer getPageSize();

    long getOffset();

    Sort getSort();

    default Sort getSortOr(Sort sort) {
        Assert.notNull(sort, "Fallback Sort must not be null!");
        return this.getSort().isSorted() ? this.getSort() : sort;
    }

    Pageable next();

    Pageable previousOrFirst();

    Pageable first();

    Pageable withPage(Integer pageNumber);

    boolean hasPrevious();

    default Optional<Pageable> toOptional() {
        return this.isUnpaged() ? Optional.empty() : Optional.of(this);
    }
}
