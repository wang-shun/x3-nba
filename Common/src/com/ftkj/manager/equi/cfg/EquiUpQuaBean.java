package com.ftkj.manager.equi.cfg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.equi.EEquiType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 装备升阶配置
 * @author lin.lin
 *
 */
public class EquiUpQuaBean {

	/**
	 * 当前品质
	 */
	private int quality;
	/**
	 * 需要金币
	 */
	private int money;
	/**
	 * 套装需要几件的配置
	 */
	private List<Integer> suitConfig;
	/**
	 * 套装的属性加成
	 */
	private Map<EActionType, Float> suitAddMap;
	/**
	 * 装备类型：需要道具
	 */
	private Map<EEquiType, PropSimple> needPropMap;
	
	/**
	 * 扩展解析方法
	 * @param
	 */
	public void initExec(RowData row) {
		//
		this.needPropMap  = Maps.newHashMap();
		this.needPropMap.put(EEquiType.头带, PropSimple.getPropSimpleByString(row.get("propCfg1")));
		this.needPropMap.put(EEquiType.护腕, PropSimple.getPropSimpleByString(row.get("propCfg2")));
		this.needPropMap.put(EEquiType.护膝, PropSimple.getPropSimpleByString(row.get("propCfg4")));
		this.needPropMap.put(EEquiType.球鞋, PropSimple.getPropSimpleByString(row.get("propCfg5")));
		//
		this.suitConfig = Arrays.stream(row.get("suit").toString().split(","))
				.filter(s-> !s.equals(""))
				.mapToInt(s-> Integer.valueOf(s)).boxed().collect(Collectors.toList());
		//
		this.suitAddMap = Maps.newHashMap();
		float tempCap = row.get("suitStat");
		if(tempCap > 0) {
			this.suitAddMap.put(EActionType.convertByName(row.get("suitStatType")), tempCap);
		}
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public List<Integer> getSuitConfig() {
		return suitConfig;
	}

	public void setSuitConfig(List<Integer> suitConfig) {
		this.suitConfig = suitConfig;
	}

	public Map<EActionType, Float> getSuitAddMap() {
		return suitAddMap;
	}

	public void setSuitAddMap(Map<EActionType, Float> suitAddMap) {
		this.suitAddMap = suitAddMap;
	}

	public Map<EEquiType, PropSimple> getNeedPropMap() {
		return needPropMap;
	}

}
