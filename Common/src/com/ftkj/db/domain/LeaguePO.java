package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年5月18日
 * 联盟DB
 */
public class LeaguePO extends AsynchronousBatchDB {

    private static final long serialVersionUID = 1L;

    /** 联盟id */
    private int leagueId;
    /** 联盟名稱 */
    private String leagueName = "";
    /** 联盟圖標 */
    private String logo = "";
    /** 联盟等級 */
    private int leagueLevel;
    /**联盟当前经验*/
    private int leagueExp;
    /**联盟升级的累计获得经验*/
    private int leagueTotalExp;
    /** 联盟荣誉值 */
    private int honor;
    /** 联盟累计贡献值*/
    private long score;
    /** 球隊名 */
    private String teamName = "";
    /** 联盟宣言 */
    private String leagueTip = "";
    /** 联盟公告 */
    private String leagueNotice = "";
    /** 联盟人數 */
    private int peopleCount;
    /** 联盟id */
    private int shopLimit;
    /** 联盟id */
    private int honorLimit;
    /** 联盟id */
    private DateTime createTime;

    public LeaguePO() {
    }

    public LeaguePO(int leagueId, String leagueName, String logo,
                    String teamName, String leagueTip, String leagueNotice) {
        super();
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.logo = logo;
        this.teamName = teamName;
        this.leagueLevel = 1;
        this.honor = 0;
        this.score = 0;
        this.peopleCount = 1;
        this.leagueTip = leagueTip;
        this.leagueNotice = leagueNotice;
        this.shopLimit = 2000;
        this.honorLimit = 2000;
        this.createTime = DateTime.now();
        this.leagueExp = 0;
        this.leagueTotalExp = 0;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public void setShopLimit(int shopLimit) {
        this.shopLimit = shopLimit;
    }

    public int getShopLimit() {
        return shopLimit;
    }

    public int getHonorLimit() {
        return honorLimit;
    }

    public void setHonorLimit(int honorLimit) {
        this.honorLimit = honorLimit;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setLeagueLevel(int leagueLevel) {
        this.leagueLevel = leagueLevel;
    }

    public void setHonor(int honor) {
        this.honor = honor;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setLeagueTip(String leagueTip) {
        this.leagueTip = leagueTip;
    }

    public void setLeagueNotice(String leagueNotice) {
        this.leagueNotice = leagueNotice;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public int getHonor() {
        return honor;
    }
    
    public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public String getLogo() {
        return logo;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public int getLeagueLevel() {
        return leagueLevel;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getLeagueTip() {
        return leagueTip;
    }

    public String getLeagueNotice() {
        return leagueNotice;
    }
    
    public int getLeagueExp() {
		return leagueExp;
	}

	public void setLeagueExp(int leagueExp) {
		this.leagueExp = leagueExp;
	}

	public int getLeagueTotalExp() {
		return leagueTotalExp;
	}

	public void setLeagueTotalExp(int leagueTotalExp) {
		this.leagueTotalExp = leagueTotalExp;
	}

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.leagueId, this.leagueName,
        		this.logo, this.leagueLevel, this.leagueExp, this.leagueTotalExp,
        		this.honor, this.score, this.teamName, this.leagueTip, this.leagueNotice,
            this.peopleCount, this.shopLimit, this.honorLimit, this.createTime);
    }

    @Override
    public String getRowNames() {
        return "league_id, league_name, logo, league_level, league_exp, league_total_exp,"
        		+ " honor, score, team_name, league_tip, league_notice, "
        		+ "people_count, limit_shop, limit_honor, create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId,
            this.leagueName,
            this.logo,
            this.leagueLevel,
            this.leagueExp,
            this.leagueTotalExp,
            this.honor,
            this.score,
            this.teamName,
            this.leagueTip,
            this.leagueNotice,
            this.peopleCount,
            this.shopLimit,
            this.honorLimit,
            this.createTime);
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public String getTableName() {
        return "t_u_league";
    }

    @Override
    public void del() {

    }

}
