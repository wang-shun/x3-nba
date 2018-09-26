package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.TeamNumBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.TeamConsole;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.team.Team;
import com.ftkj.server.RedisKey;

import java.io.Serializable;

/** 球队次数及购买次数消耗 */
public class TeamNumManager extends BaseManager {
    @IOC
    private TeamMoneyManager moneyManager;

    @Override
    public void instanceAfter() {

    }

    /** 获取已经使用的次数 */
    public int getUsedNum(long teamId, TeamNumType nt) {
        TeamNum tn = getTeamNum(teamId, nt);
        return tn != null ? tn.getNum() : 0;
    }

    /** 相关功能是否还有剩余使用次数. true 有. */
    public boolean hasRemainNumber(Team team, TeamNumType type) {
        return remainNumber(team, type) > 0;
    }

    /** 相关功能是否还有剩余使用次数. true 有. */
    public boolean hasRemainNumber(long tid, TeamNumType type) {
        return remainNumber(tid, type) > 0;
    }

    /** 相关功能是否还有剩余使用次数. true 有. */
    public boolean hasRemainNumber(TeamNum tn) {
        return remainNumber(tn) > 0;
    }

    /** 获取指定功能的剩余次数 */
    public int remainNumber(Team team, TeamNumType type) {
        TeamNumBean tnb = TeamConsole.getNums(type);
        return remainNumber(team.getTeamId(), type, tnb);
    }

    /** 获取指定功能的剩余次数 */
    public int remainNumber(long teamId, TeamNumType type) {
        TeamNumBean tnb = TeamConsole.getNums(type);
        return remainNumber(teamId, type, tnb);
    }

    /** 获取指定功能的剩余次数 */
    public int remainNumber(TeamNum tn) {
        if (tn == null) {
            return 0;
        }
        TeamNumBean tnb = TeamConsole.getNums(tn.getType());
        if (tnb == null) {
            return 0;
        }
        return tnb.getMaxNum() - tn.getNum();
    }

    /** 获取指定功能的剩余次数 */
    private int remainNumber(long teamId, TeamNumType type, TeamNumBean info) {
        if (info == null) {
            return 0;
        }
        TeamNum tn = getTeamNum(teamId, info);
        return info.getMaxNum() - tn.getNum();
    }

    /** 获取指定功能的剩余次数 */
    public int remainNumber(TeamNum tn, TeamNumBean info) {
        return info.getMaxNum() - tn.getNum();
    }

    /** 次数+1 */
    public void addUseNum(TeamNum tn) {
        addUseNum(tn, 1);
    }

    /**
     * 次数+1
     */
    public void delUseNum(TeamNum tn) {
        addUseNum(tn, -1);
    }

    /** 获取次数信息 */
    public TeamNum getTeamNum(long teamId, TeamNumType type) {
        return getTeamNum(teamId, TeamConsole.getNums(type));
    }

    /** 获取次数信息 */
    public TeamNum getTeamNum(long teamId, TeamNumBean tnb) {
        if (tnb == null) {
            return null;
        }
        boolean dailyNum = tnb.isDailyReset();
        String key = cacheKey(dailyNum, teamId, tnb.getNumType());
        TeamNum data = redis.getObj(key);
        if (data == null) {
            data = new TeamNum(teamId);
            if (dailyNum) {
                redis.set(key, data, RedisKey.DAY7);
            } else {
                redis.set(key, data);
            }
        }
        return data;
    }

    /** 次数+1 */
    public void addUseNum(TeamNum tn, int addNum) {
        TeamNumBean tnb = TeamConsole.getNums(tn.getType());
        if (tnb == null) {
            return;
        }
        addUseNum(tnb, tn, addNum);
    }

    /** 增加次数 */
    private void addUseNum(TeamNumBean tnb, TeamNum tn, int addNum) {
        tn.setNum(tn.getNum() + addNum);
        tn.setNumCount(tn.getNumCount() + addNum);
        save(tnb, tn);
    }

    /** 保存 */
    private void save(TeamNumBean tnb, TeamNum tn) {
        boolean dailyNum = tnb.isDailyReset();
        String key = cacheKey(dailyNum, tn.getTeamId(), tnb.getNumType());
        if (dailyNum) {
            redis.set(key, tn, RedisKey.DAY7);
        } else {
            redis.set(key, tn);
        }
    }

    private String cacheKey(boolean dailyNum, long teamId, TeamNumType tnt) {
        return dailyNum ? RedisKey.getDayKey(teamId, RedisKey.Team_Num_Day) + "_" + tnt.getType()
                : RedisKey.Team_Num + teamId + "_" + tnt.getType();
    }

    /** 重置次数为0 */
    public void resetNumTo0(TeamNum tn) {
        if (tn == null || tn.getNum() <= 0) {
            return;
        }
        tn.setNum(0);
    }

    /**
     * 消耗次数, 并扣除相应的货币
     *
     * @param consNum 消耗的次数
     */
    public ErrorCode consumeNumCurrency(long teamId,
                                        TeamNumType type,
                                        int consNum,
                                        ModuleLog om) {
        TeamNumBean tnb = TeamConsole.getNums(type);
        if (tnb == null) {
            return ErrorCode.Team_Num_Bean_Null;
        }
        TeamNum tn = getTeamNum(teamId, tnb);
        if (tn.getNum() >= tnb.getMaxNum()) {
            return ErrorCode.Team_Number_Max;
        }
        int remainNum = tnb.getMaxNum() - tn.getNum();
        if (remainNum < consNum) {
            return ErrorCode.Team_Number_Remain;
        }
        int oldNum = tn.getNum();
        int num = oldNum + consNum;
        int currency = 0;
        for (int i = oldNum + 1; i <= num; i++) {
            currency += tnb.getCurrency(i);
        }
        //扣除消耗
        if (currency > 0) {
            final TeamMoney money = moneyManager.getTeamMoney(teamId);
            ErrorCode ret = moneyManager.validateSub(money, tnb.getCurrencyType(), currency);
            if (ret.isError()) {
                return ret;
            }
            moneyManager.sub(money, tnb.getCurrencyType(), currency, om);
        }
        //增加次数
        addUseNum(tnb, tn, consNum);
        return ErrorCode.Success;
    }

    /** gm 重置次数 */
    public ErrorCode gmResetNum(long teamId, int nt, int currNum) {
        TeamNumType type = TeamNumType.convertByType(nt);
        if (type == null) {
            return ErrorCode.Team_Number_Type;
        }
        TeamNumBean tnb = TeamConsole.getNums(type);
        if (tnb == null) {
            return ErrorCode.Team_Num_Bean_Null;
        }
        TeamNum tn = getTeamNum(teamId, tnb);
        tn.setNum(currNum);
        save(tnb, tn);
        return ErrorCode.Success;
    }

    /**
     * 球队次数.
     *
     * @author luch
     */
    public static final class TeamNum implements Serializable {
        private static final long serialVersionUID = 7574219342383846870L;
        private long teamId;
        /** 类型 */
        private TeamNumType type;
        /** 当前次数 */
        private int num;
        /** 累计次数 */
        private long numCount;

        public TeamNum(long teamId) {
            this.teamId = teamId;
        }

        public long getTeamId() {
            return teamId;
        }

        public void setTeamId(long teamId) {
            this.teamId = teamId;
        }

        public TeamNumType getType() {
            return type;
        }

        public void setType(TeamNumType type) {
            this.type = type;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public long getNumCount() {
            return numCount;
        }

        public void setNumCount(long numCount) {
            this.numCount = numCount;
        }
    }

}
