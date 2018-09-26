package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.db.ao.logic.ITacticsAO;
import com.ftkj.db.domain.TacticsPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ETeamConfig;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.tactics.Tactics;
import com.ftkj.manager.tactics.TeamTactics;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.TacticsPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.CastUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年3月6日
 * 战术管理
 */
public class TacticsManager extends BaseManager implements OfflineOperation {

    private Map<Long, TeamTactics> tacticsMap;

    @IOC
    private ITacticsAO tacticsAO;

    @IOC
    private TeamManager teamMamager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamConfigManager teamConfigManager;
    @IOC
    private TaskManager taskManager;

    public TeamTactics getTeamTactics(long teamId) {
        TeamTactics tt = tacticsMap.get(teamId);
        if (tt == null) {
            List<TacticsPO> tlist = tacticsAO.getTacticsList(teamId);
            if (tlist == null) {
                tlist = Lists.newArrayList();
            }
            tt = new TeamTactics(teamId, tlist.stream().map(t -> new Tactics(t)).collect(Collectors.toList()));
            tacticsMap.put(teamId, tt);
        }
        return tt;
    }

    /**
     * 创建球队默认战术	, 只有创建球队调用
     *
     * @param teamId
     * @return
     */
    public TeamTactics createTeamDefaultTactics(long teamId) {
        TeamTactics tt = TeamTactics.createTeamTactics(teamId, TacticsConsole.getDefaultStudy());
        this.tacticsMap.put(teamId, tt);
        return tt;
    }

    @Override
    public void instanceAfter() {
        this.tacticsMap = Maps.newConcurrentMap();
    }

    @Override
    public void initConfig() {
       NPCConsole.getNpcs().values().forEach(npc -> {
            TeamTactics tt = TeamTactics.createTactics(npc.getNpcId(), Lists.newArrayList(TacticId.values()), npc.getTacticsLevel());
            this.tacticsMap.put(npc.getNpcId(), tt);
        });

    }

    /**
     * 取用户设定的默认战术
     *
     * @param type
     * @return
     */
    public TacticId getDefaultTactics(long teamId, TacticType type) {
        if (TacticType.Offense == type) {
            int id = CastUtil.StringCastInt(teamConfigManager.getData(teamId, ETeamConfig.默认进攻战术));
            return TacticId.checkId(id) ? TacticId.convert(id) : TacticId.强攻内线;
        }
        // 防守
        int id = CastUtil.StringCastInt(teamConfigManager.getData(teamId, ETeamConfig.默认防守战术));
        return TacticId.checkId(id) ? TacticId.convert(id) : TacticId.内线防守;
    }

    /**
     * 主界面打开
     */
    @ClientMethod(code = ServiceCode.Tactics_List)
    public void showView() {
        long teamId = getTeamId();
        TeamTactics teamTactics = getTeamTactics(teamId);
        sendMessage(TacticsPB.TeamTacticsData.newBuilder()
                .setDefJg(getDefaultTactics(teamId, TacticType.Offense).getId())
                .setDefFs(getDefaultTactics(teamId, TacticType.Defense).getId())
                .addAllTacticsList(getTacticsData(teamTactics.getTacticsMap().values()))
                .build());
    }

    private List<TacticsPB.TacticsData> getTacticsData(Collection<Tactics> collection) {
        List<TacticsPB.TacticsData> list = Lists.newArrayList();
        for (Tactics t : collection) {
            list.add(TacticsPB.TacticsData.newBuilder()
                    .setId(t.getTactics().getId())
                    .setLv(t.getLevel())
                    .setBuffTime(t.getBuffTime().getMillis())
                    .build());
        }
        return list;
    }

    public String getBattleTacticsConfig(long teamId) {
        TeamTactics tt = getTeamTactics(teamId);
        return tt.getTacticsMap().values().stream().map(tac -> "" + tac.getTactics().getId()).collect(Collectors.joining(","));
    }

