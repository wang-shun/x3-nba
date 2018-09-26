package com.ftkj.manager.logic;

import java.util.List;
import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ShopConsole;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.shop.BDMoneyShopBean;
import com.ftkj.manager.shop.LeagueShopBean;
import com.ftkj.manager.shop.MoneyShopBean;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.ShopPB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;

public class ShopManager extends BaseManager {

	@IOC
	private LeagueManager leagueManager;

	@IOC
	private PropManager propManager;
	
	@IOC
	private TeamMoneyManager teamMoneyManager;
	
	@IOC
	private TaskManager taskManager;
	
	@IOC
	private PlayerManager playerManager;
	
	/**
	 * 显示联盟商城界面
	 */
	@ClientMethod(code = ServiceCode.ShopManager_showLeagueShop)
	public void showLeagueShop(){
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId() == 0){
			sendMessage(ShopPB.LeagueShopMain.newBuilder()
					.setCurFeats(0)
					.setCurScore(0)
					.setLimitScore(0)
					.build());
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		int leagueId = lt.getLeagueId();
		League league = leagueManager.getLeague(leagueId);
		if(league == null){
			sendMessage(ShopPB.LeagueShopMain.newBuilder()
					.setCurFeats(0)
					.setCurScore(0)
					.setLimitScore(0)
					.build());
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]",teamId,leagueId);
			return ;
		}
		List<ShopPB.ShopPropData> dataList = Lists.newArrayList();
//		redis.del(RedisKey.League_Shop_Prop + leagueId + "_"+teamId);
		
		Map<Integer,Integer> propMap = redis.getMapAllKeyValues(RedisKey.getDayKey(teamId, RedisKey.Shop_League_Prop + leagueId + "_"));
		if(propMap != null)
			propMap.forEach((k,v)->dataList.add(getShopPropData(k,v)));
		
