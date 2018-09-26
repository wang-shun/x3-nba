package com.ftkj.manager.equi.cfg;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.enums.EActionType;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 装备配置bean
 * @author lin.lin
 *
 */
public class EquiBean {

	private int id;
	private int type;
	private int quality;
	
	//EActionType 
	private Map<EActionType, Float> capMap;
	
	public EquiBean() {
		super();
		capMap = Maps.newHashMap();
	}

	/**
	 * 扩展解析方法
	 * @param
	 */
	public void initExec(RowData row) {
		capMap.put(EActionType.pts, row.get("pts"));
		capMap.put(EActionType.ast, row.get("ast"));
		capMap.put(EActionType.reb, row.get("reb"));
		capMap.put(EActionType.stl, row.get("stl"));
		capMap.put(EActionType.blk, row.get("blk"));
		capMap.put(EActionType.ftm, row.get("ftm"));
		capMap.put(EActionType.fgm, row.get("fgm"));
		capMap.put(EActionType._3pm, row.get("three_pm"));
	}
	
	/**
	 * 随机属性属性数
	 * @param count 条数
	 * @param val 属性值
	 * @return
	 */
	public Map<EActionType, Float> randomAttr(int count, float value) {
		Map<EActionType, Float> map = Maps.newHashMap();
		List<EActionType> temp = this.capMap.keySet().stream().collect(Collectors.toList());
		List<Integer> randIndexs = RandomUtil.getRandomBySeed(DateTime.now().getMillis(), temp.size(), count, false);
		for(int index : randIndexs) {
			map.put(temp.get(index), value);
		}
		return map;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
	
	public Map<EActionType, Float> getCapMap() {
		return capMap;
	}

	public void setCapMap(Map<EActionType, Float> capMap) {
		this.capMap = capMap;
	}

	/**
	 * 取加成属性
	 * @param type
	 * @return
	 */
	public float getAttrData(EActionType type){
		return capMap.getOrDefault(type,0f);
	}

	@Override
	public String toString() {
		return "EquiBean [id=" + id + ", type=" + type + ", quality=" + quality + ", capMap=" + capMap + "]";
	}
	
}
