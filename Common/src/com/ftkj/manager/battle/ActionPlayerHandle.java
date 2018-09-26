package com.ftkj.manager.battle;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.battle.model.BattleTeam;

import java.util.Random;

/**
 * 计算并查找可以执行行为的球员
 */
public interface ActionPlayerHandle {

    /** 计算并查找可以执行行为的球员 */
    EPlayerPosition calcAndFindActPlayer(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran);
}