    /**
     * 学习战术
     *
     * @param id
     */
    @ClientMethod(code = ServiceCode.Tactics_Study)
    public void studyTactics(int id) {
        long teamId = getTeamId();
        TeamTactics teamTactics = getTeamTactics(teamId);
        if (!Arrays.stream(TacticId.values()).anyMatch(a -> a.getId() == id)) {
            log.debug("参数错误");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        TacticId studyTactics = TacticId.convert(id);
        Team team = teamMamager.getTeam(teamId);
        Collection<Tactics> list = teamTactics.getTacticsMap().values();
        int needLv = TacticsConsole.getStudyLv(studyTactics, list);
        if (team.getLevel() < needLv) {
            log.debug("等级不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
            return;
        }
        //		TeamMoney teamMoney = teamMoneyManager.getTeamMoney(teamId);
        int needGold = TacticsConsole.getStudyMoney(TacticId.convert(id), list);
        //		if(teamMoney.getGold() < needGold) {
        //			log.debug("经费不足", needGold);
        //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
        //			return;
        //		}
        if (needGold > 0 && propManager.delProp(teamId, 1201, needGold, true, true) == null) {
            log.debug("战术点不足", needGold);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
            return;
        }
        // 扣掉数据，学习
        //		teamMoneyManager.updateTeamMoney(teamId, 0, -needGold, 0, 0, true, EModuleCode.球队);
        teamTactics.studyTactics(studyTactics);
        taskManager.updateTask(teamId, ETaskCondition.学习战术, 1, EModuleCode.战术.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

    }

    /**
     * 升级
     */
    @ClientMethod(code = ServiceCode.Tactics_Up)
    public void upLv(int id) {
        long teamId = getTeamId();
        TeamTactics teamTactics = getTeamTactics(teamId);
        TacticId eTactics = TacticId.convert(id);
        Tactics tactics = teamTactics.getTactics(eTactics);
        // 没有学习过
        if (tactics == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        // 已是最大等级
        if (tactics.getLevel() >= TacticsConsole.Max_Lv) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
            return;
        }
        PropSimple needProp = TacticsConsole.getUpLvPropNum(tactics.getLevel(), 0);
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(needProp)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        propManager.delProp(teamId, needProp, true, false);
        // 升级
        int level = teamTactics.upLv(eTactics);
        taskManager.updateTask(teamId, ETaskCondition.升级战术, 1, "" + level);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 突破，续期
     */
    @ClientMethod(code = ServiceCode.Tactics_Buff)
    public void buffTime(int id, int day) {
        // 10级
        long teamId = getTeamId();
        TeamTactics teamTactics = getTeamTactics(teamId);
        TacticId eTactics = TacticId.convert(id);
        Tactics tactics = teamTactics.getTactics(eTactics);
        //
        int index = -1;
        for (int i = 0; i < TacticsConsole.Buff_Day.length; i++) {
            if (day == TacticsConsole.Buff_Day[i]) {
                index = i;
                break;
            }
        }
        // 非法天数
        if (index == -1) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        // 没有学习过
        if (tactics == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        // 10级的时候可以突破，11级时（已突破）可以续期
        if (tactics.getLevel() < 10) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        PropSimple needProp = TacticsConsole.getUpLvPropNum(tactics.getLevel(), index);
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(needProp)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        // 删除道具
        propManager.delProp(teamId, needProp, true, false);
        long buffTime = teamTactics.addBuff(eTactics, day);
        taskManager.updateTask(teamId, ETaskCondition.升级战术, 1, "" + tactics.getLevel());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(buffTime).build());
    }

    /**
     * 设置默认战术
     *
     * @param type 进攻1  防守2
     * @param id   战术ID
     */
    @ClientMethod(code = ServiceCode.Tactics_Setting)
    public void setDefaultTactics(int type, int id) {
        long teamId = getTeamId();
        if (type < 1 || type > 2) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        if (!TacticId.checkId(id)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        if (TacticId.convert(id).getTypeInt() != type) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        TeamTactics teamTactics = getTeamTactics(teamId);
        if (teamTactics.getTactics(TacticId.convert(id)) == null) {
            log.debug("设置默认技能不存在[{}]-[{}]", teamId, id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        if (type == TacticType.Offense.getType()) {
            teamConfigManager.setData(teamId, ETeamConfig.默认进攻战术, id + "");
        } else if (type == TacticType.Defense.getType()) {
            teamConfigManager.setData(teamId, ETeamConfig.默认防守战术, id + "");
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @Override
    public void offline(long teamId) {
        tacticsMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        tacticsMap.remove(teamId);
    }
}
