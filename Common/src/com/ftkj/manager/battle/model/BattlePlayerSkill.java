package com.ftkj.manager.battle.model;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.manager.player.PlayerBean;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年9月5日
 * 比赛球员技能
 */
public class BattlePlayerSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int _minSkillPower = -50;
    private int playerId;
    /** 携带的进攻技能 */
    private Skill attack;
    /** 携带的防守技能 */
    private Skill defend;
    /** 技能能量条 */
    private int skillPower;

    private int maxSkillPower;

    public BattlePlayerSkill(int playerId, Skill attackSkill, Skill defendSkill, int maxSkillPower) {
        this.playerId = playerId;
        this.attack = attackSkill;
        this.defend = defendSkill;
        this.maxSkillPower = maxSkillPower;
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        if (pb.getGrade() == EPlayerGrade.X) {
            this.skillPower = 7000;
        } else {
            this.skillPower = 5000 + (int) pb.getAbility(EActionType.skill_power) * 100;
        }
        this.skillPower = this.skillPower >= 10000 ? 10000 : this.skillPower;
        this.skillPower = this.skillPower < 0 ? 0 : this.skillPower;
        //		this.skillPower = (int).getAbility(EActionType.T_效率);
    }

    public int getMaxSkillPower() {
        return this.maxSkillPower;
    }

    public int getSkillPower() {
        return skillPower;
    }

    public int setSkillPower(int skillPower) {
        this.skillPower = Math.max(_minSkillPower, Math.min(maxSkillPower, skillPower));
        return skillPower;
    }

    public int getCurMaxSkillPower() {
        if (this.skillPower >= 30000) {
            return 30000;
        } else if (this.skillPower >= 20000) {
            return 20000;
        } else {
            return 10000;
        }
    }

    public int updateSkillPower(int val) {
        int newVal = this.skillPower + val;
        //		int newVal = this.skillPower + val * (val>0?50:1);
        if (newVal <= _minSkillPower) {
            newVal = _minSkillPower;
        }
        this.skillPower = newVal >= maxSkillPower ? maxSkillPower : newVal;
        return skillPower;
    }

    public int getPlayerId() {
        return playerId;
    }

    /** 是否符合使用技能的要求 */
    public boolean useSkill(boolean attack) {
        return true;
    }

    public Skill getAttack() {
        return attack;
    }

    public Skill getDefend() {
        return defend;
    }

}
