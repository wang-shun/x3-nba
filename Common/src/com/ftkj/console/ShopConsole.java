package com.ftkj.console;

import java.util.Map;

import com.ftkj.cfg.TeamPriceMoneyBean;
import com.ftkj.manager.shop.BDMoneyShopBean;
import com.ftkj.manager.shop.LeagueShopBean;
import com.ftkj.manager.shop.MoneyShopBean;
import com.google.common.collect.Maps;


public class ShopConsole {
	private static Map<Integer,LeagueShopBean> leagueShopMap;
	private static Map<Integer,MoneyShopBean> moneyShopMap;
	private static Map<Integer,BDMoneyShopBean> bdmoneyShopMap;
	
	private static Map<Integer,TeamPriceMoneyBean> teamPriceMoneyBeanMap;
	
	
	
	
	
	public static void init(){
		
		Map<Integer,LeagueShopBean> leagueShopMapTmp = Maps.newHashMap();
		Map<Integer,MoneyShopBean> moneyShopMapTmp = Maps.newHashMap();
		Map<Integer,BDMoneyShopBean> bdmoneyShopMapTmp = Maps.newHashMap();
		Map<Integer,TeamPriceMoneyBean> teamPriceMoneyBeanMapTmp  = Maps.newHashMap();
		CM.leagueShopList.forEach(shop->{
			LeagueShopBean bean = new LeagueShopBean(PropConsole.getProp(shop.getPropId()),
					shop.getNum(), shop.getDayLimit(), shop.getFeats(), shop.getLevelLimit());
			leagueShopMapTmp.put(bean.getProp().getPropId(), bean);
		});
		CM.moneyShopList.forEach(shop->{
			MoneyShopBean bean = new MoneyShopBean(PropConsole.getProp(shop.getPropId()), shop.getNum(), shop.getDayLimit(), shop.getMoney());
			moneyShopMapTmp.put(bean.getProp().getPropId(), bean);
		});
		CM.bdmoneyShopList.forEach(shop->{
			BDMoneyShopBean bean = new BDMoneyShopBean(PropConsole.getProp(shop.getPropId()), shop.getNum(), shop.getDayLimit(), shop.getBdMoney());
			bdmoneyShopMapTmp.put(bean.getProp().getPropId(), bean);
		});
		
		CM.teamPriceMoneyList.forEach(p->teamPriceMoneyBeanMapTmp.put(p.getCount(), p));
		
		leagueShopMap = leagueShopMapTmp;
		moneyShopMap = moneyShopMapTmp;
		teamPriceMoneyBeanMap = teamPriceMoneyBeanMapTmp;
		bdmoneyShopMap = bdmoneyShopMapTmp;
	}
	
	public static LeagueShopBean getLeagueShopBean(int propId){
		return leagueShopMap.get(propId);
	}
	
	public static MoneyShopBean getMoneyShopBean(int propId){
		return moneyShopMap.get(propId);
	}
	public static BDMoneyShopBean getBDMoneyShopBean(int propId){
		return bdmoneyShopMap.get(propId);
	}
	
	public static TeamPriceMoneyBean getTeamPriceMoneyBean(int count){
		return teamPriceMoneyBeanMap.get(count);
	}
	
}
