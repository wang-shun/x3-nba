package com.ftkj.event.param;

/**
 * 装备相关
 * @author Jay
 * @time:2017年9月8日 下午4:14:52
 */
public class EquiParam {

	public long teamId;
	/** 装备ID*/
	public int equiId;
	/** 强化等级*/
	public int strLv;
	/** 品阶*/
	public int quality;
	/** 是否成功*/
	public boolean suc;
	/** 操作类型，1强化，2进阶，3，进化（升阶） */
	public EquiEVentType opeType;
	
	/**
	 * 
	 * @param teamId
	 * @param equiId 装备ID
	 * @param strLv 强化等级
	 * @param quality 品阶
	 * @param suc 是否成功
	 * @param opeType  操作类型，1强化，2进阶，3，进化（升阶）
	 */
	public EquiParam(long teamId, int equiId, int strLv, int quality, boolean suc, EquiEVentType opeType) {
		super();
		this.teamId = teamId;
		this.equiId = equiId;
		this.strLv = strLv;
		this.quality = quality;
		this.suc = suc;
		this.opeType = opeType;
	}

	public enum EquiEVentType {
		强化,
		进化_染色,
	}
	
}
