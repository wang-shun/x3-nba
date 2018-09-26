package com.ftkj.cfg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 球员训练配置Bean
 * @author Jay
 * @time:2017年9月29日 上午11:32:26
 */
public class TrainPlayerBean extends ExcelBean {
	
	private long teamId;
	private int lv;
	private int playerLv;
	private List<PropSimple> needProps;
	private int step;
	private List<Integer> starPosList;
	private float stepRate;
	private float starRate;
	/**
	 * 
	 * 位置：大星脉攻防加成
	 */
	private Map<Integer, Map<String,Integer>> starCap;
	// 总进攻
	private int starAtk;
	// 总防守
	private int starDef;
	
	@Override
	public void initExec(RowData row) {
		needProps = PropSimple.getPropBeanByStringNotConfig(row.get("needProp"));
		starPosList = Arrays.stream(row.get("starPos").toString().split(",")).mapToInt(p-> Integer.valueOf(p)).boxed().collect(Collectors.toList());
		starCap = Maps.newHashMap();
		for(int i=1; i < starPosList.size()+1; i++) {
			String[] capCfg = row.get("star" + i).toString().split(":");
			Map<String, Integer> capMap = Maps.newHashMap();
			capMap.put(capCfg[0], Integer.valueOf(capCfg[1]));
			starCap.put(starPosList.get(i-1), capMap);
		}
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getPlayerLv() {
		return playerLv;
	}

	public void setPlayerLv(int playerLv) {
		this.playerLv = playerLv;
	}

	public List<PropSimple> getNeedProps() {
		return needProps;
	}

	public void setNeedProps(List<PropSimple> needProps) {
		this.needProps = needProps;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public List<Integer> getStarPosList() {
		return starPosList;
	}

	public void setStarPosList(List<Integer> starPosList) {
		this.starPosList = starPosList;
	}

	public float getStepRate() {
		return stepRate;
	}

	public void setStepRate(float stepRate) {
		this.stepRate = stepRate;
	}

	public float getStarRate() {
		return starRate;
	}

	public void setStarRate(float starRate) {
		this.starRate = starRate;
	}

	public Map<Integer, Map<String, Integer>> getStarCap() {
		return starCap;
	}

	public int getStarAtk() {
		return starAtk;
	}

	public void setStarAtk(int starAtk) {
		this.starAtk = starAtk;
	}

	public int getStarDef() {
		return starDef;
	}

	public void setStarDef(int starDef) {
		this.starDef = starDef;
	}
	
	
}
