package com.ftkj.manager.gym;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EArenaCType;
import com.ftkj.util.excel.RowData;

public class ArenaConstructionBean extends ExcelBean {
	private int level;
	private EArenaCType cidType;
	private int gold1;
	private int gold2;
	private int gold3;
	private int gold4;
	private int gold5;

	@Override
	public void initExec(RowData row) {
		int c = row.get("cid");
		this.cidType = EArenaCType.getEArenaCType(c);
	}
	
	//当前建筑级别所需金币
	public int getCurLevelGold(int gold){
		if(gold == gold1){
			return gold1;
		}else if(gold == gold2){
			return gold2-gold1;
		}else if(gold == gold3){
			return gold3-gold2;
		}else if(gold == gold4){
			return gold4-gold3;
		}else if(gold == gold5){
			return gold5-gold4;
		}else{
			return gold1;
		}
	}
	
	
	public int getNextGold(int gold){
		if(gold>=0 && gold<gold1){
			return gold1;
		}else if(gold>=gold1 && gold < gold2){
			return gold2;
		}else if(gold>=gold2 && gold < gold3){
			return gold3;
		}else if(gold>=gold3 && gold < gold4){
			return gold4;
		}else if(gold>=gold4 && gold < gold5){
			return gold5;
		}else{
			return 0;
		}
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public EArenaCType getCid() {
		return cidType;
	}

	public void setCid(EArenaCType cid) {
		this.cidType = cid;
	}

	public int getGold1() {
		return gold1;
	}

	public void setGold1(int gold1) {
		this.gold1 = gold1;
	}

	public int getGold2() {
		return gold2;
	}

	public void setGold2(int gold2) {
		this.gold2 = gold2;
	}

	public int getGold3() {
		return gold3;
	}

	public void setGold3(int gold3) {
		this.gold3 = gold3;
	}

	public int getGold4() {
		return gold4;
	}

	public void setGold4(int gold4) {
		this.gold4 = gold4;
	}

	public int getGold5() {
		return gold5;
	}

	public void setGold5(int gold5) {
		this.gold5 = gold5;
	}


}
