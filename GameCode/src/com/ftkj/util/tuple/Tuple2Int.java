package com.ftkj.util.tuple;

import java.io.Serializable;

/** @author luch */
public class Tuple2Int implements Serializable {
    private static final long serialVersionUID = -1186238956313894637L;
    public static final Tuple2Int ZERO = new Tuple2Int(0, 0);
    /** 元素1 */
    private final int _1;
    /** 元素2 */
    private final int _2;

    public Tuple2Int(int _1, int _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public int get_1() {
        return _1;
    }

    public int get_2() {
        return _2;
    }

    public int _1() {
        return _1;
    }

    public int _2() {
        return _2;
    }

    public static Tuple2Int create(int t1, int t2) {
        return new Tuple2Int(t1, t2);
    }

    public static Tuple2Int create(int t1) {
        return new Tuple2Int(t1, 0);
    }

    public static Tuple2Int createOrZero(int[] arr) {
        if (arr != null && arr.length >= 2) {
            return new Tuple2Int(arr[0], arr[1]);
        } else {
            return ZERO;
        }
    }

    public boolean isZero() {
        return _1 == 0 && _2 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Tuple2Int tuple2Int = (Tuple2Int) o;

        if (_1 != tuple2Int._1) { return false; }
        return _2 == tuple2Int._2;

    }

    @Override
    public int hashCode() {
        int result = _1;
        result = 31 * result + _2;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"_1\" : " + _1 +
                ", \"_2\" : " + _2 +
                '}';
    }
}
