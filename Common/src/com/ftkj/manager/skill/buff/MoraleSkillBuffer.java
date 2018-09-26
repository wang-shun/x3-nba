package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.google.common.primitives.Ints;

/**
 * 士气相关技能BUFFER
 *
 * @author tim.huang
 * 2017年9月14日
 */
public class MoraleSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;
    public int morale;

    public MoraleSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
        String p = super.getValMap().get("morale");
        this.morale = Ints.tryParse(p);
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        bt.updateMorale(this.morale);
    }

}
