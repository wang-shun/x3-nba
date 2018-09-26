package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * 相关技能BUFFER
 *
 * @author tim.huang
 */
public class TacticsSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;

    public TacticsSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        bt.getStat().setCanUseTactics(false);
    }

}
