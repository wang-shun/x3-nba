package com.ftkj.util.tuple;

/** @author luch */
public class Tuple2F {
    /** 元素1 */
    private final float _1;
    /** 元素2 */
    private final float _2;

    public Tuple2F(float _1, float _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public float get_1() {
        return _1;
    }

    public float get_2() {
        return _2;
    }

    public float _1() {
        return _1;
    }

    public float _2() {
        return _2;
    }

    public static Tuple2F create(float t1, float t2) {
        return new Tuple2F(t1, t2);
    }

    public static Tuple2F create(float t1) {
        return new Tuple2F(t1, 0);
    }

    @Override
    public String toString() {
        return "Tuple2F{" + _1 + "," + _2 + "}";
    }
}
