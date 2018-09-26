package com.ftkj.manager.battle.model;

import com.ftkj.manager.skill.SkillBean;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年9月6日
 * 比赛技能
 */
public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;
    private int playerId;
    private int step;
    private int level;
    private boolean type;
    private int executeLevel;
    private SkillBean skill;

    public Skill(int playerId, int step, int level, boolean type, SkillBean skill) {
        super();
        this.playerId = playerId;
        this.skill = skill;
        this.step = step;
        this.level = level;
        this.type = type;
    }

    public int getExecuteLevel() {
        return executeLevel;
    }

    public void setExecuteLevel(int executeLevel) {
        this.executeLevel = executeLevel <= 0 ? 1 : executeLevel;
    }

    public int getStep() {
        return step;
    }

    public int getLevel() {
        return level;
    }

    public int getPlayerId() {
        return playerId;
    }

    public SkillBean getSkill() {
        return skill;
    }

    public boolean isType() {
        return type;
    }

    /** 是否进攻类技能 */
    public boolean attackSkill() {
        return this.skill.attack();
    }

    @Override
    public String toString() {
        return "{" +
                "\"prid\":" + playerId +
                ", \"lev\":" + level +
                ", \"elev\":" + executeLevel +
                ", \"skill\":" + skill +
                '}';
    }
}
