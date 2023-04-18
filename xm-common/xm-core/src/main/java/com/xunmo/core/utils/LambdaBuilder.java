package com.xunmo.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaBuilder<T> {

    /**
     * 存储调用方 指定构造类的 构造器
     */
    private final Supplier<T> constructor;
    /**
     * 存储 指定类 所有需要初始化的类属性
     */
    private final List<Consumer<T>> dInjects = new ArrayList<>();

    private Consumer<T> head = t -> {};

    // V2: 构造函数私有化
    private LambdaBuilder(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> LambdaBuilder<T> builder(Supplier<T> constructor) {
        return new LambdaBuilder<>(constructor);
    }

    public <P1> LambdaBuilder<T> with(DInjectConsumer<T, P1> consumer, P1 p1) {
        Consumer<T> c = instance -> consumer.accept(instance, p1);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }

    public <P1> LambdaBuilder<T> with(DInjectConsumer<T, P1> consumer, P1 p1, Predicate<P1> predicate) {
        if (null != predicate && !predicate.test(p1)) {
            throw new RuntimeException(String.format("【%s】参数不符合通用业务规则！", p1));
        }
        Consumer<T> c = instance -> consumer.accept(instance, p1);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }


    public <P1, P2> LambdaBuilder<T> with(DInjectConsumer2<T, P1, P2> consumer, P1 p1, P2 p2) {
        Consumer<T> c = instance -> consumer.accept(instance, p1, p2);
//        dInjects.add(c);
        head = head.andThen(c);
        return this;
    }

    public T build() {
        // 调用supplier 生成类实例
        T instance = constructor.get();
        // 调用传入的setter方法，完成属性初始化
//        dInjects.forEach(dInject -> dInject.accept(instance));
        head.accept(instance);
        // 返回 建造完成的类实例
        return instance;
    }

    @FunctionalInterface
    public interface DInjectConsumer<T, P1> {
        void accept(T t, P1 p1);
    }

    @FunctionalInterface
    public interface DInjectConsumer2<T, P1, P2> {
        void accept(T t, P1 p1, P2 p2);
    }

}
