package com.ftkj.util.tuple;

import java.io.Serializable;

/** @author luch */
public class Tuple2<T1, T2> implements Serializable {
    private static final long serialVersionUID = 3184614586744439344L;
    /** 元素1 */
    private final T1 _1;
    /** 元素2 */
    private final T2 _2;

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T1 get_1() {
        return _1;
    }

    public T2 get_2() {
        return _2;
    }

    public T1 _1() {
        return _1;
    }

    public T2 _2() {
        return _2;
    }

    public static <T1, T2> Tuple2<T1, T2> create(T1 t1, T2 t2) {
        return new Tuple2<>(t1, t2);
    }

    public static <T1, T2> Tuple2<T1, T2> create(T1 t1) {
        return new Tuple2<>(t1, null);
    }

    @Override
    public String toString() {
        return "{" +
                "\"_1\":" + _1 +
                ", \"_2\":" + _2 +
                '}';
    }
}
