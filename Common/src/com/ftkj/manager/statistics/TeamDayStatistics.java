package com.ftkj.manager.statistics;

import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.battle.EBattleType;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 球队每日统计数据
 *
 * @author Jay
 * @time:2018年1月16日 下午4:58:09
 */
public class TeamDayStatistics implements Serializable {
    private static final long serialVersionUID = 1L;
    private long teamId;
    /**
     * 统计各种比赛的今天场次数
     */
    private DBList pkCount = new DBList(typeList.size());
    
    /**
     * 数组对应多人赛的类型
     */
    private static List<EBattleType> typeList = Lists.newArrayList(new EBattleType[]{
    	    EBattleType.多人赛_100, // 所有的多人赛都是这个类型，这里要转一下
    		EBattleType.Ranked_Match, // 天梯赛
    		EBattleType.联盟组队赛,
    });
    
    public TeamDayStatistics(long teamId) {
        this.teamId = teamId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
    
    /**
     * 统计比赛次数
     * @param type
     */
    public TeamDayStatistics addPkCount(EBattleType type) {
    	// 多人赛
    	if(type.getId() >= 100 && type.getId() <= 200) {
    		this.pkCount.setValueAdd(0, 1);
    		return this;
    	}
    	int index = typeList.indexOf(type);
    	if(index == -1){
    		return this;
    	}
    	this.pkCount.setValueAdd(index, 1);
    	return this;
    }
    
    public int getPkCount(EBattleType type) {
    	// 多人赛
    	if(type.getId() >= 100 && type.getId() <= 200) {
    		return this.pkCount.getValue(0);
    	}
    	int index = typeList.indexOf(type);
    	if(index == -1){
    		return 0;
    	}
    	return this.pkCount.getValue(index);
    	
    }

	public DBList getPkCount() {
		return pkCount;
	}

	@Override
	public String toString() {
		return "TeamDayStatistics [teamId=" + teamId + ", pkCount=" + getPKCount() + "]";
	}
	
	private String getPKCount() {
		StringBuilder sb = new StringBuilder();
		for(EBattleType t : typeList) {
			sb.append(t).append("("+pkCount.getValue(typeList.indexOf(t))+"), ");
		}
		return sb.toString();
	}

	/**
	 * 兼容其他比赛类型添加,pkCount长度变化，补0
	 */
	public void redisGetInit() {
		if(this.pkCount.getSize() >= typeList.size()) {
			return;
		}
		for(int i=this.pkCount.getSize(); i < typeList.size(); i++) {
			this.pkCount.addValue(0);
		}
	}
	
}
