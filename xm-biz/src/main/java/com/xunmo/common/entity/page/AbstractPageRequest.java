//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.common.entity.page;

import java.io.Serializable;

public abstract class AbstractPageRequest implements Pageable, Serializable {
    private static final long serialVersionUID = 1232825578694716871L;
    private Integer page;
    private Integer size;

    public AbstractPageRequest() {
    }

    public AbstractPageRequest(Integer page, Integer size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        } else if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        } else {
            this.page = page;
            this.size = size;
        }
    }

    public Integer getPageSize() {
        return this.size;
    }

    public Integer getPageNumber() {
        return this.page;
    }

    public long getOffset() {
        return (long) this.page * (long) this.size;
    }

    public boolean hasPrevious() {
        return this.page > 0;
    }

    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    public abstract Pageable next();

    public abstract Pageable previous();

    public abstract Pageable first();

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.page;
        result = 31 * result + this.size;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            AbstractPageRequest other = (AbstractPageRequest) obj;
            return this.page == other.page && this.size == other.size;
        } else {
            return false;
        }
    }
}
