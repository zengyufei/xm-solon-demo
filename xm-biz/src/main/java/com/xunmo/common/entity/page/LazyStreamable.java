//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xunmo.common.entity.page;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class LazyStreamable<T> implements Streamable<T> {
    private final Supplier<? extends Stream<T>> stream;

    private LazyStreamable(Supplier<? extends Stream<T>> stream) {
        this.stream = stream;
    }

    public static <T> LazyStreamable<T> of(Supplier<? extends Stream<T>> stream) {
        return new LazyStreamable(stream);
    }

    public Iterator<T> iterator() {
        return this.stream().iterator();
    }

    public Stream<T> stream() {
        return this.stream.get();
    }

    public Supplier<? extends Stream<T>> getStream() {
        return this.stream;
    }

    public String toString() {
        return "LazyStreamable(stream=" + this.getStream() + ")";
    }
}
