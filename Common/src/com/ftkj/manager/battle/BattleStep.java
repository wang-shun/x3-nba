package com.ftkj.manager.battle;

import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.cfg.battle.BattleStepBean;

/** 比赛小节配置 */
public interface BattleStep extends Iterable<BattleStepBean> {

    /**
     * 获得比赛小节回合数
     */
    int getBattleStepRound(EBattleStep step);

    BattleStepBean getBattleStep(EBattleStep step);

    /** 根据回合数获取小节 */
    BattleStepBean getStepByRound(int round);
    //    default float getRoundMin(int totalRound) {
    //        int totalMin = 48;
    //        int v1 = getBattleStepRound(EBattleStep.Start) +
    //                getBattleStepRound(EBattleStep.First_Period) +
    //                getBattleStepRound(EBattleStep.Second_Period) +
    //                getBattleStepRound(EBattleStep.Thrid_Period) +
    //                getBattleStepRound(EBattleStep.Fourth_Period) +
    //                getBattleStepRound(EBattleStep.End);
    //        if (totalRound > v1) {//还有加时
    //            v1 += getBattleStepRound(EBattleStep.Overtime);
    //            totalMin = 58;
    //        }
    //        return totalMin / v1;
    //    }

    //    default int getTotalRound() {
    //        return getBattleStepRound(EBattleStep.Start) +
    //                getBattleStepRound(EBattleStep.First_Period) +
    //                getBattleStepRound(EBattleStep.Second_Period) +
    //                getBattleStepRound(EBattleStep.Thrid_Period) +
    //                getBattleStepRound(EBattleStep.Fourth_Period);
    //    }

    boolean hasSteps();
}
