//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.jimmer.page;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class PageImpl<T> extends Chunk<T> implements Page<T> {

	private static final long serialVersionUID = 867755909294344406L;

	private final long total;

	public PageImpl(List<T> content, Pageable pageable, long total) {
		super(content, pageable);
		this.total = pageable.toOptional()
			.filter((it) -> !content.isEmpty())
			.filter((it) -> it.getOffset() + (long) it.getPageSize() > total)
			.map((it) -> it.getOffset() + (long) content.size())
			.orElse(total);
	}

	public PageImpl(List<T> content) {
		this(content, Pageable.unpaged(), null == content ? 0L : (long) content.size());
	}

	public int getTotalPages() {
		return this.getSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) this.getSize());
	}

	public long getTotalElements() {
		return this.total;
	}

	public boolean hasNext() {
		return this.getNumber() + 1 < this.getTotalPages();
	}

	public boolean isLast() {
		return !this.hasNext();
	}

	public <U> Page<U> map(Function<? super T, ? extends U> converter) {
		return new PageImpl(this.getConvertedContent(converter), this.getPageable(), this.total);
	}

	public String toString() {
		String contentType = "UNKNOWN";
		List<T> content = this.getContent();
		if (!content.isEmpty() && content.get(0) != null) {
			contentType = content.get(0).getClass().getName();
		}

		return String.format("Page %s of %d containing %s instances", this.getNumber() + 1, this.getTotalPages(),
				contentType);
	}

	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		else if (!(obj instanceof PageImpl)) {
			return false;
		}
		else {
			PageImpl<?> that = (PageImpl) obj;
			return this.total == that.total && super.equals(obj);
		}
	}

	public int hashCode() {
		int result = 17;
		result += 31 * (int) (this.total ^ this.total >>> 32);
		result += 31 * super.hashCode();
		return result;
	}

}
