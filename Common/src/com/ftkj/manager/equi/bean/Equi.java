package com.ftkj.manager.equi.bean;

import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.console.EquiConsole;
import com.ftkj.db.domain.EquiPO;
import com.ftkj.enums.EActionType;
import com.google.common.collect.Maps;

/**
 * 球队装备
 * @author lin.lin
 *
 */
public class Equi {
	
	private EquiPO equiPO;
	private Map<EActionType, Float> randAttrMap;
	
	public Equi(EquiPO po) {
		super();
		this.equiPO = po;
		// 初始化
		this.randAttrMap = Maps.newLinkedHashMap();
		if(po.getRandAttr().equals("")) {
			return;
		}
		for(String attrStr : po.getRandAttr().split(",")) {
			String[] attr = attrStr.split(":");
			this.randAttrMap.put(EActionType.convertByName(attr[0]), Float.valueOf(attr[1]));
		}
	}

	public void save(){
		StringBuilder sb = new StringBuilder();
		for(EActionType key : this.randAttrMap.keySet()) {
			sb.append(key.getConfigName() + ":" + this.randAttrMap.get(key)).append(",");
		}
		if(sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		this.equiPO.setRandAttr(sb.toString());
		this.equiPO.save();
	}
	
	public int getType() {
		return EquiConsole.getEquiBean(getEquId()).getType();
	}
	
	/**
	 * 装备序列ID
	 * @return
	 */
	public int getId() {
		return this.equiPO.getId();
	}
	
	/**
	 * 球员ID
	 * @return
	 */
	public int getPlayerId() {
		return this.equiPO.getPlayerId();
	}

	/**
	 * 取装备品质
	 * @return
	 */
	public int getQuality() {
		int qua = EquiConsole.getEquiBean(getEquId()).getQuality();
		return qua;
	}
	
	public int getEquId() {
		return this.equiPO.getEquId();
	}
	
	public int getStrLv() {
		return this.equiPO.getStrLv();
	}
	/**
	 * 球衣所属球队
	 * @return
	 */
	public int getEquiTeam() {
		return this.equiPO.getEquiTeam();
	}
	
	/**
	 * 累计强化祝福值
	 */
	public void addStrBless(float val) {
		float bless = this.equiPO.getStrBless();
		if(bless < 100) {
			this.equiPO.setStrBless(bless + val);
		}
	}
	/**
	 * 清空强化祝福值
	 */
	public void clearStrBless() {
		this.equiPO.setStrBless(0);
	}
	
	public float getStrBless() {
		return this.equiPO.getStrBless();
	}
	
	public void setEquId(int equId) {
		this.equiPO.setEquId(equId);
	}

	public void setStrLv(int strlv) {
		 this.equiPO.setStrLv(strlv);
	}

	public void setPlayerId(int playerId) {
		this.equiPO.setPlayerId(playerId);
	}
	
	public DateTime getEndTime() {
		return this.equiPO.getEndTime();
	}
	
	public void setEquiTeam(int teamId) {
		this.equiPO.setEquiTeam(teamId);
	}

	@Override
	public String toString() {
		return "Equi [equiPO=" + equiPO.toString() + "]";
	}

	public Map<EActionType, Float> getRandAttrMap() {
		return randAttrMap;
	}

	public void setRandAttrMap(Map<EActionType, Float> randAttrMap) {
		this.randAttrMap = randAttrMap;
	}
	
}
