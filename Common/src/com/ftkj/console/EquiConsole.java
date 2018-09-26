package com.ftkj.console;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.manager.equi.bean.Equi;
import com.ftkj.manager.equi.cfg.EquiBean;
import com.ftkj.manager.equi.cfg.EquiClothesBean;
import com.ftkj.manager.equi.cfg.EquiRefreshBean;
import com.ftkj.manager.equi.cfg.EquiUpLvBean;
import com.ftkj.manager.equi.cfg.EquiUpQuaBean;
import com.ftkj.manager.equi.cfg.EquiUpStrBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @Description:装备控制台
 * @author Jay
 * @time:2017年3月14日 下午2:13:03
 */
public class EquiConsole {
	
	/** 装备最大等级 */
	public static int MAX_LV;
	/** 最大强化等级 */
	public static int MAX_STRONGLV;
	
	/** 升阶最高品质 */
	public static int MAX_QUALITY;
	
	/** 最大装备套数 */
	public static int MAX_EQUI_NUM;
	
	/** 强化祝福值道具，高级强化石 */
	public static int STR_BLESS_PROP;
	/** 进阶祝福值道具，高级进阶石 */
	public static List<Integer> QUA_BLESS_PROP;
	
	/** 升级道具 **/
	public static int UPLV_PROP;
	
	/**
	 * 系统赠送的初级装备套装配置
	 */
	public static List<Integer> EQUI_INIT_LIST; 

	/**
	 * 装备配置表
	 */
	private static Map<Integer,EquiBean> equiMap;
	/**
	 * 升级配置
	 */
	private static Map<Integer,EquiUpLvBean> equiUpLvMap;
	/**
	 * 升阶配置
	 */
	private static Map<Integer,EquiUpQuaBean> equiUpQuaMap;
	/**
	 * 强化配置
	 */
	private static Map<Integer,EquiUpStrBean> equiUpStrMap;
	
	private static Map<Integer, EquiClothesBean> clothesMap;
	/**
	 * 刷新的配置
	 */
	private static Map<Integer, EquiRefreshBean> refreshmap;
	
	
	
	public static void init(List<EquiBean> equiList, List<EquiUpStrBean> strList, List<EquiUpLvBean> upLvList, List<EquiUpQuaBean> quaList, List<EquiClothesBean> clothesItemList, List<EquiRefreshBean> refreshList) {
		// 常量
		MAX_LV = upLvList.stream().mapToInt(e->e.getLv()).max().orElse(0);
		MAX_STRONGLV = strList.stream().mapToInt(e->e.getLv()).max().orElse(0);
		MAX_QUALITY = quaList.stream().mapToInt(e->e.getQuality()).max().orElse(0);
		MAX_EQUI_NUM = ConfigConsole.getIntVal(EConfigKey.MAX_EQUI_NUM);
		STR_BLESS_PROP = ConfigConsole.getIntVal(EConfigKey.BLESS_PROP); 
		UPLV_PROP = ConfigConsole.getIntVal(EConfigKey.EQUI_EXP); 
		QUA_BLESS_PROP = Arrays.stream("1006,1009,1012,1015,1018".split(",")).mapToInt(tid-> new Integer(tid)).boxed().collect(Collectors.toList());
		
		// 系统初始化赠送配置
		String equiInitStr = ConfigConsole.getVal(EConfigKey.EQUI_INIT_LIST);
		List<Integer> equiBoxData = Lists.newArrayList();
		Arrays.stream(equiInitStr.split(",")).forEach(eid-> equiBoxData.add(Integer.valueOf(eid)));
		EQUI_INIT_LIST = equiBoxData;
		
		// 基础数据
		equiMap = equiList.stream().collect(Collectors.toMap(EquiBean::getId, (e) -> e));
		equiUpStrMap = strList.stream().collect(Collectors.toMap(EquiUpStrBean::getLv, (e) -> e));
		equiUpLvMap = upLvList.stream().collect(Collectors.toMap(EquiUpLvBean::getLv, (e) -> e));
		equiUpQuaMap = quaList.stream().collect(Collectors.toMap(EquiUpQuaBean::getQuality, (e) -> e));
		clothesMap = clothesItemList.stream().collect(Collectors.toMap(EquiClothesBean::getTeamId, (e) -> e));
		refreshmap = refreshList.stream().collect(Collectors.toMap(EquiRefreshBean::getQuality, (e) -> e));
	}
	
