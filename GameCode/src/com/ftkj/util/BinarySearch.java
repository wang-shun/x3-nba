package com.ftkj.util;

import com.ftkj.util.tuple.Tuple2Int;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展的二分查找
 *
 * @author luch
 */
public class BinarySearch {

    /** 比较用来排序的两个参数。根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。 */
    public interface Comparator<T, K> {
        /**
         * 比较用来排序的两个参数。根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。
         * 在前面的描述中，符号 sgn(expression) 表示 signum 数学函数，
         * 根据 expression 的值为负数、0 还是正数，该函数分别返回 -1、0 或 1。
         *
         * @param o   要比较的第一个对象
         * @param key 要比较的第二个对象
         * @return 根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。
         */
        int compare(T o, K key);
    }

    /** @param idx 为 {@link #binarySearch}的返回值 */
    public static <T> T getByIdx(List<? extends T> list, int idx) {
        if (idx < 0 || idx >= list.size()) {
            int insertIdx = -(idx + 1);
            if (insertIdx < 0) {
                return list.get(0);
            }
            if (insertIdx >= list.size()) {
                return list.get(list.size() - 1);
            }
            return list.get(insertIdx);
        }
        return list.get(idx);
    }

    /**
     * 二分查找
     * <pre>{@code
     * List<Tuple2Int> list = new ArrayList<>();
     * list.add(Tuple2Int.create(1, 1));
     * list.add(Tuple2Int.create(2, 3));
     * list.add(Tuple2Int.create(3, 5));
     *
     * int idx = binarySearch(list, tuple_v2, (tp, key) -> Integer.compare(tp._2(), key));
     * int insertIdx = idx < 0 ? -(idx + 1) : idx;
     *
     * print:
     * key 0 => idx -1 insertIdx 0
     * key 1 => idx 0 insertIdx 0
     * key 2 => idx -2 insertIdx 1
     * key 4 => idx -3 insertIdx 2
     * key 5 => idx 2 insertIdx 2
     * key 6 => idx -4 insertIdx 3
     * }</pre>
     *
     * @param c [E, K]
     * @return 如果搜索键包含在列表中，则返回搜索键的索引；
     * 否则返回 (-(插入点) - 1)。
     * 插入点 被定义为将键插入列表的那一点：即第一个大于此键的元素索引；
     * 如果列表中的所有元素都小于指定的键，则为 list.size()。
     * 注意，这保证了当且仅当此键被找到时，返回的值将 >= 0。
     */
    public static <T, K>
    int binarySearch(List<? extends T> list, final K key, Comparator<? super T, K> c) {
        return indexedBinarySearch(list, key, c);
    }

    /** 二分查找 {@link java.util.Collections#binarySearch(List, Object, java.util.Comparator)} */
    private static <T, K> int indexedBinarySearch(List<? extends T> list,
                                                  final K key,
                                                  Comparator<? super T, K> c) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = list.get(mid);
            int cmp = c.compare(midVal, key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found
    }

    public static void main(String[] args) {
        List<Tuple2Int> list = new ArrayList<>();
        list.add(Tuple2Int.create(1, 1));//tuple[id, key]
        list.add(Tuple2Int.create(2, 3));//tuple[id, key]
        list.add(Tuple2Int.create(3, 5));//tuple[id, key]
        Comparator<Tuple2Int, Integer> asc = (tp, key) -> Integer.compare(tp._2(), key);
        System.out.println("asc");
        findAndPrint(list, 0, asc);
        findAndPrint(list, 1, asc);
        findAndPrint(list, 2, asc);
        findAndPrint(list, 4, asc);
        findAndPrint(list, 5, asc);
        findAndPrint(list, 6, asc);

        list = new ArrayList<>();
        list.add(Tuple2Int.create(1, 5));
        list.add(Tuple2Int.create(2, 3));
        list.add(Tuple2Int.create(3, 1));
        Comparator<Tuple2Int, Integer> desc = (tp, key) -> Integer.compare(key, tp._2());
        System.out.println("desc");
        findAndPrint(list, 0, desc);
        findAndPrint(list, 1, desc);
        findAndPrint(list, 2, desc);
        findAndPrint(list, 4, desc);
        findAndPrint(list, 5, desc);
        findAndPrint(list, 6, desc);
    }

    private static void findAndPrint(List<Tuple2Int> list, int tuple_v2, Comparator<Tuple2Int, Integer> c) {
        int idx = binarySearch(list, tuple_v2, c);
        int insertIdx = idx < 0 ? -(idx + 1) : idx;
        System.out.println("key " + tuple_v2 + " => idx " + idx + " insertIdx " + insertIdx);
    }
}
