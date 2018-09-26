package com.ftkj.manager.logic;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITeamAO;
import com.ftkj.db.domain.MoneyPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LevelupParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.User;
import com.ftkj.manager.logic.log.GameMoneyLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamGrade;
import com.ftkj.manager.team.api.LevelAPI;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.TeamPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年3月3日
 * 球队货币管理
 */
public class TeamMoneyManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(TeamMoneyManager.class);
    private Map<Long, TeamMoney> teamMoneyMap;
    @IOC
    private TeamManager teamManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private TaskManager taskManager;  
    @IOC
    private ITeamAO teamAO;
    @IOC
    private PropManager propManager;

    public TeamMoney getTeamMoney(long teamId) {
        TeamMoney tm = this.teamMoneyMap.get(teamId);
        if (tm == null) {
            MoneyPO po = teamAO.getTeamMoney(teamId);
            if (po == null) {
                po = new MoneyPO(teamId);
            }
            tm = new TeamMoney(po);
            this.teamMoneyMap.put(teamId, tm);
        }
        return tm;
    }

    /**
     * 更新货币信息
     *
     * @param teamId
     * @param money
     * @param gold
     * @param exp
     * @return true 扣费成功，false扣费失败
     */
    public boolean updateTeamMoney(long teamId, int money, int gold, int exp, int bdMoney, boolean send, ModuleLog module) {
        TeamMoney tm = getTeamMoney(teamId);
        return updateTeamMoney(tm, money, gold, exp, bdMoney, send, module);
    }

    /**
     * 扣费,返回true 扣费成功，false扣费失败
     *
     * @param tm
     * @param money
     * @param gold
     * @param exp
     * @param bdMoney
     * @param send
     * @param module
     * @return
     */
    public boolean updateTeamMoney(TeamMoney tm, int money, int gold, int exp, int bdMoney, boolean send, ModuleLog module) {
        //假如有扣除操作，需检查是否足够，不足返回false
        if (!checkTeamMoney(tm, money, gold, exp, bdMoney)) {
            return false;
        }
        //
        updateTeamMoneyUnCheck(tm, money, gold, exp, bdMoney, send, module);
        return true;
    }

    // 最终调用
    public void updateTeamMoneyUnCheck(TeamMoney tm, int money, int gold, int exp, int bdMoney, boolean send, ModuleLog module) {
        if(GameSource.isNPC(tm.getTeamId())){
            return;
        }
        final int srcm = tm.getMoney();
        final int srcg = tm.getGold();
        final int srce = tm.getExp();
        final int srcbd = tm.getBdMoney();
        // 经验值可能要传入计算后的剩余经验，不是直接的计算
        tm.updateMoney(EMoneyType.Money, money)
                .updateMoney(EMoneyType.Gold, gold)
                .updateMoney(EMoneyType.Exp, exp)
                .updateMoney(EMoneyType.Bind_Money, bdMoney)
                .save();
        //推送货币变动包
        if (send) {
            sendMessage(tm.getTeamId(), getTeamMoneyData(tm), ServiceCode.Push_Money);
        }
        // 加上经验判断是否升级
        if (exp != 0) {
            upgradeLevByExp(tm, exp);
        }
        if (money != 0) {
            GameMoneyLogManager.Log(tm.getTeamId(), EMoneyType.Money, money, tm.getMoney(), module);
        }
        if (gold != 0) {
            GameMoneyLogManager.Log(tm.getTeamId(), EMoneyType.Gold, gold, tm.getGold(), module);
        }
        if (exp != 0) {
            GameMoneyLogManager.Log(tm.getTeamId(), EMoneyType.Exp, exp, tm.getExp(), module);
        }
        if (bdMoney != 0) {
            GameMoneyLogManager.Log(tm.getTeamId(), EMoneyType.Bind_Money, bdMoney, tm.getBdMoney(), module);
        }
        //
        if (money > 0) {
            taskManager.updateTask(tm.getTeamId(), ETaskCondition.充值RMB, money, EModuleCode.球队.getName());
        }
        log.debug("tmoney up. tid {} module {} money {}+{}->{} gold {}+{}->{} exp {}+{}->{} bdmoney {}+{}->{}",
                tm.getTeamId(), module.getName(), srcm, money, tm.getMoney(), srcg, gold, tm.getGold(),
                srce, exp, tm.getExp(), srcbd, bdMoney, tm.getBdMoney());
    }

    /** 升级 */
    private void upgradeLevByExp(TeamMoney tm, int exp) {
        if (exp <= 0) {
            return;
        }
        Team team = teamManager.getTeam(tm.getTeamId());
        int startLevel = team.getLevel();
        TeamGrade teamGrade = LevelAPI.checkTeamGrade(startLevel, tm.getExp());
        if (teamGrade != null) {
            // 升级
            team.setLevel(teamGrade.getGrade());
            team.updatePrice(teamGrade.getPrice());
            if (team.getLevel() >= 10 && team.getLineupCount() == 2) {
                team.updateLineupCount(3);
            } else if (team.getLevel() >= 20 && team.getLineupCount() == 3) {
                team.updateLineupCount(4);
            }
            team.save();
            EventBusManager.post(EEventType.球队升级, new LevelupParam(team.getTeamId(), team.getLevel()));
            // 推送升级报
            sendMessage(tm.getTeamId(), DefaultData.newBuilder().setBigNum(team.getLevel()).build(), ServiceCode.Team_Up_Level);
            rankManager.updateTeamLevel(tm.getTeamId(), team.getLevel());
            taskManager.levelup(tm.getTeamId(), startLevel, team.getLevel());
            taskManager.updateTask(tm.getTeamId(), ETaskCondition.球队等级, team.getLevel(), EModuleCode.球队.getName());
            leagueManager.updateTeamLevel(team.getTeamId(), teamGrade.getGrade());
            propManager.addPropList(tm.getTeamId(), teamGrade.getAwardList(), true, ModuleLog.getModuleLog(EModuleCode.球员升级, ""));
            User user = GameSource.getUser(tm.getTeamId());
            if (user != null) {
                user.setVal(User.Level_Key, team.getLevel() + "");
            }
        }
    }

    /**
     * 更新货币.
     *
     * @param tid       球队id
     * @param type      货币类型
     * @param value     更新的货币数量(正加负减)
     * @param broadcast 是否向客户端广播
     * @param module    触发的模块(操作日志用)
     * @return true 扣除成功
     */
    public void update(long tid, EMoneyType type, int value, boolean broadcast, ModuleLog module) {
        TeamMoney tm = getTeamMoney(tid);
        if (tm == null) {
            return;
        }
        update(tm, type, value, broadcast, module);
    }

    /**
     * 更新货币, 向客户端广播.
     *
     * @param tm
     * @param type   货币类型
     * @param value  更新的货币数量(正加负减)
     * @param module 触发的模块(操作日志用)
     * @return true 扣除成功
     */
    public void update(TeamMoney tm, EMoneyType type, int value, ModuleLog module) {
        update(tm, type, value, true, module);
    }

    /**
     * 更新货币.
     *
     * @param tm
     * @param type      货币类型
     * @param value     更新的货币数量(正加负减)
     * @param broadcast 是否向客户端广播
     * @param module    触发的模块(操作日志用)
     * @return true 扣除成功
     */
    public void update(TeamMoney tm, EMoneyType type, int value, boolean broadcast, ModuleLog module) {
        if (value == 0) {
            return;
        }
        if(GameSource.isNPC(tm.getTeamId())){
            return;
        }
        switch (type) {
            case Money:
                updateTeamMoneyUnCheck(tm, value, 0, 0, 0, broadcast, module);
                break;
            case Bind_Money:
                updateTeamMoneyUnCheck(tm, 0, 0, 0, value, broadcast, module);
                break;
            case Gold:
                updateTeamMoneyUnCheck(tm, 0, value, 0, 0, broadcast, module);
                break;
            case Exp:
                updateTeamMoneyUnCheck(tm, 0, 0, value, 0, broadcast, module);
                break;
            default:
                break;
        }
    }

    /**
     * 扣除的货币, 向客户端广播.
     *
     * @param tm
     * @param type   货币类型
     * @param value  要扣除的货币数量 > 0
     * @param module 触发的模块(操作日志用)
     * @return true 扣除成功
     */
    public void sub(TeamMoney tm, EMoneyType type, int value, ModuleLog module) {
        sub(tm, type, value, true, module);
    }

    /**
     * 扣除的货币
     *
     * @param tm
     * @param type      货币类型
     * @param value     要扣除的货币数量 > 0
     * @param broadcast 是否向客户端广播
     * @param module    触发的模块(操作日志用)
     * @return true 扣除成功
     */
    public void sub(TeamMoney tm, EMoneyType type, int value, boolean broadcast, ModuleLog module) {
        if (value == 0 || value < 0) {
            return;
        }
        if(GameSource.isNPC(tm.getTeamId())){
            return;
        }
        switch (type) {
            case Money:
                updateTeamMoneyUnCheck(tm, -value, 0, 0, 0, broadcast, module);
                break;
            case Bind_Money:
                updateTeamMoneyUnCheck(tm, 0, 0, 0, -value, broadcast, module);
                break;
            case Gold:
                updateTeamMoneyUnCheck(tm, 0, -value, 0, 0, broadcast, module);
                break;
            case Exp:
                updateTeamMoneyUnCheck(tm, 0, 0, -value, 0, broadcast, module);
                break;
            default:
                break;
        }
    }

    /**
     * 校验要扣除的货币是否足够
     *
     * @param tm
     * @param type  货币类型
     * @param value 要扣除的货币数量 > 0
     * @return true 货币足够
     */
    public ErrorCode validateSub(TeamMoney tm, EMoneyType type, int value) {
        if (value == 0) {
            return ErrorCode.Success;
        }
        if (value < 0) {
            return ErrorCode.Error;
        }
        boolean ret = value <= tm.getMoney(type);
        if (!ret) {
            switch (type) {
                case Money: return ErrorCode.Money_1;
                case Bind_Money: return ErrorCode.Money_3;
                case Gold: return ErrorCode.Money_0;
                case Exp: return ErrorCode.Money_Exp;
                default: return ErrorCode.Money_Common;
            }
        }
        return ErrorCode.Success;
    }

    /**
     * 校验要扣除的货币是否足够
     */
    public boolean checkTeamMoney(TeamMoney tm, int money, int gold, int exp, int bdMoney) {
        //假如有扣除操作，需检查是否足够，不足返回false
        if (money < 0 && tm.getMoney() < Math.abs(money)) {
            return false;
        }
        if (gold < 0 && tm.getGold() < Math.abs(gold)) {
            return false;
        }
        if (exp < 0 && tm.getExp() < Math.abs(exp)) {
            return false;
        }
        if (bdMoney < 0 && tm.getBdMoney() < Math.abs(bdMoney)) {
            return false;
        }
        return true;
    }

    public TeamPB.TeamMoneyData getTeamMoneyData(TeamMoney tm) {
        return TeamPB.TeamMoneyData.newBuilder()
                .setExp(tm.getExp())
                .setGold(tm.getGold())
                .setMoney(tm.getMoney())
                .setBdMoney(tm.getBdMoney())
                .build();
    }

    @Override
    public void instanceAfter() {
        teamMoneyMap = Maps.newConcurrentMap();
    }

    @Override
    public void offline(long teamId) {
        teamMoneyMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamMoneyMap.remove(teamId);
    }
}