		sendMessage(ShopPB.LeagueShopMain.newBuilder()
							.setCurFeats(lt.getFeats())
							.setCurScore(lt.getScore())
							.setLimitScore(league.getShopLimit())
							.addAllProps(dataList)
							.build());
	}
	
	
	@ClientMethod(code = ServiceCode.ShopManager_showMoneyShop)
	public void showMoneyShop(){
		long teamId = getTeamId();
		Map<Integer,Integer> propMap = redis.getMapAllKeyValues(RedisKey.Shop_Money_Prop + teamId);
		List<ShopPB.ShopPropData> dataList = Lists.newArrayList();
		if(propMap != null)
			propMap.forEach((k,v)->dataList.add(getShopPropData(k,v)));
		sendMessage(ShopPB.MoneyShopMain.newBuilder().addAllProps(dataList).build());
	}
	
	@ClientMethod(code = ServiceCode.ShopManager_showBDMoneyShop)
	public void showBDMoneyShop(){
		long teamId = getTeamId();
		Map<Integer,Integer> propMap = redis.getMapAllKeyValues(RedisKey.getDayKey(teamId,RedisKey.Shop_BDMoney_Prop));
		List<ShopPB.ShopPropData> dataList = Lists.newArrayList();
		if(propMap != null)
			propMap.forEach((k,v)->dataList.add(getShopPropData(k,v)));
		sendMessage(ShopPB.MoneyShopMain.newBuilder().addAllProps(dataList).build());
	}
	
	@ClientMethod(code = ServiceCode.ShopManager_buyBDMoneyProp)
	public void buyBDMoneyProp(int propId,int num){
		long teamId = getTeamId();
		if(propId<=0 || num <=0 || num >=99999) {//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		BDMoneyShopBean bean = ShopConsole.getBDMoneyShopBean(propId);
		if(bean == null){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		int needMoney = bean.getMoney() * num;
		
		Integer curCount = redis.hget(RedisKey.getDayKey(teamId,RedisKey.Shop_BDMoney_Prop), propId);
		if(curCount!=null && curCount >= bean.getDayLimit()){//超过购买上限
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		
		boolean ok = teamMoneyManager.updateTeamMoney(teamId, 0, 0, 0, -needMoney, true, ModuleLog.getModuleLog(EModuleCode.商城, "绑定球券商城"));
		if(ok){
			int count = curCount==null?num:curCount+num;
			redis.putMapValueExp(RedisKey.getDayKey(teamId,RedisKey.Shop_BDMoney_Prop), propId, count);
			propManager.addProp(teamId, new PropSimple(propId, num * bean.getNum()), true, ModuleLog.getModuleLog(EModuleCode.商城, "绑定球券商城"));
			taskManager.updateTask(teamId, ETaskCondition.商城购买, 1,propId+","+num);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
			return;
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
		
	}
	
	
	@ClientMethod(code = ServiceCode.ShopManager_buyMoneyProp)
	public void buyMoneyProp(int propId,int num){
		long teamId = getTeamId();
		if(propId<=0 || num <=0 || num>99999) {//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
			return;
		}
		MoneyShopBean bean = ShopConsole.getMoneyShopBean(propId);
		if(bean == null){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_3.code).build());
			return;
		}
 		int needMoney = bean.getMoney() * num;
		Integer curCount = redis.hget(RedisKey.getDayKey(teamId,RedisKey.Shop_Money_Prop), propId);
		curCount = curCount==null ? 0 :curCount;
		if(curCount + num > bean.getDayLimit()){//超过购买上限
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_4.code).build());
			return;
		}
		
		boolean ok = teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.商城, "球券商城"));
		if(ok){
			int count = curCount==null?1:curCount+num;
			redis.putMapValueExp(RedisKey.getDayKey(teamId,RedisKey.Shop_Money_Prop), propId, count);
			List<PropSimple> propList = Lists.newArrayList();
			propList.add(new PropSimple(propId, num * bean.getNum()));
			propManager.addPropList(teamId, propList, true, ModuleLog.getModuleLog(EModuleCode.商城, "球券商城"));
			taskManager.updateTask(teamId, ETaskCondition.商城购买, 1,propId+","+num);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
			return;
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
	}
	
	
	/**
	 * 购买联盟商城物品
	 * @param propId
	 * @param num
	 */
	@ClientMethod(code = ServiceCode.ShopManager_buyLeagueProp)
	public void buyLeagueProp(int propId,int num){
		long teamId = getTeamId();
		if(propId<=0 || num <=0 || num >=99999) {//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId() == 0){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		int leagueId = lt.getLeagueId();
		League league = leagueManager.getLeague(leagueId);
		if(league == null){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]",teamId,leagueId);
			return ;
		}
		if(lt.getScore()<league.getShopLimit()){//没有达到最低贡献值
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		LeagueShopBean shop = ShopConsole.getLeagueShopBean(propId);
		if(shop == null){//购买的道具不在商城中
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		if(league.getLeagueLevel()<shop.getLevelLimit()){//联盟等级没有达到购买要求
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		int needFeats = shop.getFeats() * num;
		if(lt.getFeats()<needFeats){//功勋不足
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		Integer curCount = redis.hget(RedisKey.getDayKey(teamId, RedisKey.Shop_League_Prop + leagueId + "_"), propId);
		if(curCount!=null && curCount+num > shop.getDayLimit()){//超过购买上限
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		int count = curCount==null?1:curCount+num;
		redis.putMapValueExp(RedisKey.getDayKey(teamId, RedisKey.Shop_League_Prop + leagueId + "_"), propId, count);
		lt.updateFeats(-needFeats);
		propManager.addProp(teamId, new PropSimple(shop.getProp().getPropId(), shop.getNum() * num), true, ModuleLog.getModuleLog(EModuleCode.商城, "联盟商城"));
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	public static ShopPB.ShopPropData getShopPropData(int propId,int num){
		return ShopPB.ShopPropData.newBuilder().setNum(num).setPropId(propId)
				.build();
	}
	
	@Override
	public void instanceAfter() {

	}

}
