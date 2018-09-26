package com.ftkj.manager.logic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.ao.logic.impl.PlayerInvestmentAO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.investment.TeamInvestment;
import com.ftkj.manager.investment.TeamInvestmentInfo;
import com.ftkj.manager.investment.TeamPlayerInvestment;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerInvestmentPB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年12月28日
 * 球员投资
 */
public class PlayerInvestmentManager extends BaseManager implements IRedPointLogic{

	
	@IOC
	private TeamManager teamManager;
	@IOC
	private TeamMoneyManager teamMoneyManager;
	@IOC
	private TaskManager taskManager;
	
	@IOC
	private PlayerInvestmentAO playerInvestmentAO;
	
	private Map<Long,TeamInvestment> teamMap;

	private static final int JF =1;
	
	
	private int _buyTotal;
	private int _maxTotal;
	private int _maxBuyCount;
	private int _buyMoneyFK;
	private int _buyMoneyJF;
	private int _money;
	/**
	 * 提现倍数
	 */
	private int _getMoney;
	
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_showPlayerInvestmentMain)
	public void showPlayerInvestmentMain(){
		long teamId = getTeamId();
		TeamInvestment ti = getTeamInvestment(teamId);
		//
		List<PlayerInvestmentPB.TeamPlayerInvestmentData> players = ti.getPlayerMap().values().stream().map(player->getTeamPlayerInvestmentData(player)).collect(Collectors.toList());
		sendMessage(PlayerInvestmentPB.TeamInvestmentMainData.newBuilder()
			.setTeamInfo(getTeamInvestmentInfoData(ti))
			.addAllPlayers(players)
			.build());
	}
	
	
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_buyPlayer)
	public void buyPlayer(int playerId,int num){
		long teamId = getTeamId();
		TeamInvestment ti = getTeamInvestment(teamId);
		//
		PlayerBean playerBean = PlayerConsole.getPlayerBean(playerId);
		int price = playerBean.getPrice();
		TeamInvestmentInfo info = ti.getInfo();
		int totalPrice = price * num ;
		totalPrice = (int)Math.ceil(totalPrice * 1.01f);
		if(info.getMoney()<totalPrice){//钱不够
			log.debug("PlayerInvestment-buyPlayer[money < price]");
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(ti.getTotal()+num>info.getMaxTotal()){//超出最大持有量
			log.debug("PlayerInvestment-buyPlayer[curTotal >= maxTotal]");
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		info.updateMoney(-totalPrice);
		//
		TeamPlayerInvestment player = ti.addPlayer(playerId, price, num);
//		log.debug("-------------------------->{},{}",player.getTotal(),num);
		sendMessage(getTeamPlayerInvestmentData(player));
		taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.球员投资.getName());
	}
	
	
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_salePlayer)
	public void salePlayer(int playerId,int num){
		long teamId = getTeamId();
		TeamInvestment ti = getTeamInvestment(teamId);
		//
		PlayerBean playerBean = PlayerConsole.getPlayerBean(playerId);
		int price = playerBean.getPrice();
		TeamInvestmentInfo info = ti.getInfo();
		TeamPlayerInvestment player = ti.getPlayer(playerId);
		if(player == null || player.getTotal()<=0){
			log.debug("PlayerInvestment-salePlayer[player=null||0]");
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
		//判断出售的手数是否足够
		if (player.getTotal() < num) {
			sendErrorCode(ErrorCode.Player_Investment_Total_Not_Enough);
			return;
		}
		
		int saleMoney = price * num;
		saleMoney = (int) (saleMoney -  Math.ceil((saleMoney * player.getRate())));
		info.updateMoney(saleMoney);
		player.sale(num, price);
		sendMessage(getTeamPlayerInvestmentData(player));
		taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.球员投资.getName());
	}
	
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_buyMaxTotal)
	public void buyMaxTotal(){
		long teamId = getTeamId();
		TeamInvestment ti = getTeamInvestment(teamId);
		TeamInvestmentInfo info = ti.getInfo();
		int max = info.getMaxTotal() + 10;
		int needMoney  = Math.round(18+(30+max)*(max/10f-2)/2f);
		
		if(info.getMaxTotal()>=_maxTotal){
			log.debug("PlayerInvestment-buyMaxTotal[curTotal>=maxTotal]");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
//		Team team = teamManager.getTeam(teamId);
		if(!teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球员投资, "buyMaxTotal"))){
			log.debug("PlayerInvestment-buyMaxTotal[money<needMoney]");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		info.updateMaxTotal(_buyTotal);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_buyMoney)
	public void buyMoney(int type,int num){
		long teamId = getTeamId();
		if(num<=0 || num >=999999){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		int needFK = 0;
		int needJF = 0;
		int count = getBuyCount(teamId) + num;
		if(type == JF){//使用经费，增加次数限制验证
			if(count >_maxBuyCount){
				log.debug("PlayerInvestment-buyMoney[dayCount>=maxDayCount]");
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
				return;
			}
			needJF = _buyMoneyJF*num;
		}else{
			needFK = _buyMoneyFK*num;
		}
		//
		if(!teamMoneyManager.updateTeamMoney(teamId, -needFK, -needJF, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球员投资, "buyMoney"))){
			log.debug("PlayerInvestment-buyMoney[money<needMoney]");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		//
		TeamInvestment ti = getTeamInvestment(teamId);
		TeamInvestmentInfo info = ti.getInfo();
		info.updateMoney(_money*num);
		if(type == JF){
			saveBuyCount(teamId,count);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 * 提现
	 * @param num
	 */
	@ClientMethod(code = ServiceCode.PlayerInvestmentManager_getMoney)
	public void getMoney(int num){
		long teamId = getTeamId();
		TeamInvestment ti = getTeamInvestment(teamId);
		TeamInvestmentInfo info = ti.getInfo();
		int money = _getMoney * num;
		if(info.getMoney()<money){
			log.debug("PlayerInvestment-getMoney[money<needMoney]");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(info.getMoney() - money < _getMoney) {
			log.debug("PlayerInvestment-getMoney[money<needMoney]");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		//
		info.updateMoney(-money);
		teamMoneyManager.updateTeamMoney(teamId, 0, money, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球员投资, "getMoney"));
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * 自动出售
	 * @param teamId
	 */
	public void autoSale(long teamId){
		TeamInvestment ti = getTeamInvestment(teamId);
		ti.getPlayerMap().values().stream()
				.filter(player->player.isEnd())
				.forEach(player->player.sale(player.getTotal(), PlayerConsole.getPlayerBean(player.getPlayerId()).getPrice()));
		
		
		
		
	}
	
	private int getBuyCount(long teamId){
		String key = RedisKey.getDayKey(teamId, RedisKey.PlayerInvestment_Buy);
		return redis.getIntNullIsZero(key);
	}

	
	private void saveBuyCount(long teamId,int count){
		String key = RedisKey.getDayKey(teamId, RedisKey.PlayerInvestment_Buy);
		redis.set(key, count+"",RedisKey.DAY);
	}
	
	private PlayerInvestmentPB.TeamPlayerInvestmentData getTeamPlayerInvestmentData(TeamPlayerInvestment player){
		return PlayerInvestmentPB.TeamPlayerInvestmentData.newBuilder().setBasePrice(player.getBasePrice())
							.setBuyDay(player.getBuyDay())
							.setPlayerId(player.getPlayerId())
							.setTotal(player.getTotal())
							.build();
	}
	
	private PlayerInvestmentPB.TeamInvestmentInfoData getTeamInvestmentInfoData(TeamInvestment ti){
		TeamInvestmentInfo info = ti.getInfo();
		return PlayerInvestmentPB.TeamInvestmentInfoData.newBuilder()	
								.setMaxTotal(info.getMaxTotal())
								.setMoney(info.getMoney())
								.setBuyCount(getBuyCount(info.getTeamId()))
								.build();
	}
	
	private TeamInvestment getTeamInvestment(long teamId){
		TeamInvestment ti = teamMap.get(teamId);
		if(ti == null){
			TeamInvestmentInfo info = playerInvestmentAO.getTeamInvestmentInfo(teamId);
			if(info == null)
				info = new TeamInvestmentInfo(teamId);
			List<TeamPlayerInvestment> players = playerInvestmentAO.getTeamPlayerInvestments(teamId);
			Map<Integer,TeamPlayerInvestment> playerMap = players.stream().collect(Collectors.toMap(TeamPlayerInvestment::getPlayerId, val->val));
			ti = new TeamInvestment(info, playerMap);
			teamMap.put(teamId, ti);
		}
		return ti;
	}
	
	
	@Override
	public RedPointParam redPointLogic(long teamId) {
		int code = 0;
		TeamInvestment ti = getTeamInvestment(teamId); 
		code = ti.getPlayerMap().values().stream().filter(player->player.getBuyDay()>=4).findFirst().isPresent()?1:0;
		RedPointParam pp = new RedPointParam(teamId, ERedPoint.球员投资.getId(), code);
		return pp;
	}


	@Override
	public void initConfig() {
		_buyTotal =10;
		_maxTotal =80;
		_maxBuyCount=25;
		_buyMoneyFK=10;
		_buyMoneyJF=100;
		_money=100;
		_getMoney=100; // 提现倍数
	}


	@Override
	public void instanceAfter() {
		teamMap = Maps.newConcurrentMap();
	}

}
