package com.ftkj.manager.gym;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EArenaCType;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.util.excel.RowData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年7月14日
 *
 */
public class ArenaBean extends ExcelBean{
	private Map<EArenaCType,ArenaConstructionBean> constructionMap;
	private int level;
	private int attackGold;
	private int attackDefendGold;
	private int stealGold;
	private int rid;
	private int attack;
	private int defend;
	
	private ENBAPlayerTeam nbaTeam;
	
	
	public void initConstructionBean(List<ArenaConstructionBean> list){
		this.constructionMap = list.stream().collect(Collectors.toMap(ArenaConstructionBean::getCid, v->v));
	}
	
	
	
	@Override
	public void initExec(RowData row) {
		int tid = row.get("team");
		this.nbaTeam = ENBAPlayerTeam.getENBAPlayerTeam(tid);
	}
	
	public ArenaConstructionBean getConstruction(EArenaCType type){
		return this.constructionMap.get(type);
	}
	
	public int getRid() {
		return rid;
	}

	public int getAttackDefendGold() {
		return attackDefendGold;
	}

	public int getAttackGold() {
		return attackGold;
	}

	public int getStealGold() {
		return stealGold;
	}

	public int getLevel() {
		return level;
	}
	public ENBAPlayerTeam getTeam() {
		return nbaTeam;
	}
	public int getAttack() {
		return attack;
	}

	public int getDefend() {
		return defend;
	}
	
	
}
