package com.ftkj.util.concurrent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class X3Collectors {
    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
    }

    @SuppressWarnings("unchecked")
    public final static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    public static <ID extends Number, T, K, U>
    MaxIdMapTuple<K, U> collector(List<T> list,
                                  Function<? super T, ID> maxIdMapper,
                                  Function<? super T, ? extends K> keyMapper) {
        return collector(list, maxIdMapper, keyMapper, castingIdentity());
    }

    public static <ID extends Number, T, K, U>
    MaxIdMapTuple<K, U> collector(List<T> list,
                                  Function<? super T, ID> maxIdMapper,
                                  Function<? super T, ? extends K> keyMapper,
                                  Function<? super T, ? extends U> valueMapper) {
        return collector(list, maxIdMapper, keyMapper, valueMapper, HashMap::new);
    }

    public static <ID extends Number, T, K, U, M extends Map<K, U>>
    MaxIdMapTuple<K, U> collector(List<? extends T> list,
                                  Function<? super T, ? extends ID> maxIdMapper,
                                  Function<? super T, ? extends K> keyMapper,
                                  Function<? super T, ? extends U> valueMapper,
                                  Supplier<M> mapSupplier) {
        Map<K, U> map = mapSupplier.get();
        long maxId = 0;
        for (T v : list) {
            ID id = maxIdMapper.apply(v);
            long lid = id.longValue();
            if (maxId < lid) {
                maxId = lid;
            }
            map.put(keyMapper.apply(v), valueMapper.apply(v));
        }
        return new MaxIdMapTuple<>(maxId, map);
    }

    public static final class MaxIdMapTuple<K, V> {
        private final int maxIdInt;
        private final long maxIdLong;
        private final Map<K, V> map;

        private MaxIdMapTuple(long maxIdLong, Map<K, V> map) {
            this.maxIdLong = maxIdLong;
            this.maxIdInt = (int) maxIdLong;
            this.map = map;
        }

        public int getMaxIdInt() {
            return maxIdInt;
        }

        public long getMaxIdLong() {
            return maxIdLong;
        }

        public Map<K, V> getMap() {
            return map;
        }
    }
}
