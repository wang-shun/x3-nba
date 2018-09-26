package com.ftkj.manager.battle.subaction;

import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleActionBean;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.battle.ActionPlayerHandle;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.RoundSkill;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.RoundReport;

import java.util.Random;

/**
 * 行为执行接口
 *
 * @author tim.huang
 * @author luch
 */
public interface SubAction {
    /** 行为执行. 每个 BattleAction 都有独立的 ActionContext */
    void doAction(SubActionContext ctx);

    /** 行为类型 */
    default EActionType getType() {
        return EActionType.NULL;
    }

    /** 行为参数(上下文) */
    SubActionContext newContext(BattleSource bs,
                                BattleTeam ball,
                                BattleTeam otherBall,
                                BattleActionBean bean,
                                BaseSubActionBean subActionbean,
                                RoundSkill roundSkill,
                                Random ran,
                                ActionPlayerHandle actPlayerHandle);

    /** 行为执行参数 */
    interface SubActionContext {
        /** battle id */
        long bid();

        BattleSource bs();

        EBattleStep step();

        RoundReport report();

        /** 交换球权 */
        void swapBall();

        /** 球权方 */
        BattleTeam ball();

        BattleTeam otherBall();

        BattleActionBean bean();

        BaseSubActionBean subBean();

        RoundSkill roundSkill();

        Random random();

        /** 执行当前行为的位置 */
        BattlePosition bp();

        void setBp(BattlePosition bp);

        /** 执行当前行为的球员 */
        BattlePlayer bpr();

        ActionPlayerHandle actPlayerHandle();

        /** 计算并查找可以执行行为的球员 */
        EPlayerPosition calcAndFindActPlayer(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran);
    }

}
