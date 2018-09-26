package com.ftkj.util.tuple;

import java.io.Serializable;

/** @author luch */
public class Tuple2Long implements Serializable {
    public static final Tuple2Long ZERO = new Tuple2Long(0, 0);
    private static final long serialVersionUID = -7812843619709127091L;
    /** 元素1 */
    private final long _1;
    /** 元素2 */
    private final long _2;

    public Tuple2Long(long _1, long _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public long get_1() {
        return _1;
    }

    public long get_2() {
        return _2;
    }

    public long _1() {
        return _1;
    }

    public long _2() {
        return _2;
    }

    public static Tuple2Long create(long t1, long t2) {
        return new Tuple2Long(t1, t2);
    }

    public static Tuple2Long create(long t1) {
        return new Tuple2Long(t1, 0);
    }

    @Override
    public String toString() {
        return "{" +
                "\"_1\" : " + _1 +
                ", \"_2\" : " + _2 +
                '}';
    }
}
