package com.ftkj.manager.battle.model;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.ablity.TeamAbility;

/**
 * 战斗球队属性，常驻生命周期与比赛一致 各项属性只在变更时修改
 *
 * @author tim.huang
 */
public class BattleTeamAbility extends BaseAbility {
    private static final long serialVersionUID = 1L;
    /** 球员战力 */
    private PlayerAbility playerAbility;
    private TeamAbility teamAbility;
    private TeamAbility tmpAbility;
    /** 成功权重 */
    private int succWeight;
    /** 失败权重 */
    private int failWeight;

    // Buffer部分

    public BattleTeamAbility(AbilityType type, TeamAbility teamAbility) {
        super(type);
        this.playerAbility = new PlayerAbility(AbilityType.Player, 0);
        if (teamAbility == null) {
            this.teamAbility = new TeamAbility(AbilityType.Team);
        } else {
            this.teamAbility = teamAbility;
        }
        this.tmpAbility = new TeamAbility(AbilityType.Temp);
    }

    @Override
    public void clear() {
    }

    public TeamAbility clearCap() {
        this.teamAbility.clear();
        return this.teamAbility;
    }

    public PlayerAbility clearPlayerAbility() {
        this.playerAbility.clear();
        this.tmpAbility.clear();
        return this.playerAbility;
    }

    public TeamAbility getTmpAbility() {
        return this.tmpAbility;
    }

    @Override
    public float getAttrData(EActionType type) {
        return this.playerAbility.getAttrData(type) + this.teamAbility.getAttrData(type) + this.tmpAbility.getAttrData(type);
    }

    public float getViewCap(EActionType type) {
        return this.playerAbility.getAttrData(type) + this.teamAbility.getAttrData(type);
    }

    public int getSuccWeight() {
        return succWeight;
    }

    public void setSuccWeight(int succWeight) {
        this.succWeight = succWeight;
    }

    public int getFailWeight() {
        return failWeight;
    }

    public void setFailWeight(int failWeight) {
        this.failWeight = failWeight;
    }

    @Override
    public String toString() {
        return "{" +
            "\"pr\":" + playerAbility +
            ", \"team\":" + teamAbility +
            ", \"tmp\":" + tmpAbility +
            ", \"succw\":" + succWeight +
            ", \"failw\":" + failWeight +
            '}';
    }
}
