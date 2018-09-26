package com.ftkj.console;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 *
 */
public abstract class AbstractConsole {

    protected static BeanException exception(String msg, Object... args) {
        throw new BeanException(String.format(msg, args));
    }

    protected static BeanException exception(String msg) {
        throw new BeanException(msg);
    }

    protected static <T, K, V> ImmutableMap<K, V> toMap(
            List<? extends T> els,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction) {
        return els.stream().collect(toImmutableMap(keyFunction, valueFunction));
    }

    protected static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction) {
        return ImmutableMap.toImmutableMap(keyFunction, valueFunction);
    }

    public static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
        return ImmutableList.toImmutableList();
    }

}