	//-----------
	public static int getClothesUpQuaItem(int teamId) {
		if(clothesMap.containsKey(teamId)) {
			return clothesMap.get(teamId).getItemId();
		}
		return 0;
	}
	public static EquiClothesBean getClothesBean(int teamId) {
		return clothesMap.get(teamId);
	}
	public static EquiBean getEquiBean(int equiId) {
		return equiMap.get(equiId);
	}
	public static EquiUpLvBean getEquiUpLvBean(int lv) {
		return equiUpLvMap.get(lv);
	}
	public static EquiUpQuaBean getEquiUpQuaBean(int qua) {
		return equiUpQuaMap.get(qua);
	}
	public static EquiUpStrBean getEquiUpStrBean(int lv) {
		return equiUpStrMap.get(lv);
	}
	public static EquiRefreshBean getEquiRefreshBean(int qua) {
		return refreshmap.get(qua);
	}
	
	/**
	 * 计算套装加成
	 * 注意，不支持属性重叠
	 * @param equiList
	 * @return
	 */
	public static Map<EActionType, Float> getEquiSuitCapMap(Collection<Equi> equiList) {
		Map<EActionType, Float> capMap = Maps.newHashMap();
		int minQua = 0;//装备中最小品质等级
		for (Equi equi : equiList) {
			if (minQua == 0) {
				minQua = equi.getQuality();
			}else {
				minQua = equi.getQuality() < minQua ? equi.getQuality() : minQua;
			}
		}
		
		for(EquiUpQuaBean qua : equiUpQuaMap.values()) {
			if(qua.getSuitAddMap() == null || qua.getSuitAddMap().size() == 0) {
				continue;
			}
			
			if (minQua != qua.getQuality()) {
				continue;
			}
			
			// 计算是否满足套装
			int size = 0;
			for(Equi e : equiList) {
				if(e.getQuality() < qua.getQuality()) {//向下兼容前面品质的，如 3紫+1蓝 可以享受全套蓝装的加成
					break;
				}
				if(!qua.getSuitConfig().contains(e.getType())) {//判断是否是改套装类型
					break;
				}
				size++;
			}
			boolean isSuit = size == qua.getSuitConfig().size();
			if(!isSuit) {
				continue;
			}
			// 累计套装
			List<Map<EActionType, Float>> tempCap = Lists.newArrayList();
			equiList.stream().filter(e-> e.getQuality() >= qua.getQuality() && qua.getSuitConfig().contains(e.getType())).forEach(e-> {
				tempCap.add(jisuanCap(Maps.newHashMap(qua.getSuitAddMap()),  1 + EquiConsole.getEquiUpStrBean(e.getStrLv()).getAdd()));
			});
			// 计算属性综合
			for(Map<EActionType, Float> map : tempCap) {
				for(EActionType t : map.keySet()) {
					if(capMap.containsKey(t)) {
						capMap.put(t, capMap.get(t) + map.get(t));
					}else {
						capMap.put(t, map.get(t));
					}
				}
			} 
		}
		return capMap;
	}
	
	private static Map<EActionType, Float> jisuanCap(Map<EActionType, Float> tempCap, float beishu) {
		for(EActionType t : tempCap.keySet()) {
			tempCap.put(t, tempCap.get(t).floatValue() * beishu);
		}
		return tempCap;
	}
	
	/**
	 * 根据模板ID取装备类型
	 * @param equId
	 * @return
	 */
	public static Integer getEquiTypeByEid(int equId) {
		if(equiMap.containsKey(equId)) {
			return equiMap.get(equId).getType();
		}
		return 0;
	}

}
