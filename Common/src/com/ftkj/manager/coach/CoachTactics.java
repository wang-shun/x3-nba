package com.ftkj.manager.coach;

import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class CoachTactics implements Serializable {
    private static final long serialVersionUID = 5502257248230608390L;
    private TacticId tactics;
    private Map<EPlayerPosition, Float> posCapRate;

    public CoachTactics(TacticId tactics, int PG, int SG, int SF, int PF,
                        int C) {
        super();
        this.tactics = tactics;
        this.posCapRate = new EnumMap<>(EPlayerPosition.class);
        this.posCapRate.put(EPlayerPosition.C, C / 1000f);
        this.posCapRate.put(EPlayerPosition.PF, PF / 1000f);
        this.posCapRate.put(EPlayerPosition.PG, PG / 1000f);
        this.posCapRate.put(EPlayerPosition.SF, SF / 1000f);
        this.posCapRate.put(EPlayerPosition.SG, SG / 1000f);
    }

    public Float getPosCapRate(EPlayerPosition position) {
        return posCapRate.getOrDefault(position, 0f);
    }

    public TacticId getTactics() {
        return tactics;
    }

    public void setTactics(TacticId tactics) {
        this.tactics = tactics;
    }
}
