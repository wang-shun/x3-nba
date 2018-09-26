package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年4月18日
 * 比赛信息
 */
public class BattleInfoPO extends AsynchronousBatchDB {

    private long battleId;
    private int battleType;

    private long homeTeamId;
    private String homeTeamName;
    private int homeScore;

    private long awayTeamId;
    private String awayTeamName;
    private int awayScore;

    /** 附加参数int 1 */
    private int vi1;
    /** 附加参数int 2 */
    private int vi2;
    /** 附加参数int 3 */
    private int vi3;
    /** 附加参数int 4 */
    private int vi4;
    /** 附加参数long 1 */
    private long vl1;
    /** 附加参数long 2 */
    private long vl2;
    /** 附加参数long 3 */
    private long vl3;
    /** 附加参数long 4 */
    private long vl4;
    /** 附加参数str 1 */
    private String str1;
    /** 附加参数str 2 */
    private String str2;
    private DateTime createTime;

    public BattleInfoPO(long battleId, int battleType, long homeTeamId,
                        String homeTeamName, int homeScore, long awayTeamId,
                        String awayTeamName, int awayScore) {
        super();
        this.battleId = battleId;
        this.battleType = battleType;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeScore = homeScore;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayScore = awayScore;
        this.createTime = DateTime.now();
    }

    public BattleInfoPO() {
    }

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }

    public int getBattleType() {
        return battleType;
    }

    public void setBattleType(int battleType) {
        this.battleType = battleType;
    }

    public long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public int getVi1() {
        return vi1;
    }

    public void setVi1(int vi1) {
        this.vi1 = vi1;
    }

    public int getVi2() {
        return vi2;
    }

    public void setVi2(int vi2) {
        this.vi2 = vi2;
    }

    public int getVi3() {
        return vi3;
    }

    public void setVi3(int vi3) {
        this.vi3 = vi3;
    }

    public int getVi4() {
        return vi4;
    }

    public void setVi4(int vi4) {
        this.vi4 = vi4;
    }

    public long getVl1() {
        return vl1;
    }

    public void setVl1(long vl1) {
        this.vl1 = vl1;
    }

    public long getVl2() {
        return vl2;
    }

    public void setVl2(long vl2) {
        this.vl2 = vl2;
    }

    public long getVl3() {
        return vl3;
    }

    public void setVl3(long vl3) {
        this.vl3 = vl3;
    }

    public long getVl4() {
        return vl4;
    }

    public void setVl4(long vl4) {
        this.vl4 = vl4;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(battleId, battleType, homeTeamId, homeTeamName, homeScore,
                awayTeamId, awayTeamName, awayScore, createTime,
                vi1, vi2, vi3, vi4, vl1, vl2, vl3, vl4, str1, str2);
    }

    @Override
    public String getRowNames() {
        return "battle_id, battle_type, home_team_id, home_team_name, home_score," +
                " away_team_id, away_team_name, away_score, create_time, " +
                " vi1, vi2, vi3, vi4, vl1, vl2, vl3, vl4, str1, str2";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(battleId, battleType, homeTeamId, homeTeamName, homeScore,
            awayTeamId, awayTeamName, awayScore, createTime,
            vi1, vi2, vi3, vi4, vl1, vl2, vl3, vl4, str1, str2);
    }

    @Override
    public String getTableName() {
        return "t_u_battle";
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public void del() {
    }


}
