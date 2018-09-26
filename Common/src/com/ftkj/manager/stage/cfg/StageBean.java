package com.ftkj.manager.stage.cfg;

import java.util.List;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Lists;

/**
 * @Description:主线关卡配置
 * @author Jay
 * @time:2017年3月24日 下午3:29:40
 */
public class StageBean {

	/**
	 * 奇数是常规赛，偶数是季后赛，一个组合是一个赛季，方便前台控制动画
	 * 1~2，勇士冲锋
	 * 3~4，骑士冲锋
	 */
	private int tab;
	
	private int stageId;
	
	/**
	 * 1 常规赛
	 * 2 季后赛
	 */
	private int type;
	
	private int step;
	
	private long npcId; // 对手
	
	private int level;
	
//	private List<PropSimple> props;
	
	// 掉落
	private int drop;
	
	private int homeTeamId;
	
	// 初始化
	public void initExec(RowData row) {
//		props = Lists.newArrayList();
//		String propStr = row.get("crop");
//		if(!"".equals(propStr)) {
//			String[] cfg = propStr.split("[_]");
//			for(int i=0; i < cfg.length; i +=2) {
//				PropSimple t = new PropSimple(Integer.valueOf(cfg[i]), Integer.valueOf(cfg[i+1])); 
//				props.add(t);
//			}
//		}
	}

	public int getHomeTeamId() {
		return homeTeamId;
	}

	public void setHomeTeamId(int homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

//	public List<PropSimple> getProps() {
//		return props;
//	}
//
//	public void setProps(List<PropSimple> props) {
//		this.props = props;
//	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public long getNpcId() {
		return npcId;
	}

	public void setNpcId(long npcId) {
		this.npcId = npcId;
	}

	public int getDrop() {
		return drop;
	}

	public void setDrop(int drop) {
		this.drop = drop;
	}
	
}
