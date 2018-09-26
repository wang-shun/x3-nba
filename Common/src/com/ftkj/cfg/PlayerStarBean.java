package com.ftkj.cfg;

import java.util.Map;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年10月10日
 *
 */
public class PlayerStarBean extends ExcelBean {

	
	private int grade;
	private int needExp;
	private int xneedExp;
	private int needLevel;
	private float pts;
	private float oreb;
	private float ast;
	private float stl;
	private float blk;
	
	//EActionType 
	private Map<EActionType, Float> capMap;
	
	public PlayerStarBean() {
		capMap = Maps.newHashMap();
	}
	@Override
	public void initExec(RowData row) {
		capMap.put(EActionType.pts, row.get("pts"));
		capMap.put(EActionType.reb, row.get("oreb"));
		capMap.put(EActionType.ast, row.get("ast"));
		capMap.put(EActionType.stl, row.get("stl"));
		capMap.put(EActionType.blk, row.get("blk"));
	}
	
	public int getGrade() {
		return grade;
	}


	public int getNeedExp() {
		return needExp;
	}




	public int getXneedExp() {
		return xneedExp;
	}




	public int getNeedLevel() {
		return needLevel;
	}



	public float getPts() {
		return pts;
	}


	public float getOreb() {
		return oreb;
	}


	public float getAst() {
		return ast;
	}


	public float getStl() {
		return stl;
	}


	public float getBlk() {
		return blk;
	}
	
	public Map<EActionType, Float> getCapMap() {
		return capMap;
	}
	
	public void setCapMap(Map<EActionType, Float> capMap) {
		this.capMap = capMap;
	}

	

}
