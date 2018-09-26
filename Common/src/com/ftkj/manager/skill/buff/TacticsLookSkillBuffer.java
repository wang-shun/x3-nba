package com.ftkj.manager.skill.buff;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * 查看战术相关技能BUFFER
 *
 * @author tim.huang
 */
public class TacticsLookSkillBuffer extends SkillBuffer {
    private static final long serialVersionUID = 1L;

    public TacticsLookSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        bs.getReport().addSubAct(teamId, 0, EActionType.tactics_look,
                bt.getPkTactics(TacticType.Offense).getTactics().getId().getId(),
                bt.getPkTactics(TacticType.Defense).getTactics().getId().getId(), 0,false);

        //		bt.getPkPlayerMap().values().forEach(player->player.getPlayer().updatePower(this.power));
    }

}
