package com.xunmo.jimmer.page;

enum Unpaged implements Pageable {

	INSTANCE;

	Unpaged() {
	}

	@Override
	public boolean isPaged() {
		return false;
	}

	public Pageable previousOrFirst() {
		return this;
	}

	public Pageable next() {
		return this;
	}

	public boolean hasPrevious() {
		return false;
	}

	public Sort getSort() {
		return Sort.unsorted();
	}

	public Integer getPageSize() {
		throw new UnsupportedOperationException();
	}

	public Integer getPageNumber() {
		throw new UnsupportedOperationException();
	}

	public long getOffset() {
		throw new UnsupportedOperationException();
	}

	public Pageable first() {
		return this;
	}

	public Pageable withPage(Integer pageNumber) {
		if (pageNumber != null && pageNumber == 0) {
			return this;
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

}
