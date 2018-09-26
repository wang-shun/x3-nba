package com.ftkj.manager.equi.cfg;

import java.util.Map;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.equi.EEquiType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 品阶随机属性刷新配置
 * @author Jay
 * @time:2018年3月23日 上午11:11:47
 */
public class EquiRefreshBean extends ExcelBean {

	private int quality;
	/**
	 * 随机属性条数
	 */
	private int randomStatCount;
	/**
	 * 属性的加成 
	 */
	private float randomStat;
	
	/**
	 * 装备type:刷新需要的道具数量
	 */
	private Map<EEquiType, PropSimple> needPropMap;
	
	@Override
	public void initExec(RowData row) {
		this.needPropMap  = Maps.newHashMap();
		this.needPropMap.put(EEquiType.头带, PropSimple.getPropSimpleByString(row.get("propCfg1")));
		this.needPropMap.put(EEquiType.护腕, PropSimple.getPropSimpleByString(row.get("propCfg2")));
		this.needPropMap.put(EEquiType.护膝, PropSimple.getPropSimpleByString(row.get("propCfg4")));
		this.needPropMap.put(EEquiType.球鞋, PropSimple.getPropSimpleByString(row.get("propCfg5")));
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getRandomStatCount() {
		return randomStatCount;
	}

	public void setRandomStatCount(int randomStatCount) {
		this.randomStatCount = randomStatCount;
	}


	public float getRandomStat() {
		return randomStat;
	}

	public void setRandomStat(float randomStat) {
		this.randomStat = randomStat;
	}

	public Map<EEquiType, PropSimple> getNeedPropMap() {
		return needPropMap;
	}

	public void setNeedPropMap(Map<EEquiType, PropSimple> needPropMap) {
		this.needPropMap = needPropMap;
	}
	
}
