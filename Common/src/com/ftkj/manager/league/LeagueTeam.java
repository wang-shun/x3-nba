package com.ftkj.manager.league;

import org.joda.time.DateTime;

import com.ftkj.console.LeagueArenaConsole;
import com.ftkj.db.domain.LeagueTeamPO;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang
 * 2017年5月18日
 * 联盟成员
 */
public class LeagueTeam {
	private LeagueTeamPO po;
	
	public LeagueTeam(){
		
	}
	
	private LeagueTeam(LeagueTeamPO po) {
		super();
		this.po = po;
	}

	/**
	 * 创建一个新的联盟成员对象
	 * @param leagueId
	 * @param teamId
	 * @param level
	 * @return
	 */
	public static LeagueTeam createLeagueTeam(int leagueId, long teamId, ELeagueTeamLevel level,int feats){
		LeagueTeamPO p = new LeagueTeamPO(leagueId, teamId, level.getId(),feats);
		p.save();
		return instantLeagueTeam(p);
	}
	
	/**
	 * 实例化一个联盟对象
	 * @param ltp
	 * @return
	 */
	public static LeagueTeam instantLeagueTeam(LeagueTeamPO ltp){
		LeagueTeam l = new LeagueTeam(ltp);
		return l;
	}
	
	public void updateLevel(ELeagueTeamLevel level){
		this.po.setLevel(level.getId());
		this.po.save();
	}
	
	public void updateFeats(int val){
		this.po.setFeats(this.po.getFeats()+val);
		this.po.save();
	}
	
	
	public void updateScore(int val){
		this.po.setScore(this.po.getScore()+val);
		this.po.save();
	}
	
	// 更新一到周六的贡献值
	public void updateWeekScore(int val){
	    if(DateTimeUtil.getCurrWeekDay() == LeagueArenaConsole.OpenDayOfWeek) return;
        this.po.setWeekScore(this.po.getWeekScore()+val);
        this.po.save();
    }
	
	public void quit(){
		this.po.del();
		this.po.save();
	}
	
	public int getFeats() {
		return this.po.getFeats();
	}
	
	public int getLeagueId() {
		return po.getLeagueId();
	}

	public long getTeamId() {
		return po.getTeamId();
	}

	public ELeagueTeamLevel getLevel() {
		return ELeagueTeamLevel.getELeagueTeamLevel(po.getLevel());
	}

	public int getScore() {
		return po.getScore();
	}

	public DateTime getCreateTime() {
		return po.getCreateTime();
	}
	
    public int getWeekScore() {
        return  po.getWeekScore();
    }

    public void setWeekScore(int weekScore) {
        this.po.setWeekScore(weekScore);       
    }	
}
