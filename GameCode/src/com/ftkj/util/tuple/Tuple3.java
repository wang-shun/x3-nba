package com.ftkj.util.tuple;

/** @author luch */
public class Tuple3<T1, T2, T3> {
    /** 元素1 */
    private final T1 _1;
    /** 元素2 */
    private final T2 _2;

    private final T3 _3;

    public Tuple3(T1 _1, T2 _2, T3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    public T1 get_1() {
        return _1;
    }

    public T2 get_2() {
        return _2;
    }

    public T3 get_3() {
        return _3;
    }

    public T1 _1() {
        return _1;
    }

    public T2 _2() {
        return _2;
    }

    public T3 _3() {
        return _3;
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> create(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> create(T1 t1) {
        return new Tuple3<>(t1, null, null);
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
