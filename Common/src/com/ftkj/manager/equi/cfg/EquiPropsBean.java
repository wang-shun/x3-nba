package com.ftkj.manager.equi.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ftkj.console.EquiConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.manager.prop.PropSimple;

/**
 * 装备养成道具：几率，配置bean
 * @author lin.lin
 *
 */
public class EquiPropsBean {

	private Map<Integer, Float> propsMap;
	
	/**
	 * 初始化
	 * @param cfg
	 */
	public EquiPropsBean(String cfg) {
		propsMap = new HashMap<Integer, Float>();
		if(cfg!=null && !"".equals(cfg)) {
			String[] vues =cfg.split(",");
			for(String vue : vues) {
				String[] temp = vue.split(":");
				propsMap.put(Integer.valueOf(temp[0]), Float.valueOf(temp[1]));
			}
		}
	}
	
	/**
	 * @param propsCfg  道具列表
	 * @param probability 对应的概率列表
	 */
	public EquiPropsBean(String propsCfg, String probability) {
		if(propsCfg.trim().equals("") || probability.trim().equals("")) {
			return;
		}
		String[] propsCfgStr = propsCfg.split(",");
		String[] probabilityStr = probability.split(",");
		if(propsCfgStr.length != probabilityStr.length) {
			System.err.println("配置概率和道具数量不一致...");
			return;
		}
		propsMap = new HashMap<Integer, Float>();
		int len = propsCfgStr.length;
		for(int i=0; i<len; i++) {
			propsMap.put(Integer.valueOf(propsCfgStr[i]), Float.valueOf(probabilityStr[i]));
		}
	}

	/**
	 * 通过道具得到概率
	 * @param propsId
	 * @return
	 */
	private float getRateByProp(int propsId) {
		if(propsMap.containsKey(propsId)) {
			return propsMap.get(propsId);
		}
		return 0f;
	}
	
	/**
	 * 通过道具得到概率
	 * @param props
	 * @return
	 */
	public float getRateByProps(List<PropSimple> props) {
		float rate = 0f;
		for(PropSimple ps : props) {
			if(propsMap.containsKey(ps.getPropId())) {
				rate += getRateByProp(ps.getPropId()) * ps.getNum();
			}
		}
		return rate>1?1:rate;
	}

	/**
	 * 升阶专用
	 * @param collection
	 * @return
	 */
	public boolean canUse(int type, Collection<PropSimple> props) {
		for(PropSimple ps : props) {
			// 球衣特殊处理
			if(type == 3) {
				int team = 0;
				try{
					team = Integer.valueOf(PropConsole.getProp(ps.getPropId()).getConfig());
				}catch(Exception e) {
					return false;
				}
				if(ps.getPropId() != EquiConsole.getClothesUpQuaItem(team)) {
					return false;
				}
			}
			else if (!propsMap.containsKey(ps.getPropId())) {
				 return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否能使用道具，概率0不代表不能用
	 * @param props
	 * @return
	 */
	public boolean canUse(Collection<PropSimple> props) {
		for(PropSimple ps : props) {
			if (!propsMap.containsKey(ps.getPropId())) {
				 return false;
			}
		}
		return true;
	}
	/**
	 * 是否能使用道具，概率0不代表不能用
	 * @param propsId
	 * @return
	 */
	public boolean canUse(int propsId) {
		return propsMap.containsKey(propsId);
	}
	
	public Map<Integer, Float> getPropsMap() {
		return propsMap;
	}

	public void setPropsMap(Map<Integer, Float> propsMap) {
		this.propsMap = propsMap;
	}
	
}
