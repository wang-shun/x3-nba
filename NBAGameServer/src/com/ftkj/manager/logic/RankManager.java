package com.ftkj.manager.logic;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jredis.ri.alphazero.support.DefaultCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.ERank;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.cap.Cap;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.rank.LeagueRank;
import com.ftkj.manager.rank.Rank;
import com.ftkj.manager.rank.TeamPowerRank;
import com.ftkj.manager.rank.TeamRank;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.RankPB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.lambda.LBInt;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import redis.clients.jedis.Jedis;

/**
 * @author tim.huang
 * 2017年6月1日
 * 排行榜
 */
public class RankManager extends AbstractBaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(RankManager.class);
    @IOC
    private TeamManager teamManager;
    @IOC
    private LeagueManager leagueManager;  
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamCapManager teamCapManager;  

    private Set<Long> teamCaps = Sets.newConcurrentHashSet();
    //	private int _minTeamLevel;
    //	private int _minTeamPower;

    @ClientMethod(code = ServiceCode.RankManager_showTeamLevelRank)
    public void showTeamLevelRank(int page) {
        long teamId = getTeamId();
        int[] tmpTween = getPage(page);
        List<TeamRank> ranksList = getRankList(ERank.Team_Lev, tmpTween[0], tmpTween[1]);
        List<RankPB.TeamLevelRankData> dataList = Lists.newArrayList();
        LBInt i = new LBInt(tmpTween[0]);
        int myRank = getRank(teamId, ERank.Team_Lev) + 1;

        TeamRank myTeam = getRankInfo(ERank.Team_Lev, teamId);
        ranksList.forEach(r -> dataList.add(getTeamLevelRank(r, i.increaseAndGet())));
        RankPB.TeamLevelRankMainData.Builder builder = RankPB.TeamLevelRankMainData.newBuilder().addAllRankList(dataList);
        if (myTeam != null) {
            builder.setMy(getTeamLevelRank(myTeam, myRank));
        }
        sendMessage(builder.build());
    }

    /**
     * 玩家战力排行榜
     */
    @ClientMethod(code = ServiceCode.Rank_Team_Cap_Rank)
    public void showTeamPowerRank(int page) {
        long teamId = getTeamId();
        int[] startAndEnd = getPage(page);
        List<TeamPowerRank> ranksList = getRankList(ERank.Team_Cap, startAndEnd[0], startAndEnd[1]);
        List<RankPB.TeamPowerRankData> dataList = Lists.newArrayList();
        LBInt start = new LBInt(startAndEnd[0]);
        int myRank = getRank(teamId, ERank.Team_Cap) + 1;

        TeamPowerRank myTeam = getRankInfo(ERank.Team_Cap, teamId);
        ranksList.forEach(r -> dataList.add(getTeamPowerRankData(r, start.increaseAndGet())));
        RankPB.TeamPowerRankMainData.Builder builder = RankPB.TeamPowerRankMainData.newBuilder().addAllRankList(dataList);
        if (myTeam != null) {
            builder.setMy(getTeamPowerRankData(myTeam, myRank));
        }
        sendMessage(builder.build());
    }

    /**
     * 玩家联盟排行榜
     */
    @ClientMethod(code = ServiceCode.RankManager_showLeagueRank)
    public void showLeagueRank(int page) {
        long teamId = getTeamId();
        int[] tmpTween = getPage(page);
        List<LeagueRank> leagueRanks = getRankList(ERank.League, tmpTween[0], tmpTween[1]);
        //Redis中查询出来的是按score降序排序,在对rankList排序根据联盟等级,贡献值排序
        Collections.sort(leagueRanks);
        List<RankPB.LeagueLevelRankData> ranks = Lists.newArrayList();
        LBInt i = new LBInt(tmpTween[0]);
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        leagueRanks.forEach(r -> ranks.add(getLeagueLevelRankData(r, i.increaseAndGet())));
        RankPB.LeagueLevelRankMainData.Builder builder = RankPB.LeagueLevelRankMainData.newBuilder()
                .addAllRankList(ranks);
        if (lt != null) {
            int myRank = getRank(lt.getLeagueId(), ERank.League) + 1;
            LeagueRank myTeam = getRankInfo(ERank.League, lt.getLeagueId());
            if (myTeam != null) { builder.setMy(getLeagueLevelRankData(myTeam, myRank)); }
        }

        sendMessage(builder.build());
    }

    /**
     * 根据联盟名字查询信息
     */
    @ClientMethod(code = ServiceCode.RankManager_seachLeagueRank)
    public void seachLeagueRank(String leagueName) {
        int seachLeagueId = leagueManager.getLeagueId(leagueName);
        int myRank = getRank(seachLeagueId, ERank.League) + 1;
        LeagueRank myTeam = getRankInfo(ERank.League, seachLeagueId);
        sendMessage(RankPB.LeagueLevelRankMainData.newBuilder().addRankList(getLeagueLevelRankData(myTeam, myRank)).build());
    }

    void updateLeagueRank(League l, int totalLevel) {
        int leagueId = l.getLeagueId();
        LeagueRank myTeam = getRankInfo(ERank.League, leagueId);
        if (myTeam == null) {
            String leagueName = leagueManager.getLeagueName(leagueId);
            myTeam = new LeagueRank(leagueId, leagueName, l.getLeagueLevel(), totalLevel, l.getPeopleCount(), l.getScore());
        }

        myTeam.updateScore(l.getLeagueLevel(), totalLevel, l.getScore());
        saveRankInfo(ERank.League, myTeam, leagueId);
        updateRank(leagueId, myTeam.getScore(), ERank.League, false);
    }

    void updateLeaguePeople(int leagueId, int val) {
        LeagueRank myTeam = getRankInfo(ERank.League, leagueId);
        if (myTeam == null) { return; }
        myTeam.setPeopleCount(myTeam.getPeopleCount() + val);
        saveRankInfo(ERank.League, myTeam, leagueId);
    }

    void teamLogin(long teamId) {
        teamCaps.add(teamId);
    }

    void gmRecalcTeamCap(long teamId) {
        teamCaps.add(teamId);
        updateTeamCapTask();
    }

    //	public int getMin(ERank type){
    //		if(type == ERank.等级){
    //			return this._minTeamLevel;
    //		}else if(type == ERank.攻防){
    //			return this._minTeamPower;
    //		}
    //		return 0;
    //	}

    public synchronized void updateTeamCapTask() {
        Set<Long> teams = teamCaps;
        teamCaps = Sets.newConcurrentHashSet();
        //		long tid = 10100000500028l;
        //		updateTeamPower(tid,teamManager.getTeamAllAbility(tid).getCap()
        //				, teamManager.getTeamLineUpAbility(tid).getCap());
        for (long tid : teams) {
            try {
                TeamCap tc = calcTeamCap(tid);
                updateTeamCap(tid, tc.getTotalCap(), tc.getOcap(), tc.dcap);
            } catch (Exception e) {
                log.error("更新玩家攻防信息排行榜异常:[{}]", tid);
                log.error(e.getMessage(), e);
            }
        }
    }

    /** 计算球队实时最终战力 */
    public TeamCap calcTeamCap(long tid) {
        TeamCap tc = new TeamCap();
        Cap teamcap = teamCapManager.getTeamOtherCap(tid).getCap();//场下
        Cap startingCap = teamCapManager.getStartingTotalCap(tid);
        Cap benchCap = teamCapManager.getBenchTotalCap(tid);
        boolean overflow = teamManager.isSalaryOverflow(teamManager.getTeam(tid), playerManager.getTeamPlayer(tid));
        if (overflow) {
            tc.overflow = true;
            float capRate = ConfigConsole.global().overSalary;
            tc.ocap = (int) ((startingCap.getOcap() + teamcap.getOcap()) * capRate);
            tc.dcap = (int) ((startingCap.getDcap() + teamcap.getDcap()) * capRate);
            tc.totalCap = (int) ((startingCap.getTotalCap() + teamcap.getTotalCap() + benchCap.getTotalCap()) * capRate);
        } else {
            tc.ocap = (int) (startingCap.getOcap() + teamcap.getOcap());
            tc.dcap = (int) (startingCap.getDcap() + teamcap.getDcap());
            tc.totalCap = (int) (startingCap.getTotalCap() + teamcap.getTotalCap() + benchCap.getTotalCap());
        }
        log.debug("rank. tid {} calc cap {} o {} team {} starting {} bench {}",
                tid, tc, overflow, teamcap, startingCap, benchCap);
        return tc;
    }

    /** 球队战力信息 */
    public static final class TeamCap {
        /** 是否超帽 */
        private boolean overflow;
        /** 球队总战力. 包括阵容球员战力和球队战力 */
        private int totalCap;
        /** 阵容首发进攻战力 + 球队场下进攻战力 */
        private int ocap;
        /** 阵容首发防守战力 + 球队场下防守战力 */
        private int dcap;

        public boolean isOverflow() {
            return overflow;
        }

        public int getTotalCap() {
            return totalCap;
        }

        public int getOcap() {
            return ocap;
        }

        public int getDcap() {
            return dcap;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"overflow\":" + overflow +
                    ", \"totalCap\":" + totalCap +
                    ", \"ocap\":" + ocap +
                    ", \"dcap\":" + dcap +
                    '}';
        }
    }

    /**
     * 更新球队战力排行榜
     *
     * @param srcCap        原始攻防
     * @param finalTotalCap 最终攻防. 包括球队战力和阵容球员战力
     * @param ocap          阵容首发进攻战力 + 球队场下进攻战力
     * @param dcap          阵容首发防守战力 + 球队场下防守战力
     */
    private void updateTeamCap(long teamId, int finalTotalCap, int ocap, int dcap) {
        TeamPowerRank tpr = getRankInfo(ERank.Team_Cap, teamId);
        if (tpr == null) {
            LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
            Team team = teamManager.getTeam(teamId);
            int leagueId = lt == null ? 0 : lt.getLeagueId();
            tpr = new TeamPowerRank(teamId, team.getName(), team.getLogo(),
                    leagueManager.getLeagueName(leagueId), finalTotalCap, ocap, dcap);
        }
        tpr.updateScore(finalTotalCap, ocap, dcap);
        saveRankInfo(ERank.Team_Cap, tpr, teamId);
        updateRank(teamId, tpr.getScore(), ERank.Team_Cap, false);        
    }

    void updateTeamNameCap(long teamId, String teamName) {
        TeamPowerRank tpr = getRankInfo(ERank.Team_Cap, teamId);
        if (tpr == null) {
            return;
        }

        tpr.setName(teamName);
        saveRankInfo(ERank.Team_Cap, tpr, teamId);
    }

    void updateTeamLevel(long teamId, int level) {
        //		int minLevel = getMin(ERank.等级);
        //		if(level>=minLevel){
        TeamRank myTeam = getRankInfo(ERank.Team_Lev, teamId);
        if (myTeam == null) {
            LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
            Team team = teamManager.getTeam(teamId);
            int leagueId = lt == null ? 0 : lt.getLeagueId();
            myTeam = new TeamRank(teamId, team.getName(), team.getLogo(),
                    team.getLevel(), leagueManager.getLeagueName(leagueId));
        }
        if (level < myTeam.getLevel()) {
            return;// 不超过当前最高等级，不做统计
        }
        myTeam.updateScore(level);
        saveRankInfo(ERank.Team_Lev, myTeam, teamId);
        updateRank(teamId, myTeam.getScore(), ERank.Team_Lev, false);
        //		}
    }

    void updateTeamNameLev(long teamId, String teamName) {
        TeamRank myTeam = getRankInfo(ERank.Team_Lev, teamId);
        if (myTeam == null) {
            return;
        }

        myTeam.setName(teamName);

        saveRankInfo(ERank.Team_Lev, myTeam, teamId);
    }

    void updateTeamLeagueName(long teamId, String leagueName) {
        updateTeamLevelLeagueName(teamId, leagueName);
        updateTeamPowerLeagueName(teamId, leagueName);
    }

    private void updateTeamLevelLeagueName(long teamId, String leagueName) {
        TeamRank myTeam = getRankInfo(ERank.Team_Lev, teamId);
        if (myTeam == null) {
            return;
        }
        myTeam.setLeagueName(leagueName);
        saveRankInfo(ERank.Team_Lev, myTeam, teamId);
    }

    private void updateTeamPowerLeagueName(long teamId, String leagueName) {
        TeamPowerRank myTeam = getRankInfo(ERank.Team_Cap, teamId);

        if (myTeam == null) {
            return;
        }
        myTeam.setLeagueName(leagueName);
        saveRankInfo(ERank.Team_Cap, myTeam, teamId);
    }

    private RankPB.LeagueLevelRankData getLeagueLevelRankData(LeagueRank league, int rank) {

        return RankPB.LeagueLevelRankData.newBuilder()
                .setLeagueId(league.getLeagueId())
                .setLeagueLevel(league.getLevel())
                .setLeagueName(league.getName())
                .setPeopleCount(league.getPeopleCount())
                .setTotalLevel(league.getTotalLevel())
                .setRank(rank)
                .build();
    }

    private RankPB.TeamPowerRankData getTeamPowerRankData(TeamPowerRank team, int rank) {
        return RankPB.TeamPowerRankData.newBuilder().setLeagueName(team.getLeagueName())
                .setLogo(team.getLogo())
                .setName(team.getName())
                .setRank(rank)
                .setTeamId(team.getTeamId())
                .setTotalPower(team.getTotalCap())
                .setLineupAttackPower(team.getOcap())
                .setLineupDefendPower(team.getDcap())
                .build();
    }

    private RankPB.TeamLevelRankData getTeamLevelRank(TeamRank team, int rank) {
        return RankPB.TeamLevelRankData.newBuilder().setLeagueName(team.getLeagueName())
                .setLevel(team.getLevel())
                .setLogo(team.getLogo())
                .setName(team.getName())
                .setRank(rank)
                .setTeamId(team.getTeamId())
                .build();
    }

    /**
     * 更新排名数据
     */
    private <K extends Serializable> void updateRank(K key, double score, ERank type, boolean max) {
        String jKey = RedisKey.Rank_Sort + type.getType();
        if (max) {// 要判断分数
            double cur = redis.getSetScore(jKey, key);
            if (cur >= score) {
                return;
            }
        }
        redis.zadd(jKey, score, key);
    }

    /**
     * 获取战力排行榜战力或实时战力
     */
    public int getRankCapOrCalcedCap(long tid) {
        int rankcap = getTeamCap(tid);
        if (rankcap > 0) {
            return rankcap;
        }
        TeamCap tc = calcTeamCap(tid);
        return tc.getTotalCap();
    }

    /**
     * 更新排名数据
     */
    private int getTeamCap(long tid) {
        ERank type = ERank.Team_Cap;
        String jKey = RedisKey.Rank_Sort + type.getType();
        Double cap = redis.zscore(jKey, String.valueOf(tid));
        return cap != null ? cap.intValue() : 0;
    }

    //    /**
    //     * 增加排名权重
    //     */
    //    public <K extends Serializable> void increaseRankSource(K vaule, ERank type, int val) {
    //        byte[] keys = DefaultCodec.encode(RedisKey.Rank_Sort + type.getType());
    //        byte[] values = DefaultCodec.encode(vaule + "");
    //        try (Jedis j = redis.getJedis()) {
    //            j.zincrby(keys, val, values);
    //        }
    //    }

    <T extends Rank> List<T> getRankList(ERank rankType, int start, int end) {
        List<T> rankList = Lists.newArrayList();
        try (Jedis j = redis.getJedis()) {
            //取start到end的数据
            byte[] keys = DefaultCodec.encode(RedisKey.Rank_Sort + rankType.getType());
            byte[] mapKeys = DefaultCodec.encode(RedisKey.Rank_Info + rankType.getType());
            List<byte[]> source = Lists.newArrayList(j.zrevrange(keys, start, end));
            if (source.size() <= 0) {
                return rankList;
            }
            //从map中取对应的数据

            byte[][] fileds = new byte[source.size()][];
            for (int i = 0; i < source.size(); i++) {
                fileds[i] = source.get(i);
            }
            List<byte[]> infoList = j.hmget(mapKeys, fileds);
            infoList = infoList.stream().filter(info -> info != null).collect(Collectors.toList());
            rankList = DefaultCodec.decode(infoList);
        }
        return rankList;
    }

    private <T extends Rank, K extends Serializable> T getRankInfo(ERank type, K key) {
        byte[] mKey = DefaultCodec.encode(RedisKey.Rank_Info + type.getType());
        byte[] fieldKey = DefaultCodec.encode(key);
        return redis.hget(mKey, fieldKey);
    }

    private <T extends Rank, K extends Serializable> void saveRankInfo(ERank type, T t, K key) {
        try (Jedis j = redis.getJedis()) {
            byte[] mKey = DefaultCodec.encode(RedisKey.Rank_Info + type.getType());
            byte[] fieldKey = DefaultCodec.encode(key);
            j.hset(mKey, fieldKey, DefaultCodec.encode(t));
        }
    }

    /** int[start, end] */
    private int[] getPage(int page) {
        int[] result = new int[2];
        result[1] = page * 10 - 1;
        result[0] = result[1] - 9;
        return result;
    }

    int getTeamCapRank(long teamId) {
        return getRank(teamId, ERank.Team_Cap) + 1;
    }

    int getLeagueRank(int leagueId) {
        return getRank(leagueId, ERank.League) + 1;
    }

    private int getRank(long key, ERank type) {
        return (int) redis.zrevrank(RedisKey.Rank_Sort + type.getType(), key);
    }

    private int getRank(int key, ERank type) {
        return (int) redis.zrevrank(RedisKey.Rank_Sort + type.getType(), key);
    }

    @Override
    public void offline(long teamId) {
    }

    @Override
    public void dataGC(long teamId) {
    }

    @Override
    public void instanceAfter() {
        EventBusManager.register(EEventType.登录, this);
        redis.del("Rank_Sort_power");
        redis.del("Rank_Info_power");
    }

    @Subscribe
    public void login(LoginParam param) {
        updateTeamLevel(param.teamId, teamManager.getTeam(param.teamId).getLevel());
    }

}
