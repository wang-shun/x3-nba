package com.ftkj.manager.battle;

import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.tactics.TacticsSimple;
import com.ftkj.util.tuple.Tuple2;

import java.util.List;

/**
 * 战斗对外提供接口
 *
 * @author tim.huang
 */
public interface BattleHandle {

    RoundReport pk();

    /**
     * 玩家点击开始比赛调用，npc在初始化时会自动准备。
     * 建议道具扣除等操作在调用ready的时候就调用
     */
    boolean ready(long teamId);

    /**
     * 修改默认装备的初始道具
     */
    void changeProp(long teamId, List<PropSimple> props);

    /**
     * 修改默认的战术
     */
    void changeTactis(long teamId, List<TacticsSimple> tacticsList);

    /**
     * 更换球员，将A,B球员位置互换
     */
    void changePlayerPos(long teamId, int aid, int bid);

    /**
     * 战斗中使用道具
     */
    ErrorCode useProp(long teamId, int pid, int tagerPlayer, int otherPlayer);

    /**
     * 战斗中修改球员位置
     */
    ErrorCode updatePlayerPosition(long teamId, String newPosition);

    /**
     * 战斗中修改战术
     */
    ErrorCode updateTactics(long teamId, int atid, int dtid);

    /**
     * 战斗中使用技能
     */
    ErrorCode usePlayerSkill(long teamId, int playerId, boolean attack);

    /**
     * 战斗中使用教练技能
     */
    Tuple2<ErrorCode, List<BattleBuffer>> useCoachSkill(long teamId, int sid);

    /**
     * 暂停比赛
     */
    void pause(boolean pause);

    /**
     * 快速结束比赛
     */
    void quickEnd();

    /**
     * 获取战斗数据对象
     */
    BattleSource getBattleSource();

    /**
     * 获取战斗结束回调方法
     */
    BattleEnd getEnd();

    /**
     * 获得回合执行方法
     */
    BattleRoundReport getRound();

    BattleStartPush getStartPush();

    /**
     * 检查赛前准备超时
     */
    boolean checkBeforeTimeOut();

    /** 比赛结束 */
    void end();

    //	/**
    //	 * @param teamId
    //	 * @return
    //	 * 战斗中打开数据列表
    //	 */
    //	public List<BattlePlayerPKSource> pkGetBattlePlayerSource(long teamId);

}

