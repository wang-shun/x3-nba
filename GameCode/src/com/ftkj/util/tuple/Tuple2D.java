package com.ftkj.util.tuple;

/** @author luch */
public class Tuple2D {
    /** 元素1 */
    private final double _1;
    /** 元素2 */
    private final double _2;

    public Tuple2D(double _1, double _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public double get_1() {
        return _1;
    }

    public double get_2() {
        return _2;
    }

    public double _1() {
        return _1;
    }

    public double _2() {
        return _2;
    }

    public static Tuple2D create(double t1, double t2) {
        return new Tuple2D(t1, t2);
    }

    public static Tuple2D create(double t1) {
        return new Tuple2D(t1, 0);
    }

    @Override
    public String toString() {
        return "Tuple2D{" + _1 + "," + _2 + "}";
    }
}
