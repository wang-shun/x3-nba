package com.ftkj.cfg;

/** 比较操作符. -eq, -ne, -lt, -le, -gt or -ge */
public enum CompareOp {
    /** < */
    LessThan("<"),
    /** <= */
    LessEqual("<="),
    /** > */
    GreaterThan(">"),
    /** >= */
    GreaterEqual(">="),
    /** = */
    Equal("=");

    private final String op;

    CompareOp(String op) {
        this.op = op;
    }

    public static CompareOp convert(String op) {
        if (op == null) {
            return null;
        }
        switch (op) {
            case "<": return LessThan;
            case "<=": return LessEqual;
            case ">": return GreaterThan;
            case ">=": return GreaterEqual;
            case "=": return Equal;
        }
        return null;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toString() {
        return op;
    }

    public boolean compare(float o1, float o2) {
        switch (this) {
            case LessThan: return Float.compare(o1, o2) < 0;
            case LessEqual: return Float.compare(o1, o2) <= 0;
            case GreaterThan: return Float.compare(o1, o2) > 0;
            case GreaterEqual: return Float.compare(o1, o2) >= 0;
            case Equal: return Float.compare(o1, o2) == 0;
        }
        return false;
    }

    public boolean compare(int o1, int o2) {
        switch (this) {
            case LessThan: return Integer.compare(o1, o2) < 0;
            case LessEqual: return Integer.compare(o1, o2) <= 0;
            case GreaterThan: return Integer.compare(o1, o2) > 0;
            case GreaterEqual: return Integer.compare(o1, o2) >= 0;
            case Equal: return Integer.compare(o1, o2) == 0;
        }
        return false;
    }
}
