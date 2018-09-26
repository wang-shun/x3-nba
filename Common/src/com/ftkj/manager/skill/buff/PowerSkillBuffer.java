package com.ftkj.manager.skill.buff;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 体能相关技能BUFFER
 *
 * @author tim.huang
 */
public class PowerSkillBuffer extends SkillBuffer {
    private static final Logger log = LoggerFactory.getLogger(PowerSkillBuffer.class);
    private static final long serialVersionUID = 1L;
    public int power;

    public PowerSkillBuffer(SkillBufferVO vo) {
        super(vo);
    }

    @Override
    protected void initVal() {
        String p = super.getValMap().get("power");
        this.power = Ints.tryParse(p);
    }

    @Override
    public void execute(long teamId, BattleSource bs) {
        BattleTeam bt = bs.getTeam(teamId);
        //		if(super.isHome()){//使用方效果
        //			bt = bs.getTeam(teamId);
        //		}
        //		else{
        //			bt = bs.getHomeTeam().getTeamId() == teamId?bs.getAwayTeam():bs.getHomeTeam();
        //		}
        log.debug("打印教练技能-体力回复[{}],[{}]", teamId, this.power);
        bt.getLineupPlayers().values()
                .forEach(player -> bs.stats().upPower(player.getPlayer(), power));

    }

}
