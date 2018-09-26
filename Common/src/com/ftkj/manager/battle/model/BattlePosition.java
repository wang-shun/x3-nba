package com.ftkj.manager.battle.model;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.coach.CoachBean;

import java.io.Serializable;

/**
 * 比赛位置信息
 *
 * @author tim.huang
 */
public class BattlePosition implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 场上位置 */
    private final EPlayerPosition position;
    private BattlePlayer player;
    /**
     * 位置对犯规行为的随机命中量
     */
    private final int ranFGCount;

    private boolean isForce;
    
    public BattlePosition(EPlayerPosition position) {
        this.position = position;
        //默认占18，根据位置不同占不同占比
        if (position == EPlayerPosition.C) {
            this.ranFGCount = 24;
        } else if (position == EPlayerPosition.PF) {
            this.ranFGCount = 22;
        } else {
            this.ranFGCount = 18;
        }
    }

    public void initPlayer(BattlePlayer player) {
        this.player = player;
    }

    public void updatePlayer(BattlePlayer player, BattleTactics jg, BattleTactics fs, BattleTactics ajg, BattleTactics afs, CoachBean coach) {
        if (this.player != null) {
            this.player.removeAbility(AbilityType.Tactic_Retrain);
            //            this.player.removeAbility(AbilityType.Tactic_Offense);
            //            this.player.removeAbility(AbilityType.Tactic_Defense);

        }
        this.player = player;
        updateTactics(jg, fs, ajg, afs, coach);
    }

    public void updateTactics(BattleTactics dt, BattleTactics ot, BattleTactics odt, BattleTactics oot, CoachBean coach) {
        player.updateTactics(AbilityType.Tactic_Retrain, new TacticsAbility(AbilityType.Tactic_Retrain, dt, ot, odt, oot, coach, this));
        //        this.player.updateTactics(AbilityType.Tactic_Offense, new TacticsAbility(AbilityType.Tactic_Offense, jg, fs, ajg, afs, coach, this));
        //        this.player.updateTactics(AbilityType.Tactic_Defense, new TacticsAbility(AbilityType.Tactic_Defense, jg, fs, ajg, afs, coach, this));
    }

    public int getRanFGCount() {
        return ranFGCount;
    }

    public EPlayerPosition getPosition() {
        return position;
    }

    public BattlePlayer getPlayer() {
        return player;
    }
    
    

    public boolean isForce() {
      return isForce;
    }

    public void setForce(boolean isForce) {
      this.isForce = isForce;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pos\":" + position +
                ", \"pr\":" + player +
                '}';
    }
}
