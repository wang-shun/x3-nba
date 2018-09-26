package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 清空增益技能BUFFER
 *
 * @author tim.huang
 */
public class ClearSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;

    public ClearSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        List<BattleBuffer> tempList = bt.getBuffers().stream()
                .filter(buff -> buff.getBuffer().isDebuff())
                .collect(Collectors.toList());
        bt.setBuffers(tempList);
    }

}
