package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.util.excel.RowData;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * 战术加成
 *
 * @author Jay
 * @time:2017年5月12日 下午5:43:43
 */
public class TacticsCapBean extends ExcelBean implements Serializable {
    private static final long serialVersionUID = -7050004308395430405L;
    private int id;
    private int lv;
    /** map[位置, 千分率] */
    private Map<EPlayerPosition, Float> posCapRate;
    /** 生效的 map[位置, 千分率] */
    private Map<EPlayerPosition, Float> effectivePosCapRate;
    private float PG;
    private float SG;
    private float SF;
    private float PF;
    private float C;

    @Override
    public void initExec(RowData row) {
        posCapRate = new EnumMap<>(EPlayerPosition.class);
        posCapRate.put(EPlayerPosition.PG, PG / 1000f);
        posCapRate.put(EPlayerPosition.SG, SG / 1000f);
        posCapRate.put(EPlayerPosition.SF, SF / 1000f);
        posCapRate.put(EPlayerPosition.PF, PF / 1000f);
        posCapRate.put(EPlayerPosition.C, C / 1000f);

        effectivePosCapRate = new EnumMap<>(EPlayerPosition.class);
        posCapRate.forEach((k, v) -> {
            if (Float.compare(v, 0) != 0) {
                effectivePosCapRate.put(k, v);
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public Float getPosCapRate(EPlayerPosition position) {
        return posCapRate.get(position);
    }

    public void setPosCapRate(Map<EPlayerPosition, Float> posCapRate) {
        this.posCapRate = posCapRate;
    }

    public Map<EPlayerPosition, Float> getPosCapRate() {
        return posCapRate;
    }

    public Map<EPlayerPosition, Float> getEffectivePosCapRate() {
        return effectivePosCapRate;
    }

    public float getPG() {
        return PG;
    }

    public void setPG(float pG) {
        PG = pG;
    }

    public float getSG() {
        return SG;
    }

    public void setSG(float sG) {
        SG = sG;
    }

    public float getSF() {
        return SF;
    }

    public void setSF(float sF) {
        SF = sF;
    }

    public float getPF() {
        return PF;
    }

    public void setPF(float pF) {
        PF = pF;
    }

    public float getC() {
        return C;
    }

    public void setC(float c) {
        C = c;
    }

}
