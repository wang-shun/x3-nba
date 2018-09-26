package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;

/**
 * 比赛行为统计数据
 */
public interface ReadOnlyActionStats {

    float getValue(EActionType type);

    int getIntValue(EActionType type);

    float getActionValue(EActionType act);
}
