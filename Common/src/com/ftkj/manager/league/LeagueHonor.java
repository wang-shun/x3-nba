package com.ftkj.manager.league;

import java.util.Collection;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.console.LeagueConsole;
import com.ftkj.db.domain.LeagueHonorPO;
import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年5月25日
 * 联盟成就
 */
public class LeagueHonor {
	
	private int leagueId;
	private LeagueHonorPool pool;
	private Map<Integer,LeagueHonorPO> honorMap;
	
	
	public LeagueHonor(int leagueId) {
		super();
		this.leagueId = leagueId;
		this.pool = new LeagueHonorPool(leagueId);
		this.honorMap = Maps.newHashMap();
	}
	
	public void initAppendHonor(LeagueHonorPO po){
		this.honorMap.put(po.getHonorId(), po);
	}
	
	/**
	 * 成就升级,联盟等级限制成就等级,其他没有限制.
	 * @param hid
	 * @param league
	 * @return
	 */
	public boolean levelUp(int hid,League league){
		LeagueHonorPO po = this.honorMap.get(hid);
		int curLevel  = po.getLevel();
//		if(curLevel>=getMinLevel()+1) {
//			return false;
//		}
		LeagueHonorBean lhb = LeagueConsole.getLeagueHonorBean(hid, curLevel);
		LeagueHonorBean next = LeagueConsole.getLeagueHonorBean(hid, curLevel+1);
		if(next == null) {
			return false;
		}
		
		if(league.getHonor() < lhb.getHonorConsume()) {
			return false;
		}
		
		//修改只消耗荣誉值
		league.updateLeagueHonor(-lhb.getHonorConsume());
		po.setLevel(next.getLevel());
		po.save();
		
		return true;
	}
	
	/**
	 * 取最低等级
	 * @return
	 */
	public int getMinLevel(){
		return this.honorMap.values().stream().mapToInt(ho->ho.getLevel()).min().orElse(1);
	}
	
	public int getAllLevel(){
		return this.honorMap.values().stream().mapToInt(ho->ho.getLevel()).sum();
	}
	
	public LeagueHonorPO getLeagueHonorPO(int hid){
		return this.honorMap.get(hid);
	}
	
	/**
	 * 创建默认激活到本周六
	 * @param now
	 */
	public void createActivateAll(){
		DateTime now = DateTime.now();
		int week = now.getDayOfWeek();
		if (week >= 6) {// 下周
			now = now.plusDays(7);
		}
		final DateTime endTime = now.withDayOfWeek(6).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
		this.honorMap.values().forEach(po->{
			po.setEndTime(endTime);
			po.save();
		});
	}
	
	public boolean isActivate(int hid){
		return this.honorMap.get(hid).getEndTime().isAfter(DateTime.now());
	}
	
	public int getLevel(int hid){
		return this.honorMap.get(hid).getLevel();
	}
	
	public void appendHonorProp(int pid,int num){
		this.pool.appendHonorProp(pid, num);
	}
	
	
	public int getCurPropCount(int pid){
		return this.pool.getCurPropCount(pid);
	}

	public static LeagueHonor createLeagueHonor(int leagueId){
		LeagueHonor lh = new LeagueHonor(leagueId);
		LeagueConsole.getAllBaseHonor().forEach(honor->lh.honorMap.put(honor.getHonorId(), 
									new LeagueHonorPO(leagueId,honor.getHonorId(),honor.getLevel())));
		return lh;
	}
	
	
	public Collection<LeagueHonorPoolPO> getPoolProps(){
		return this.pool.getPropMap().values();
	}
	
	
	public int getLeagueId() {
		return leagueId;
	}
	public LeagueHonorPool getPool() {
		return pool;
	}
	public Map<Integer, LeagueHonorPO> getHonorMap() {
		return honorMap;
	}	
}
