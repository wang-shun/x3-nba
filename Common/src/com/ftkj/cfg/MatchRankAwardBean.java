package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

public class MatchRankAwardBean extends ExcelBean {

	
	private int id;
	private int min;
	private int max;
	private int drop;

	@Override
	public void initExec(RowData row) {
		String[] rank = row.get("rank").toString().split("[-]");
		this.min = Integer.parseInt(rank[0]);
		if(rank.length>1) {
			this.max = Integer.parseInt(rank[1]);
		}else {
			this.max = this.min;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getDrop() {
		return drop;
	}

	public void setDrop(int drop) {
		this.drop = drop;
	}
	
}
