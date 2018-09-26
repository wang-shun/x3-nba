package com.ftkj.manager.cap;

/**
 * 战力
 */
public class Cap {
    /** 进攻战力 */
    private float ocap;
    /** 防守战力 */
    private float dcap;

    public Cap() {
    }

    public Cap(Cap... caps) {
        for (Cap cap : caps) {
            this.ocap += cap.ocap;
            this.dcap += cap.dcap;
        }
    }

    public Cap(float ocap, float dcap) {
        this.ocap = ocap;
        this.dcap = dcap;
    }

    public float getOcap() {
        return ocap;
    }

    public float getDcap() {
        return dcap;
    }

    public float getTotalCap() {
        return ocap + dcap;
    }

    public void setOcap(float ocap) {
        this.ocap = ocap;
    }

    public void setDcap(float dcap) {
        this.dcap = dcap;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ocap\":" + ocap +
                ", \"dcap\":" + dcap +
                '}';
    }
}
