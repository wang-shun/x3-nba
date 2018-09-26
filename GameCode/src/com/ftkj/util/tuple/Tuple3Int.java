package com.ftkj.util.tuple;

import java.io.Serializable;

/** @author luch */
public class Tuple3Int implements Serializable {
    public static final Tuple3Int ZERO = new Tuple3Int(0, 0, 0);
    private static final long serialVersionUID = 9184643819179730177L;
    /** 元素1 */
    private final int _1;
    /** 元素2 */
    private final int _2;
    /** 元素2 */
    private final int _3;

    public Tuple3Int(int _1, int _2, int _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
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

    public int _3() {
        return _3;
    }

    public int get_3() {
        return _3;
    }

    public static Tuple3Int create(int t1, int t2, int t3) {
        return new Tuple3Int(t1, t2, t3);
    }

    public static Tuple3Int create(int t1, int t2) {
        return new Tuple3Int(t1, t2, 0);
    }

    public static Tuple3Int create(int t1) {
        return new Tuple3Int(t1, 0, 0);
    }

    public static Tuple3Int create(int[] arr) {
        return new Tuple3Int(arr[0], arr[1], arr[2]);
    }

    public static Tuple3Int createOrZero(int[] arr) {
        if (arr != null && arr.length >= 3) {
            return create(arr);
        } else {
            return ZERO;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"_1\" : " + _1 +
                ", \"_2\" : " + _2 +
                ", \"_3\" : " + _3 +
                '}';
    }
}
