package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * 禁止更换球员BUFFER
 *
 * @author tim.huang
 */
public class PlayerSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;

    public PlayerSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        bt.getStat().setCanSubPlayer(false);
    }

}
