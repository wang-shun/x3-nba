package com.ftkj.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ListsUtil {

    public static <K, V, E, U> List<U> getByKeys(Map<K, V> src, Collection<E> keys, Function<E, K> keyMapper, BiFunction<E, V, U> valueMapper) {
        if (src == null || src.isEmpty()) {
            return Collections.emptyList();
        }
        List<U> ret = new ArrayList<>();
        for (E k : keys) {
            V v = src.get(keyMapper.apply(k));
            ret.add(valueMapper.apply(k, v));
        }
        return ret;
    }

    public static <E> E last(List<? extends E> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <E> void fillNull(List<E> list, int maxSize) {
        if (list.size() <= maxSize) {
            return;
        }
        for (int i = list.size(); i < maxSize; i++) {
            list.add(null);
        }
    }

    /**
     * Checks that {@code fromIndex} and {@code toIndex} are in
     * the range and return false if they aren't.
     */
    public static boolean rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        return fromIndex <= toIndex && fromIndex >= 0 && toIndex <= arrayLength;
    }

    public static boolean rangeCheck(List<?> list, int index) {
        return index >= 0 && index < list.size();
    }

    public static boolean rangeCheck(List<?> list, int index1, int index2) {
        return index1 >= 0 && index1 < list.size() &&
                index2 >= 0 && index2 < list.size();
    }

    public static boolean rangeCheck(int size, int index) {
        return index >= 0 && index < size;
    }

    public static <T> T get(List<T> list, int idx) {
        if (rangeCheck(list, idx)) {
            return list.get(idx);
        }
        return null;
    }

}
