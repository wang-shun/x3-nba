package com.ftkj.util.lambda;

import java.io.Serializable;

public class LBFloat implements Serializable {
    private static final long serialVersionUID = 1L;
    private float val;

    public LBFloat() {
    }

    public LBFloat(float val) {
        this.val = val;
    }

    public LBFloat sum(float v) {
        this.val += v;
        return this;
    }

    public LBFloat sum(LBFloat other) {
        this.val += other.val;
        return this;
    }

    public float getVal() {
        return val;
    }

    public int intValue() {
        return (int) val;
    }

    public void setVal(float val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}
