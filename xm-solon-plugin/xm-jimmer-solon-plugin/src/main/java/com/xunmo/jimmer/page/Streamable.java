/*
 * Copyright 2016-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xunmo.jimmer.page;

import cn.hutool.core.lang.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface Streamable<T> extends Iterable<T>, Supplier<Stream<T>> {

	@SafeVarargs
	static <T> Streamable<T> of(T... t) {
		return () -> Arrays.asList(t).iterator();
	}

	static <T> Streamable<T> of(Iterable<T> iterable) {

		Assert.notNull(iterable, "Iterable must not be null!");

		return iterable::iterator;
	}

	static <T> Streamable<T> of(Supplier<? extends Stream<T>> supplier) {
		return LazyStreamable.of(supplier);
	}

	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default <R> Streamable<R> map(Function<? super T, ? extends R> mapper) {

		Assert.notNull(mapper, "Mapping function must not be null!");

		return Streamable.of(() -> stream().map(mapper));
	}

	default Streamable<T> filter(Predicate<? super T> predicate) {

		Assert.notNull(predicate, "Filter predicate must not be null!");

		return Streamable.of(() -> stream().filter(predicate));
	}

	default boolean isEmpty() {
		return !iterator().hasNext();
	}

	default Streamable<T> and(Supplier<? extends Stream<? extends T>> stream) {

		Assert.notNull(stream, "Stream must not be null!");

		return Streamable.of(() -> Stream.concat(this.stream(), stream.get()));
	}

	@SuppressWarnings("unchecked")
	default Streamable<T> and(T... others) {

		Assert.notNull(others, "Other values must not be null!");

		return Streamable.of(() -> Stream.concat(this.stream(), Arrays.stream(others)));
	}

	default Streamable<T> and(Iterable<? extends T> iterable) {

		Assert.notNull(iterable, "Iterable must not be null!");

		return Streamable.of(() -> Stream.concat(this.stream(), StreamSupport.stream(iterable.spliterator(), false)));
	}

	default Streamable<T> and(Streamable<? extends T> streamable) {
		return and((Supplier<? extends Stream<? extends T>>) streamable);
	}

	/**
	 * Creates a new, unmodifiable {@link List}.
	 * @return will never be {@literal null}.
	 * @since 2.2
	 */
	default List<T> toList() {
		return stream().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	default Stream<T> get() {
		return stream();
	}

}
