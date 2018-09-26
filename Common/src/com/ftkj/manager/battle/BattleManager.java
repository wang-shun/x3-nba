package com.ftkj.manager.battle;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.CoachConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.manager.AbstractBaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.RoundSkill;
import com.ftkj.manager.battle.model.Skill;
import com.ftkj.manager.coach.CoachSkillBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.tactics.TacticsSimple;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.GameLogPB;
import com.ftkj.proto.GameLogPB.BattleEndLogData;
import com.ftkj.proto.GameLogPB.BattleEndTeamResp;
import com.ftkj.server.CrossCode;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.util.PbUtil;
import com.ftkj.util.StringUtil;
import com.ftkj.util.tuple.Tuple2;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 战斗管理,不做数据处理
 *
 * @author tim.huang
 */
public class BattleManager extends AbstractBaseManager {
    private static final Logger log = LoggerFactory.getLogger(BattleManager.class);

    @ClientMethod(code = ServiceCode.Battle_Remove_Listener)
    public void removeBattleService(long battleId) {
        ServiceManager.removeService(ServiceConsole.getBattleKey(battleId), getTeamId());
        sendMessage(DefaultData.newBuilder().setCode(0).build());
    }

    @ClientMethod(code = ServiceCode.Battle_All)
    public void showBattle(long battleId) {
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMsg(ErrorCode.Battle_1);
            return;
        }
        long teamId = getTeamId();
        BattleSource bs = battle.getBattleSource();
        boolean isBattleTeam = teamId == bs.getHome().getTeamId() || teamId == bs.getAway().getTeamId();//赛前阶段，推送赛前准备界面.旁观者无法进入
        ServiceManager.addService(ServiceConsole.getBattleKey(battleId), teamId);
        sendMessage(teamId, BattlePb.battleMainData(teamId, isBattleTeam, bs), ServiceCode.Battle_All_Push);
        sendMsg(ErrorCode.Success);
    }

    @ClientMethod(code = ServiceCode.Battle_showHalfTimeSource)
    public void showHalfTimeSource(long battleId) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMsg(ErrorCode.Battle_1);
            return;
        }
        //比赛正在进行中,查询当前比赛数据
        BattleSource bs = battle.getBattleSource();
        BattleTeam home = bs.getHome().getTeamId() == teamId ? bs.getHome() : bs.getAway();
        Map<Integer, Float> hMap = new HashMap<>();
        for (BattlePlayer bp : home.getPlayers()) {
            hMap.put(bp.getPlayerId(), calcMvpActVal(bp));
        }
        BattleTeam away = bs.getHome().getTeamId() == teamId ? bs.getAway() : bs.getHome();
        Map<Integer, Float> aMap = new HashMap<>();
        for (BattlePlayer bp : away.getPlayers()) {
            aMap.put(bp.getPlayerId(), calcMvpActVal(bp));
        }

        int hmax = getMaxPlayerID(hMap);
        int amax = getMaxPlayerID(aMap);

        sendMessage(BattlePB.BattleHalfTimeData.newBuilder()
            .setHomePlayer(BattlePb.halfTimePlayerData(home.getPlayer(hmax)))
            .setAwayPlayer(BattlePb.halfTimePlayerData(away.getPlayer(amax)))
            .setHomeId(home.getTeamId())
            .setAwayId(away.getTeamId())
            .build());
    }

    private float calcMvpActVal(BattlePlayer bp) {
        return bp.getRealTimeActionStats().getValue(EActionType.pts) * 2
            + bp.getRealTimeActionStats().getValue(EActionType.reb) * 3
            + bp.getRealTimeActionStats().getValue(EActionType.ast) * 2
            + bp.getRealTimeActionStats().getValue(EActionType.blk) * 4
            + bp.getRealTimeActionStats().getValue(EActionType.stl) * 4;
    }

    private int getMaxPlayerID(Map<Integer, Float> map) {
        float max = 0;
        int pid = 0;
        for (int playerId : map.keySet()) {
            float tmp = map.get(playerId);
            if (tmp >= max) {
                max = tmp;
                pid = playerId;
            }
        }
        return pid;
    }

    @ClientMethod(code = ServiceCode.Battle_Before_Team_Data)
    public void showBattleBefore(long battleId) {
        //        long teamId = getTeamId();
        BattleHandle bh = BattleAPI.getInstance().getBattleHandle(battleId);
        BattlePB.BattleBeforeData.Builder resp = BattlePB.BattleBeforeData.newBuilder();
        if (bh == null) {
            sendMessage(resp.build());
            return;
        }

        BattleSource bs = bh.getBattleSource();
        BattlePB.BattlePKTeamData home = bs.getAttributeMap(bs.getHomeTid()).getVal(EBattleAttribute.Before_Data);
        BattlePB.BattlePKTeamData away = bs.getAttributeMap(bs.getAwayTid()).getVal(EBattleAttribute.Before_Data);
        resp.setId(bs.getId())
            .setBattleType(bs.getInfo().getBattleType().getId())
            .setHome(home)
            .setAway(away);
        sendMessage(resp.build());
    }

    /**
     * 战斗中使用道具
     */
    @ClientMethod(code = ServiceCode.Battle_PK_pkUseProp)
    public void pkUseProp(long battleId, int pid, int tagerPlayer, int otherPlayer) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }

        ErrorCode code = battle.useProp(teamId, pid, tagerPlayer, otherPlayer);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).build());
    }

    /**
     * 战斗中修改球员位置
     */
    @ClientMethod(code = ServiceCode.Battle_PK_pkUpdatePlayer)
    public void pkUpdatePlayer(long battleId, String newPosition) {
        ErrorCode ret = pkUpdatePlayer0(battleId, newPosition);
        sendMsg(ret);
    }

    private ErrorCode pkUpdatePlayer0(long battleId, String newPosition) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            return ErrorCode.Error;
        }
        if (newPosition == null || newPosition.isEmpty()) {
            return ErrorCode.Battle_UpPos_Null;
        }
        return battle.updatePlayerPosition(teamId, newPosition);
    }

    /**
     * 战斗中修改战术
     */
    @ClientMethod(code = ServiceCode.Battle_PK_pkUpdateTactics)
    public void pkUpdateTactics(long battleId, int atid, int dtid) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        ErrorCode code = battle.updateTactics(teamId, atid, dtid);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).build());
    }

    /** 查看比赛详细数据 */
    @ClientMethod(code = ServiceCode.Battle_PK_showPlayerSource)
    public void showPlayerSource(long battleId) {
        long teamId = getTeamId();
        GameLogPB.BattleEndLogData resp = redis.getObj(RedisKey.Battle_End_Source + battleId);
        if (resp != null) {//比赛已经结束，从redis中取历史数据
            sendResp(battleId, teamId, resp);
            return;
        }
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {//比赛不存在,并且历史数据中没有
            sendMessage(GameLogPB.BattleEndLogData.newBuilder().setCode(ErrorCode.Battle_2.code).build());
            return;
        }
        //比赛正在进行中,查询当前比赛数据
        BattleSource bs = battle.getBattleSource();
        sendMessage(BattlePb.getBattleEndLogData(bs.getHome(), bs.getAway()));
    }

    private void sendResp(long battleId, long teamId, BattleEndLogData resp) {
        log.debug("btm show stats. bid {} tid {} htid {} atid {} score {}:{}", battleId, teamId,
            resp.getHome().getTeamId(), resp.getAway().getTeamId(),
            resp.getHome().getScore(), resp.getAway().getScore());
        teamPrsLog(battleId, teamId, resp.getHome());
        teamPrsLog(battleId, teamId, resp.getAway());
        sendMessage(resp);
    }

    private void teamPrsLog(long battleId, long reqtid, BattleEndTeamResp htr) {
        if (log.isTraceEnabled()) {
            log.trace("btm show stats. bid {} reqtid {} tid {} stepscores {} prs [{}]", battleId, reqtid,
                htr.getTeamId(), PbUtil.shortDebug(htr.getStepScore()), PbUtil.shortDebug(htr.getPlayersList()));
        }
    }

    @ClientMethod(code = ServiceCode.Battle_PK_updateBattleEquProp)
    public void updateBattleEquProp(long battleId, String props) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        //TODO:暂时不做限制
        battle.changeProp(teamId, PropSimple.getPropBeanByStringNotConfig(props));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.Battle_PK_updateBattleEquTactics)
    public void updateBattleEquTactics(long battleId, String tactis) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        //TODO:暂时不做限制
        String[] ts = StringUtil.toStringArray(tactis, StringUtil.DEFAULT_ST);
        List<TacticsSimple> tacticsList = Lists.newArrayList();
        for (String t : ts) {
            tacticsList.add(new TacticsSimple(TacticId.convert(Ints.tryParse(t.split(StringUtil.DEFAULT_FH)[0])),
                Ints.tryParse(t.split(StringUtil.DEFAULT_FH)[1])));
        }
        battle.changeTactis(teamId, tacticsList);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.Battle_PK_useSkill)
    public void pkUseSkill(long battleId, int playerId, int attack) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_1.code).build());
            return;
        }
        if (battle.getBattleSource().getHome().getTeamId() != teamId &&
            battle.getBattleSource().getAway().getTeamId() != teamId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_Member.code).build());
            return;
        }
        ErrorCode code = battle.usePlayerSkill(teamId, playerId, attack == 1);
        RoundSkill rs = battle.getBattleSource().getRound().getSkill();
        if (rs != null && code == ErrorCode.Success) {
            int winSkill = 0;
            Skill hs = rs.getHomeSkill();
            Skill as = rs.getAwaySkill();
            if (hs != null && as != null) {
                winSkill = rs.getSkillActionTeam(true).getSkill().getSkillId();
            }

            if (!battle.getBattleSource().getInfo().isDisablePushMessage()) {
                BattlePB.BattleSkillPushData data = BattlePB.BattleSkillPushData
                    .newBuilder()
                    .setCode(code.code)
                    .setHomeSkillId(hs == null ? 0 : hs.getSkill().getSkillId())
                    .setHomePlayerId(hs == null ? 0 : hs.getPlayerId())
                    .setHomeLevel(hs == null ? 0 : hs.getLevel())
                    .setAwaySkillId(as == null ? 0 : as.getSkill().getSkillId())
                    .setAwayPlayerId(as == null ? 0 : as.getPlayerId())
                    .setAwayLevel(as == null ? 0 : as.getLevel())
                    .setWinSkillId(winSkill)
                    .build();

                sendMessage(ServiceConsole.getBattleKey(battleId), data, ServiceCode.Push_Battle_Skill);
            }
            log.debug("btget useskill. bid {} pid {} attack {} win {} rs {}", battleId, playerId, attack, winSkill, rs);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).build());
    }

    @ClientMethod(code = ServiceCode.Battle_PK_pkUseCoach)
    public void pkUseCoach(long battleId, int sid) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }

        CoachSkillBean skillBean = CoachConsole.getCoachSkillBean(sid);
        if (skillBean == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        Tuple2<ErrorCode, List<BattleBuffer>> cr = battle.useCoachSkill(teamId, sid);
        //        List<BattleBuffer> datas = cr._2();
        ErrorCode code = cr._1() != null ? cr._1() : ErrorCode.Success;
        //        if (datas != null) {
        //            sendMessage(ServiceConsole.getBattleKey(battleId)
        //                    , BattlePb.getBattleCoachSkillData(teamId, skillBean, datas)
        //                    , ServiceCode.Push_Battle_Coach);
        //        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).build());
    }

    //暂停
    @ClientMethod(code = ServiceCode.Battle_PK_pauseBattle)
    public void pauseBattle(int pause, long battleId) {
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        battle.pause(pause == 1);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    //快速结束比赛
    @ClientMethod(code = ServiceCode.Battle_Quick_End)
    public void quickBattle(long battleId) {
        ErrorCode ret = quickBattle0(battleId);
        sendMsg(ret);
    }

    private ErrorCode quickBattle0(long battleId) {
        long teamId = getTeamId();
        BattleHandle battle = BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            return ErrorCode.Battle_End;
        }
        BattleSource bs = battle.getBattleSource();
        if (bs.getHomeTid() != teamId && bs.getAwayTid() != teamId) {
            return ErrorCode.Battle_Member;
        }
        if (!bs.getInfo().isQuickEnd()) {
            return ErrorCode.Battle_Quick_Disable;
        }
        battle.quickEnd();
        return ErrorCode.Success;
    }

    /** 跨服 强制结束比赛 */
    @RPCMethod(code = CrossCode.Match_Force_End, pool = EServerNode.Battle, type = ERPCType.NONE)
    public void xQuickBattle(long teamId, long battleId, boolean homeWine) {
        gmQuickBattle0(teamId, battleId, homeWine);
    }

    /** gm命令强制结束比赛 */
    public ErrorCode gmQuickBattle0(long teamId, long battleId, boolean homeWin) {
        log.warn("btm gmQuickBattle0. tid {} bid {} homewin {}", teamId, battleId, homeWin);
        BaseBattleHandle battle = (BaseBattleHandle) BattleAPI.getInstance().getBattleHandle(battleId);
        if (battle == null) {
            log.debug("tid {} battle {} is null", teamId, battleId);
            return ErrorCode.Error;
        }
        BattleSource bs = battle.getBattleSource();
        if (bs != null) {
            BattleAttribute ba = bs.getOrCreateAttributeMap(0);
            ba.addVal(EBattleAttribute.GM_FORCE_END_MATCH, true);
            ba.addVal(EBattleAttribute.GM_FORCE_END_MATCH_HOME_WIN, homeWin);
        }
        battle.forceEnd(homeWin,battle);
        return ErrorCode.Success;
    }

    @Override
    public void instanceAfter() {
        //初始化比赛队列数据
        BattleAPI.getInstance();
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Battle.getOrder();
    }

}
