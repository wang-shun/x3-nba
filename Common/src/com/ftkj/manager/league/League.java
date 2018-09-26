package com.ftkj.manager.league;

import com.ftkj.db.domain.LeaguePO;

/**
 * 联盟
 * @author tim.huang
 * 2017年5月18日
 *
 */
public class League {
	private LeaguePO po;
	
	
	public League(LeaguePO po) {
		super();
		this.po = po;
	}

	/**
	 * 默认人数1
	 * @param leagueId
	 * @param leagueName
	 * @param logo
	 * @param teamName
	 * @param leagueTip
	 * @param leagueNotice
	 * @return
	 */
	public static League createLeague(int leagueId, String leagueName, String logo,
			String teamName, String leagueTip, String leagueNotice){
		LeaguePO p = new LeaguePO(leagueId, leagueName, logo, teamName, leagueTip, leagueNotice);
		p.save();
		League l = new League(p);
		return l;
	}
	
	public void updateShopLimit(int val){
		this.po.setShopLimit(val);
		this.po.save();
	}
	
	public void updateHonorLimit(int val){
		this.po.setHonorLimit(val);
		this.po.save();
	}
	
	
	public void updateLevel(int level){
		this.po.setLeagueLevel(level);
		this.po.save();
	}
	
	public void updateLeagueHonor(int val){
		this.po.setHonor(this.po.getHonor()+val);
		this.po.save();
	}
	
	public void updateLeagueScore(int val){
		this.po.setScore(this.po.getScore()+val);
		this.po.save();
	}
	
	public void updatePeopleCount(int val){
		this.po.setPeopleCount(this.po.getPeopleCount()+val);
		this.po.save();
	}
	
	public void updateLeagueTip(String tip){
		this.po.setLeagueTip(tip);
		this.po.save();
	}
	
	public void updateLeagueNotice(String notice){
		this.po.setLeagueNotice(notice);
		this.po.save();
	}
	
	public void updateTeamName(String teamName) {
		this.po.setTeamName(teamName);
		this.po.save();
	}
	
	/**
	 * 更新联盟当前的经验.
	 * @param exp
	 */
	public void updateLeagueExp(int exp){
		this.po.setLeagueExp(exp);
		this.po.save();
	}
	
	/**
	 * 更新联盟当前的累计总经验.
	 * @param exp
	 */
	public void updateLeagueTotalExp(int exp){
		this.po.setLeagueTotalExp(this.po.getLeagueTotalExp() + exp);
		this.po.save();
	}
	
	public int getShopLimit() {
		return this.po.getShopLimit();
	}

	public int getHonorLimit() {
		return this.po.getHonorLimit();
	}
	
	
	public int getPeopleCount() {
		return this.po.getPeopleCount();
	}
	
	public int getHonor() {
		return this.po.getHonor();
	}
	
	public long getScore(){
		return this.po.getScore();
	}
	
	public String getLogo() {
		return this.po.getLogo();
	}

	public int getLeagueId() {
		return this.po.getLeagueId();
	}

	public String getLeagueName() {
		return this.po.getLeagueName();
	}

	public int getLeagueLevel() {
		return this.po.getLeagueLevel();
	}

	public String getTeamName() {
		return this.po.getTeamName();
	}

	public String getLeagueTip() {
		return this.po.getLeagueTip();
	}

	public String getLeagueNotice() {
		return this.po.getLeagueNotice();
	}
	
	public int getLeagueExp(){
		return this.po.getLeagueExp();
	}
	
	public int getLeagueTotalExp(){
		return this.po.getLeagueTotalExp();
	}
	
}
