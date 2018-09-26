package com.ftkj.console;

import com.ftkj.cfg.card.PlayerCardCompositeBean;
import com.ftkj.cfg.card.PlayerCardGradeCap;
import com.ftkj.cfg.card.PlayerCardGroupBean;
import com.ftkj.cfg.card.PlayerCardStarLvExpBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.RandomUtil;

import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:球星卡控制台
 * @author Jay
 * @time:2017年4月11日 下午7:29:28
 */
public class PlayerCardConsole {
	
	/**
	 * 卡组：卡组配置
	 */
	private static Map<Integer, PlayerCardGroupBean> cardGroupMap;
	/**
	 * 星级经验配置 : 卡类型，等级经验配置
	 */
	private static Map<Integer, List<PlayerCardStarLvExpBean>> groupStarLvMap;
	/**
	 * key = type_qua_starlv
	 */
	private static Map<String, PlayerCardGradeCap> playerGradeCap;
	/**
	 * 碎片合成配置
	 * tab：合成消耗
	 */
	private static Map<Integer, PropSimple> compositeMap;
	
	public static void init(List<PlayerCardGroupBean> groupList, List<PlayerCardCompositeBean> player_Card_Composite, List<PlayerCardStarLvExpBean> player_Card_StarLvExp, List<PlayerCardGradeCap> playerGradCapList) {
		// 球星卡配置
		cardGroupMap = Maps.newHashMap();
		groupList.stream().forEach(g-> cardGroupMap.put(g.getType(), g));
		// 碎片合成配置
		compositeMap = Maps.newHashMap();
		for(PlayerCardCompositeBean bean : player_Card_Composite) {
			compositeMap.put(bean.getTab(), bean.getCompositePro());
		}
		// 星级经验配置
		groupStarLvMap = player_Card_StarLvExp.stream().collect(Collectors.groupingBy(v->v.getType(),Collectors.toList()));
		// 加成配置
		playerGradeCap = Maps.newHashMap();
		playerGradCapList.stream().forEach(g-> {
			playerGradeCap.put(getKey(g.getType(), g.getQuality(), g.getStarLv()), g);
		});
	}
	
	private static String getKey(int... val) {
		StringBuilder sb = new StringBuilder();
		for(int v : val) {
			sb.append(v).append("_");
		}
		return sb.deleteCharAt(sb.length()-1).toString();
	}
	
	/**
	 * 取卡组配置
	 * @param type
	 * @return
	 */
	public static PlayerCardGroupBean getPlayerCardGroup(int type) {
		return cardGroupMap.get(type);
	}
	
//	/**
//	 * 取品质配置
//	 * @param qua
//	 * @return
//	 */
//	public static PlayerCardQuaBean getPlayerCardQua(int qua) {
//		return cardQuaMap.get(qua);
//	}
	
	/**
	 * 制卡随机得到一个掉落的卡组
	 * 过滤不包含球员的卡组
	 * @ has 是否已有全套卡
	 * @return
	 */
	public static PlayerCardGroupBean getMakeCardRandGroup(int playerId, boolean has) {
		// 没有全套必中全套
		if(!has) {
			return cardGroupMap.get(0);
		}
		//
		Map<PlayerCardGroupBean, Integer> rate = Maps.newHashMap();
		int totalRate = 0;
		for(PlayerCardGroupBean bean : cardGroupMap.values()) {
			if(bean.getType()!=0 && bean.getPlayerList().contains(playerId) && bean.getRate()>0) {
				rate.put(bean, bean.getRate());
				totalRate += bean.getRate();
			}
		}
		// 全套卡概率做总概率，减去其他卡组的概率，就是全套卡经验的概率
		PlayerCardGroupBean bean = cardGroupMap.get(0);
		rate.put(bean, bean.getRate() - totalRate);
		return RandomUtil.randMap(rate);
	}
	
	/**
	 * 合成卡配置
	 * @param tab
	 * @return
	 */
	public static PlayerCardGroupBean getCompositeCardByTab(int tab) {
		Map<PlayerCardGroupBean, Integer> rate = Maps.newHashMap();
		for(PlayerCardGroupBean bean : cardGroupMap.values()) {
			if(bean.getTab() == tab) {
				rate.put(bean, bean.getTab());
			}
		}
		return RandomUtil.randMap(rate);
	}
	
