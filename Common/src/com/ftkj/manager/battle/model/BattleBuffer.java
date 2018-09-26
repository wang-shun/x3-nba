package com.ftkj.manager.battle.model;

import java.io.Serializable;

import com.ftkj.manager.skill.buff.SkillBuffer;

/**
 * @author tim.huang
 * 2017年9月18日
 * 战斗Buffer
 */
public class BattleBuffer implements Serializable {
    private static final long serialVersionUID = 1L;
    private long teamId;
    /** 技能buff*/
    private SkillBuffer buffer;
    /** 效果持续的结束回合*/
    private int endRound;
    private String val;

    public BattleBuffer(long teamId, SkillBuffer buffer, int curRound) {
        super();
        this.buffer = buffer;
        this.endRound = buffer.getRunRound() + curRound;
        this.teamId = teamId;
        this.val = "";
    }

    public BattleBuffer(long teamId, SkillBuffer buffer, int endRound,
                        String val) {
        super();
        this.teamId = teamId;
        this.buffer = buffer;
        this.endRound = endRound;
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public boolean active(int curRound) {
        return endRound >= curRound;
    }

    public long getTeamId() {
        return teamId;
    }

    public SkillBuffer getBuffer() {
        return buffer;
    }

    public int getEndRound() {
        return endRound;
    }

    public void increaseEndRound(int val) {
        this.endRound += val;
    }

}
