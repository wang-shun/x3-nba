package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * 禁止使用技能BUFFER
 *
 * @author tim.huang
 */
public class StopSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;

    public StopSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        bt.getStat().setCanUseCoach(false);
    }

}