	/**
	 * 取合成的配置
	 * @param tab
	 * @return
	 */
	public static PropSimple getCompositeByTab(int tab) {
		if(compositeMap.containsKey(tab)) {
			return compositeMap.get(tab);
		}
		return null;
	}
	
	/**
	 * 是否可以升级星级
	 * @param type
	 * @return
	 */
	public static boolean isUpStarLv(int type) {
		return groupStarLvMap.containsKey(type);
	}
	
//	/**
//	 * 最大星级
//	 * @param type
//	 * @return
//	 */
//	public static int getMaxStarLv(int type) {
//		if(groupStarLvMap.containsKey(type)) {
//			return groupStarLvMap.get(type).stream().max(Comparator.comparing(PlayerCardStarLvExpBean::getStarLv)).get().getStarLv();
//		}
//		return 0;
//	}
	
	/**
	 * 升级到下一级所需要的总经验
	 * @param type 卡组类型
	 * @param currLv 当前等级
	 * @return
	 */
	@Deprecated
	public static int getNextStarLvNeed(int type, int quality, int currLv) {
		PlayerCardStarLvExpBean bean = getStarLvMap(type, quality, currLv);
		if(bean == null) return 0;
		return bean.getTotalExp();
	}
	
	/**
	 * 一键注入所需要的经验
	 * @param type
	 * @param quality
	 * @param totalExp 当前经验 + 注入经验
	 * @return int 星级
	 */
	public static int getOneKeyStarLvNeed(int type, int quality, int totalExp) {
		PlayerCardStarLvExpBean bean = groupStarLvMap.get(type).stream()
		.filter(s-> s.getQuality() == quality)
		.filter(s-> s.getTotalExp() <= totalExp)
		.sorted(Comparator.comparingInt(PlayerCardStarLvExpBean::getTotalExp).reversed()).findFirst().orElse(null);
		return bean.getStarLv();
	}
	
	/**
	 * 当前星级突破需要的底薪数量
	 * @param type
	 * @param currLv
	 * @return
	 */
	public static int getNextStarLvNeedLowPrice(int type, int quality, int currLv) {
		PlayerCardStarLvExpBean bean = getStarLvMap(type, quality, currLv);
		return bean.getNeedLowPrice();
	}
	
	private static PlayerCardStarLvExpBean getStarLvMap(int type, int quality, int currLv) {
		PlayerCardStarLvExpBean bean = groupStarLvMap.get(type).stream().filter(s-> s.getStarLv()==currLv && s.getQuality() == quality).findFirst().orElse(null);
		return bean;
	}
	
//	/**
//	 * 根据卡组收集星级张数来取攻防
//	 * @param type
//	 * @param starLv
//	 * @param num
//	 * @return intp[]{进攻，防守}
//	 */
//	public static int[] getGroupCollectCap(int type, int starLv, long num) {
//		PlayerCardCapBean bean = groupCapMap.get(type).stream()
//				.filter(s-> s.getStarLv() == starLv)
//				.filter(s-> num >= s.getStartNum() && num <= s.getEndNum()).findFirst().orElse(null);
//		if(bean == null) {
//			return new int[]{0, 0};
//		}
//		return new int[]{bean.getAtkCap(), bean.getDefCap()};
//	}
	
	/**
	 * 取卡组最大星级，现在是只有全套卡是5级，其他事0级
	 * @param type
	 * @return
	 */
	public static int getGroupTypeMaxStarLv(int type) {
		if(type == 0) {
			return 5;
		}
		return 0;
	}
	
//	/**
//	 * 取球星卡的基础攻防
//	 * @param type
//	 * @param qua
//	 * @param starlv
//	 * @param grade
//	 * @return float[] 0进攻比例， 1防守比例
//	 */
//	public static float getPlayerCardCap(int type, int qua, int starlv) {
//		PlayerCardCapBean2 bean = cardCapList.stream().filter(c-> c.getType() == type && c.getQulity() == qua && c.getStarLv() == starlv)
//		.findFirst().orElse(null);
//		if(bean == null) {
//			return 0f;
//		}
//		return bean.getCap();
//	}
	/**
	 * 取攻防比例系数
	 * @param type
	 * @param qua
	 * @param starLv
	 * @return
	 */
	public static PlayerCardGradeCap getPlayerGradeCap(int type, int qua, int starLv) {
		return playerGradeCap.get(getKey(type, qua, starLv));
	}
}
