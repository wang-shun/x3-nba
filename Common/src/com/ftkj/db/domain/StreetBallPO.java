package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

public class StreetBallPO extends AsynchronousBatchDB {

	private long teamId;
	// 副本类型最新关卡
	private int type1;
	private int type2;
	private int type3;
	private int type4;
	private int type5;
	
	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getType1() {
		return type1;
	}

	public void setType1(int type1) {
		this.type1 = type1;
	}

	public int getType2() {
		return type2;
	}

	public void setType2(int type2) {
		this.type2 = type2;
	}

	public int getType3() {
		return type3;
	}

	public void setType3(int type3) {
		this.type3 = type3;
	}

	public int getType4() {
		return type4;
	}

	public void setType4(int type4) {
		this.type4 = type4;
	}

	public int getType5() {
		return type5;
	}

	public void setType5(int type5) {
		this.type5 = type5;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.type1,this.type2,this.type3,this.type4,this.type5);
	}

	@Override
	public String getRowNames() {
		return "team_id,type_1,type_2,type_3,type_4,type_5";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.type1,this.type2,this.type3,this.type4,this.type5);
    }

	@Override
	public String getTableName() {
		return "t_u_street_ball";
	}

	@Override
	public void del() {
		
	}


}
