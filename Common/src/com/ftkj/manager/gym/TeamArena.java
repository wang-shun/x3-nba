package com.ftkj.manager.gym;

import com.ftkj.console.GymConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.db.domain.TeamArenaPO;
import com.ftkj.enums.EArenaCType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamArena {
	private TeamArenaPO info;
	private Map<EArenaCType,TeamArenaConstruction> cMap;
	
	private int attackCount;
	private int stealCount;
	private TeamArenaCross attack;
	private List<TeamArenaCross> steal;
	
	public TeamArena(TeamArenaPO info, List<TeamArenaConstruction> tcList){
		this.info = info;
		this.cMap = tcList.stream().collect(Collectors.toMap(TeamArenaConstruction::getcId,v->v));
		this.steal = Lists.newArrayList();
	}
	
	public static TeamArena createTeamArena(long teamId){
		TeamArenaPO info = new TeamArenaPO();
		info.setDefend(0);
		info.setGold(500);
		info.setLastUpdateTime(DateTime.now());
		info.setLevel(1);
		info.setPower(50);
		info.setTeamId(teamId);
		List<TeamArenaConstruction> tcList = Lists.newArrayList();
		info.save();
		ArenaBean ab = GymConsole.getArenaBean(1);
		for(int i = 0 ; i < EArenaCType.values().length;i++)
			tcList.add(TeamArenaConstruction.createTeamArenaConstruction(teamId, EArenaCType.values()[i]
					,ab.getConstruction(EArenaCType.values()[i]).getGold1()));
		TeamArena ta = new TeamArena(info, tcList);
		return ta;
		
	}
	
	public void updateAttackCount(int val){
		this.attackCount = this.attackCount + val;
	}
	
	public void updateStealCount(int val){
		this.stealCount = this.stealCount + val;
	}
	
	
	public int getStealCount() {
		return stealCount;
	}

	public int getAttackCount() {
		return attackCount;
	}
	
	public long getTeamId(){
		return this.info.getTeamId();
	}

	public TeamArenaCross getAttack() {
		return attack;
	}

	public void setAttack(TeamArenaCross attack) {
		this.attack = attack;
	}

	public List<TeamArenaCross> getSteal() {
		return steal;
	}

	public void setSteal(List<TeamArenaCross> steal) {
		this.steal = steal;
	}

	public void updateLevel(int val){
		this.info.setLevel(this.info.getLevel()+val);
		this.info.save();
	}
	
	public void updateGold(int val){
		val = this.info.getGold() + val;
		this.info.setGold(val<0?0:val);
		this.info.save();
	}
	public void updateDefend(int val){
		int total = this.info.getDefend() + val;
		this.info.setDefend(total>3?3:total);
		this.info.save();
	}
	
	
	public TeamArenaConstruction getTeamArenaConstruction(EArenaCType type){
		return this.cMap.get(type);
	}
	
	
	public Map<EArenaCType, TeamArenaConstruction> getcMap() {
		return this.cMap;
	}
	public int getPower() {
		int sp = ConfigConsole.getIntVal(EConfigKey.Team_Arena_Power_Second);
		DateTime now = DateTime.now();
		int second = DateTimeUtil.secondBetween(getLastUpdateTime(),now);
		int add = second/sp;
		int last = second-add*sp;
		updatePower(add);
		this.info.setLastUpdateTime(now.plusSeconds(-last));
		this.info.save();
		return this.info.getPower();
	}
	
	public int getPowerSecond() {
		int sp = ConfigConsole.getIntVal(EConfigKey.Team_Arena_Power_Second);
		DateTime now = DateTime.now();
		int second = DateTimeUtil.secondBetween(getLastUpdateTime(),now);
		int add = second/sp;
		int last = second-add*sp;
		return last;
	}
	
	
	
	public void updatePower(int val){
		int power = this.info.getPower() + val;
		if(power < 0) power =0;
		int max = ConfigConsole.getIntVal(EConfigKey.Team_Arena_Power_Max);
		if(power > max) power = max;
		this.info.setPower(power);
		this.info.save();
	}
	
	public void updatePowerMax(int val){
		this.info.setPower(this.info.getPower() + val);
		this.info.save();
	}

	
	public int getGold() {
		return this.info.getGold();
	}

	
	/**
	 * 护盾
	 * @return
	 */
	public int getDefend() {
		return this.info.getDefend();
	}

	public DateTime getLastUpdateTime() {
		return this.info.getLastUpdateTime();
	}
	
	public int getLevel(){
		return this.info.getLevel();
	}
	
}
