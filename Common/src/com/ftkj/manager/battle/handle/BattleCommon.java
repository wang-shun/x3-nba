package com.ftkj.manager.battle.handle;

import java.util.Map;

import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.statistics.TeamDayStatistics;
import com.google.common.collect.Maps;

/**
 * 普通战斗逻辑
 *
 * @author tim.huang
 */
public class BattleCommon extends BaseBattleHandle {
    private static final float Exp_Win_Rate = 1.2f;
    private static final float Exp_Loss_Rate = 0.8f;

    /**
     * 控制比赛次数掉落
     * 比赛类型：  每天最大掉落场次数
     */
    private static Map<EBattleType, Integer> battleDropConfig;

    static {
        battleDropConfig = Maps.newHashMap();
        battleDropConfig.put(EBattleType.多人赛_100, 15);
        battleDropConfig.put(EBattleType.Ranked_Match, 15);
        // 不掉落的控制
        battleDropConfig.put(EBattleType.街球副本, 0);
    }

    public BattleCommon() {
    }

    public BattleCommon(BattleSource battleSource) {
        super(battleSource);
    }

    /** 多人赛做特殊转换，取比赛类型 */
    private static EBattleType getMyBattleType(EBattleType type) {
        if (type.getId() >= 100 && type.getId() <= 200) {
            return EBattleType.多人赛_100;
        }
        return type;
    }

    public BattleCommon(BattleSource battleSource, BattleEnd end, BattleRoundReport round) {
        super(battleSource, end, round);
    }

    /** 是否超出每日次数限制 */
    private boolean isOverlimitNum(BattleTeam win, EBattleType myType) {
        if (battleDropConfig.containsKey(myType)) {
            TeamDayStatistics tds = win.getDayStatistics();
            if (tds == null) {
                return false;
            }
            int num = tds.getPkCount(myType);
            return num >= battleDropConfig.get(myType);
        }
        return false;
    }

    @Override
    protected synchronized int getWinExp(BattleTeam win, BattleTeam loss) {
        int winExp = Math.round(win.getLevel() * ConfigConsole.getGlobal().Main_Win_Exp);
        //        int winGlod = win.getLevel() * 10;
        EBattleType myType = getMyBattleType(super.getBattleSource().getType());
        if (isOverlimitNum(win, myType)) {
            return 0;
        }
        return winExp;
    }

    @Override
    protected synchronized int getLossExp(BattleTeam win, BattleTeam loss) {
        int lossExp = Math.round(loss.getLevel() * ConfigConsole.getGlobal().Main_Lost_Exp);
        EBattleType myType = getMyBattleType(super.getBattleSource().getType());
        if (isOverlimitNum(loss, myType)) {
            return 0;
        }
        //        int lossGlod = 10;
        return lossExp;
    }

    @Override
    protected synchronized int getWinTactics(BattleTeam win, BattleTeam loss) {
        EBattleType myType = getMyBattleType(super.getBattleSource().getType());
        if (isOverlimitNum(win, myType)) {
            return 0;
        }
        return super.getWinTactics(win, loss);
    }

    @Override
    protected synchronized int getLossTactics(BattleTeam win, BattleTeam loss) {
        EBattleType myType = getMyBattleType(super.getBattleSource().getType());
        if (isOverlimitNum(loss, myType)) {
            return 0;
        }
        return super.getLossTactics(win, loss);
    }

}
