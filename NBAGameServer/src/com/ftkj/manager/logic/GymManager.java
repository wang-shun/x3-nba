package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.GymConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.dao.logic.GymDAO;
import com.ftkj.db.domain.ArenaPlayerPO;
import com.ftkj.db.domain.TeamArenaConstructionPO;
import com.ftkj.db.domain.TeamArenaPO;
import com.ftkj.enums.EArenaCType;
import com.ftkj.enums.EArenaRollType;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.gym.ArenaBean;
import com.ftkj.manager.gym.ArenaConstructionBean;
import com.ftkj.manager.gym.ArenaPlayer;
import com.ftkj.manager.gym.ArenaPlayerTeam;
import com.ftkj.manager.gym.ArenaRoll;
import com.ftkj.manager.gym.ArenaRollItem;
import com.ftkj.manager.gym.TeamArena;
import com.ftkj.manager.gym.TeamArenaConstruction;
import com.ftkj.manager.gym.TeamArenaCross;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.proto.ArenaPB;
import com.ftkj.proto.ArenaPB.ArenaPlayerTeamMainData;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.task.IRPCTask;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年6月20日
 * 玩家球馆（跨服玩法）
 */
public class GymManager extends BaseManager implements IRedPointLogic, OfflineOperation{

	
	@IOC
	private PlayerManager playerManager;
	
	@IOC
	private TeamManager teamManager;
	
	@IOC
	private NodeManager nodeManager;
	
	@IOC
	private GymDAO arenaAO;
	
	@IOC
	private BuffManager buffManager;
	
	@IOC
	private TaskManager taskManager;
	
	@IOC
	private TeamMoneyManager teamMoneyManager;
	
	private int _powerMoney;
	
	private Map<Long,TeamArena> teamAreanaMap;
	private Map<Long,ArenaPlayerTeam> teamAreanaPlayerMap;
	
	
	private ArenaPB.ArenaAttackMainData _helpArenaData;
	private List<TeamArenaCross> _helpArenaList;
	
	/**
	 * 显示球馆转盘主界面
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showArenaMain)
	public void showArenaMain(){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
//		ta.updatePower(10);
		sendMessage(ArenaPB.ArenaMainData.newBuilder()
							.setTeamData(getArenaTeamData(ta))
							.build());
	}
	
	/**
	 * 显示球馆建筑
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showArenaConstructionMain)
	public void showArenaConstructionMain(){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		List<ArenaPB.ArenaConstructionData> cList = Lists.newArrayList();
		ta.getcMap().values().forEach(c->cList.add(getArenaConstructionData(c)));
		sendMessage(ArenaPB.ArenaConstructionMainData.newBuilder().addAllCList(cList).build());
	}
	
	/**
	 * 显示球员之魂列表
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showArenaPlayerTeamMain)
	public void showArenaPlayerTeamMain(){
		long teamId = getTeamId();
		ArenaPlayerTeam apt = getTeamArenaPlayer(teamId);
		List<ArenaPB.ArenaPlayerData> players = apt.getPlayers().stream().filter(player->player.getPlayerId()>0).map(player->getArenaPlayerData(player)).collect(Collectors.toList());
		sendMessage(ArenaPlayerTeamMainData.newBuilder().addAllPlayers(players).build());
	}
	
	private ArenaPB.ArenaPlayerData getArenaPlayerData(ArenaPlayer player){
		return ArenaPB.ArenaPlayerData.newBuilder()
							.setGrade(player.getGrade().ordinal())
							.setPid(player.getPid())
							.setPlayerId(player.getPlayerId())
							.setPosition(player.getPosition())
							.setTid(player.getTid())
							.build();
		
	}
	
	
	/**
	 * 升级修复建筑
	 * @param cid
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_levelupConstruction)
	public void levelupConstruction(int cid){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		synchronized (ta) {
			TeamArenaConstruction tc = ta.getTeamArenaConstruction(EArenaCType.getEArenaCType(cid));
			if(tc == null){//cid错误
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
				return;
			}
			ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
			if(bean == null){//没有该级别的建筑配置信息
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_1.code).build());
				return;
			}
			ArenaConstructionBean acBean = bean.getConstruction(tc.getcId());
			int nextGold = acBean.getNextGold(tc.getCurGold());
			if(nextGold==0){//当前已经是最高等级
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
				return;
			}
			int needGold = nextGold - tc.getCurGold();//取得当前升级或者修复需要的金币数量
			if(ta.getGold()<needGold){//金币不足
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
				return;
			}
			//
			ta.updateGold(-needGold);
			tc.updateGold(needGold);
			taskManager.updateTask(teamId, ETaskCondition.球馆建筑升级, 1, EModuleCode.球馆建筑升级.getName());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		}
	}
	
	/**
	 * 上阵守护球员
	 * @param cid
	 * @param pid
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_lineupPlayer)
	public void lineupPlayer(int cid,int pid){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		TeamArenaConstruction tc = ta.getTeamArenaConstruction(EArenaCType.getEArenaCType(cid));
		if(tc == null){//cid错误
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
			return;
		}
		ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
		if(bean == null){//没有该级别的建筑配置信息
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_1.code).build());
			return;
		}
		ArenaConstructionBean acBean = bean.getConstruction(tc.getcId());
		ArenaPlayerTeam apt = getTeamArenaPlayer(teamId);
		List<ArenaPlayer> list = apt.getPlayers();
				
		ArenaPlayer player = list.stream().filter(p->p.getPid() == pid).findFirst().orElse(null);
		if(player == null || player.getPlayerId()==0){//上阵守护球员不存在
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_2.code).build());
			return;
		}
		if(acBean.getCid().getPosition()!=player.getPosition()){//球员位置与守护的建筑不匹配
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_3.code).build());
			return;
		}
		if(tc.getPlayerGrade().ordinal()>player.getGrade().ordinal()){//守护的老球员等级比要上阵的牛逼
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_7.code).build());
			return;
		}
		//
		tc.updatePlayer(player);
		list.remove(player);
		player.del();
		taskManager.updateTask(teamId, ETaskCondition.球馆球员上阵, 1, player.getGrade() + "");
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 * 升级球馆
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_levelupArena)
	public void levelupArena(){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		List<TeamArenaConstruction> list = Lists.newArrayList(ta.getcMap().values());
		
		ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());

		ArenaConstructionBean acBean = null;
		TeamArenaConstruction tc = null;
		for(int i = 0 ; i < list.size() ; i++){
			tc = list.get(i);
			acBean = bean.getConstruction(tc.getcId());
			if(tc.getCurGold() < acBean.getGold5()
					|| tc.getPlayerId()==0){//有建筑没有满级,或者没有守护球员
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_2.code).build());
				return;
			}
		}
		if(GymConsole.getArenaBean(ta.getLevel()+1) == null){//建筑已经满级了~
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_4.code).build());
			return;
		}
		//升级球馆成功
		ta.updateLevel(1);
		list.forEach(c->c.levelup());
		taskManager.updateTask(teamId, ETaskCondition.球馆升级, ta.getLevel(), "");
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * roll转盘
	 */
	@SuppressWarnings("unchecked")
	@ClientMethod(code = ServiceCode.ArenaManager_roll)
	public void roll(){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		int curPower = ta.getPower();//刷新并将当前的能量取出来~
		if(curPower<=0){//次数不足
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_8.code).build());
			return;
		}
		
		ArenaRoll roll = GymConsole.getArenaRoll(ta.getLevel());
		ArenaRollItem item = null; 
		Team team = teamManager.getTeam(teamId);
		boolean helpStep = team.getHelp().indexOf("l=7_120005")>=0;
		if(helpStep){
			item = roll.gmRoll(EArenaRollType.偷取);
		}
		//
		if(item == null)
			item = roll.roll();
		
		ta.updatePower(-1);
		if(item.getType() == EArenaRollType.偷取){
			
			if(helpStep){
				ta.setSteal(_helpArenaList);
				ta.updateStealCount(1);
				sendMessage(teamId,_helpArenaData,ServiceCode.Push_Arena_Steal_Team);
			}else{
	//			TODO:增加一次偷取次数发送请求到跨服服务器上拉取3个球队
				RPCLinkedTask.build().appendTask((tid,maps,args)->{//随机一个逻辑节点，拉取一名玩家的球馆信息
					RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_ranTeamArena, null,tid,3,EArenaRollType.偷取);
				}).appendTask((tid,maps,args)->{
					List<TeamArenaCross> list = (ArrayList)args[0];
					ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
					List<ArenaPB.ArenaAttackTeamData> dataList = Lists.newArrayList();
					list.forEach(tac->dataList.add(getArenaAttackTeamData(tac,bean.getStealGold())));
					ta.setSteal(list);
					ta.updateStealCount(1);
					//
					ArenaPB.ArenaAttackMainData data = ArenaPB.ArenaAttackMainData.newBuilder().addAllTeams(dataList).build();
					sendMessage(teamId,data,ServiceCode.Push_Arena_Steal_Team);
				}).start();
			}
		}else if(item.getType() == EArenaRollType.攻击){
			//TODO:增加一次可攻击次数,从跨服服务器上拉取1个球队
			RPCLinkedTask.build().appendTask((tid,maps,args)->{//随机一个逻辑节点，拉取一名玩家的球馆信息
				RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_ranTeamArena, null,tid,1,EArenaRollType.攻击);
			}).appendTask((tid,maps,args)->{//执行攻击操作的回调
				List<TeamArenaCross> list = (ArrayList)args[0];
				TeamArenaCross otherTA = list.get(0);
				//将命中的跨服攻击玩家数据缓存到本地
				ta.setAttack(otherTA);
				ta.updateAttackCount(1);
				ArenaPB.ArenaAttackMainData data = ArenaPB.ArenaAttackMainData.newBuilder().addTeams(getArenaAttackTeamData(otherTA)).build();
				//将玩家的球馆数据推送到前台，进攻界面推送
				sendMessage(teamId,data,ServiceCode.Push_Arena_Attack_Team);
			}).start();
		}else if(item.getType() == EArenaRollType.数值增加){
			if(item.getDefend()!=0){
				if(ta.getDefend()<3){
					ta.updateDefend(item.getDefend());
				}else{
					ta.updatePower(1);
				}
			}
			if(item.getGold()!=0){
				ta.updateGold(item.getGold());
			}
			if(item.getPower()!=0){
				ta.updatePower(item.getPower());
			}
		}
	
		taskManager.updateTask(teamId, ETaskCondition.球馆转盘, 1, EModuleCode.球馆转盘.getName());
		sendMessage(DefaultPB.DefaultData.newBuilder().setBigNum(item.getId()).setCode(ErrorCode.Success.code).build());
	}
	
	
	@ClientMethod(code = ServiceCode.ArenaManager_buyPower)
	public void buyPower(int num){
		long teamId = getTeamId();
		
		if(num<=0|| num>=500){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		int needMoney = num * _powerMoney;
		
		if(!teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球馆转盘, ""))){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_1.code).build());
			return;
		}
		
		TeamArena ta = getTeamArena(teamId);
		ta.updatePowerMax(num);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		
	}
	
	
	/**
	 * 偷取玩家球馆
	 * @param stealTeamId
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_stealArena)
	public void stealArena(long stealTeamId){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		if(ta.getStealCount()<0){//没有偷取次数，偷个鸡毛
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_5.code).build());
			return;
		}
		TeamArenaCross stealTeam = ta.getSteal().stream().filter(s->s.getTeamId()==stealTeamId).findFirst().orElse(null);
		if(stealTeam == null){//偷取的玩家不在可偷取列表中
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Arena_6.code).build());
			return;
		}
		//
		if(stealTeam.getTeamId()<=10){
			ArenaBean bean = GymConsole.getArenaBean(stealTeam.getLevel());
			int gold = stealTeam.getGold() * bean.getStealGold() / 1000;
			//更新被偷取者的货币数据
			//long defendTeamId,String stealName,int gold
			ta.updateGold(gold);
			ta.updateStealCount(-1);
			String tName = teamManager.getTeamNameById(teamId);
//		RPCMessageManager.sendMessage(CrossCode.LocalArenaManager_updateStealArenaTeamGold, tac.getLocal().getNodeName()
//					, stealTeam.getTeamId(),tName,gold);
			sendMessage(teamId,DefaultPB.DefaultData.newBuilder()
					.setCode(gold)
					.setBigNum(stealTeamId)
					.setMsg(tName + "," + 0 + "," + 0).build()
					,ServiceCode.Push_Arena_Steal_Attack_Tip);
			return;
		}
		
		//
		RPCLinkedTask.build().appendTask((tid,maps,args)->{
			RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_getTeamArena, stealTeam.getLocal().getNodeName(), tid,stealTeam.getTeamId());
		}).appendTask((tid,maps,args)->{
			TeamArenaCross tac = (TeamArenaCross)args[0];
			if(tac == null){
				log.debug("球馆偷取玩家信息不存在{}-node{}", stealTeamId,stealTeam.getLocal().getNodeName());
				return;
			}
			ArenaBean bean = GymConsole.getArenaBean(tac.getLevel());
			int gold = tac.getGold() * bean.getStealGold() / 1000;
			//更新被偷取者的货币数据
			//long defendTeamId,String stealName,int gold
			ta.updateGold(gold);
			ta.updateStealCount(-1);
			String tName = teamManager.getTeamNameById(teamId);
			RPCMessageManager.sendMessage(CrossCode.LocalArenaManager_updateStealArenaTeamGold, tac.getLocal().getNodeName()
						, tac.getTeamId(),tName,gold);
			sendMessage(teamId,DefaultPB.DefaultData.newBuilder()
					.setCode(gold)
					.setBigNum(stealTeamId)
					.setMsg(tName + "," + 0 + "," + 0).build()
					,ServiceCode.Push_Arena_Steal_Attack_Tip);
		}).start();
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	
	/**
	 * 显示复仇列表
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showVengeanceMain)
	public void showVengeanceMain(){
		long teamId = getTeamId();
		List<TeamNodeInfo> vengeanceList = redis.getList(RedisKey.getKey(teamId, RedisKey.Arena_Vengeance));
		 List<ArenaPB.ArenaVengeanceData> vengeanceDataList = vengeanceList.stream()
				 .map(info->getArenaVengeanceData(info))
				 .collect(Collectors.toList());
		
		sendMessage(ArenaPB.ArenaVengeanceMainData.newBuilder().addAllVengeanceList(vengeanceDataList).build());
	}

	
	
	/**
	 * 攻击球馆
	 * 
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_attackArena)
	public void attackArena(int cid){
		//
		long teamId = getTeamId();
		
//		if(GameSource.isNPC(defendTeamId)){//
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
		TeamArena ta = getTeamArena(teamId);
		if(ta.getAttackCount()<=0){//没有攻击次数
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		EArenaCType ctype =EArenaCType.getEArenaCType(cid);
		if(ctype == null){//建筑ID不对
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		TeamArenaCross defend = ta.getAttack();
		if(defend==null){//玩家还没选择攻击目标
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		
		TeamArenaConstruction tc = defend.getTeamArenaConstruction(ctype);
		if(tc.getCurGold()<=0){//建筑已经被打爆了，打个JJ
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		

		long defendTeamId = defend.getTeamId();
		ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
		Team team = teamManager.getTeam(teamId);
		boolean playerDie = tc.getCurGold()<tc.getMaxGold();
		boolean df = false;
		
		if(GameSource.serverName.equals(defend.getLocal().getNodeName())){//攻击本地球馆玩家
			//攻击方收益
			TeamArena other = getTeamArena(defendTeamId);
			df = other.getDefend()>0;
			//防守方损坏,扣除被攻击金币，推送事件
			defendConstruction(defendTeamId,other,ctype,team.getName(),team.getTeamId(),team.getLogo(),team.getLevel(),GameSource.serverName);
			//
		}else{//攻击跨服球馆玩家
//			RPCLinkedTask.build().appendTask((tid,maps,args)->{//
//				RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_updateArenaTeamGold, 
//						defend.getLocal().getNodeName(), tid, new TeamNodeInfo(teamId, GameSource.serverName,team.getName(), team.getLogo(), 
//								team.getLevel()),defendTeamId,ctype);
////				RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_getTeamArena, 
////						node, tid, defendTeamId);
//			}).appendTask((tid,maps,args)->{//发送跨服数据更新
//				TeamArenaCross tac = (TeamArenaCross)args[0];
//				attackGold(teamId,ta,tc, bean, tac.getDefend()>0);
//			});
			df = defend.getDefend()>0;
			RPCMessageManager.sendMessage(CrossCode.LocalArenaManager_updateAttackArenaTeamGold, 
					defend.getLocal().getNodeName(), new TeamNodeInfo(teamId, GameSource.serverName,team.getName(), team.getLogo(), 
							team.getLevel()),defendTeamId,ctype);
		}
		ta.updateAttackCount(-1);
		attackGold(teamId,ta,tc, bean,defend.getName(),df);
		if(playerDie && tc.getPlayerId()>0){
			ArenaPlayerTeam apt = getTeamArenaPlayer(teamId);
			int id = apt.getId();
			PlayerBean playerBean = PlayerConsole.getPlayerBean(tc.getPlayerId());
			ArenaPlayer ap = ArenaPlayer.createArenaPlayer(teamId,id ,tc.getPlayerId(),tc.getPlayerGrade().ordinal()
					,PlayerConsole.getPlayerBean(tc.getPlayerId()).getPosition()[0].getId(),playerBean.getTeamId());
			apt.addPlayer(ap);
			sendMessage(teamId,getArenaPlayerData(ap),ServiceCode.Push_Arena_Player_Tip);
			RPCMessageManager.sendMessage(CrossCode.LocalArenaManager_playerDie, defend.getLocal().getNodeName(), defend.getTeamId(),ctype);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		redPointLogic(defendTeamId);
	}

	
	
	/**
	 * 显示玩家球馆信息并指定攻击目标
	 * @param defendTeamId
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showAttackArena)
	public void showAttackArena(long defendTeamId){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		if(teamManager.existTeam(defendTeamId)){//本地数据，直接获得信息之后走推送
			TeamArena t = getTeamArena(defendTeamId);
			Team team = teamManager.getTeam(t.getTeamId());
			TeamArenaCross tac = new TeamArenaCross(t.getTeamId(), t.getGold(), t.getLevel(),t.getDefend(),
					teamManager.getTeamNameById(t.getTeamId()), 
					new TeamNodeInfo(t.getTeamId(),team.getName(),team.getLogo(),team.getLevel()), Lists.newArrayList(t.getcMap().values()));
			ta.setAttack(tac);
			//将命中的跨服攻击玩家数据缓存到本地
			ArenaPB.ArenaAttackMainData data = ArenaPB.ArenaAttackMainData.newBuilder().addTeams(getArenaAttackTeamData(tac)).build();
			//将玩家的球馆数据推送到前台，进攻界面推送
			sendMessage(teamId,data,ServiceCode.Push_Arena_Attack_Team);
		}else{
			int defendSid = GameSource.getSid(defendTeamId);
			String defendNode = nodeManager.getNode(defendSid);
			RPCLinkedTask.build().appendTask((tid,maps,args)->{//
				RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_getTeamArena, 
						defendNode, tid, defendTeamId);
			}).appendTask((tid,maps,args)->{//
				TeamArenaCross tac = (TeamArenaCross)args[0];
				if(tac.getTeamId()!=defendTeamId){
					//数据异常，防守方数据不在指定节点上
					return;
				}
				//重置攻击目标
				ta.setAttack(tac);
				ArenaPB.ArenaAttackMainData data = ArenaPB.ArenaAttackMainData.newBuilder().addTeams(getArenaAttackTeamData(tac)).build();
				//将玩家的球馆数据推送到前台，进攻界面推送
				sendMessage(teamId,data,ServiceCode.Push_Arena_Attack_Team);
			}).start();
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 * 显示偷取的球馆信息
	 */
	@ClientMethod(code = ServiceCode.ArenaManager_showStealArena)
	public void showStealArena(){
		long teamId = getTeamId();
		TeamArena ta = getTeamArena(teamId);
		//从各个节点拉取最新的玩家球馆信息
		RPCLinkedTask link = RPCLinkedTask.build();
		ta.getSteal().forEach(st->{
			IRPCTask task = (tid,maps,args)->{
				if(args.length>0){
					TeamArenaCross tac = (TeamArenaCross)args[0];
					maps.put(""+tac.getTeamId(), tac);
				}
				RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalArenaManager_getTeamArena, 
						st.getLocal().getNodeName(), tid, st.getTeamId());
			};
			link.appendTask(task);
		});
		//
		ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
		//汇总节点信息，重置当前偷取列表的玩家数据
		link.appendTask((tid,maps,args)->{
			List<TeamArenaCross> newStealList = Lists.newArrayList();
			List<ArenaPB.ArenaAttackTeamData> dataList = Lists.newArrayList();
			ta.getSteal().forEach(st->{
				TeamArenaCross tac = maps.get(st.getTeamId()+"");
				if(tac!=null){
					newStealList.add(tac);
					dataList.add(getArenaAttackTeamData(tac,bean.getStealGold()));					
				}
			});
			ta.setSteal(newStealList);
			//将封装好的数据推送到客户端
			ArenaPB.ArenaAttackMainData data = ArenaPB.ArenaAttackMainData.newBuilder().addAllTeams(dataList).build();
			sendMessage(teamId,data,ServiceCode.Push_Arena_Steal_Team);
		}).start();
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}

	
	@Override
	public RedPointParam redPointLogic(long teamId) {
		TeamArena ta = getTeamArena(teamId);
		boolean show = ta.getcMap().values().stream().filter(tc->tc.getCurGold()!=tc.getMaxGold()).findFirst().isPresent();
		//
		ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
		
		if(!show){
		show = ta.getcMap().values().stream().map(tc->{
				ArenaConstructionBean acBean = bean.getConstruction(tc.getcId());
				int nextGold = acBean.getNextGold(tc.getCurGold());
				return nextGold - tc.getCurGold();//取得当前升级或者修复需要的金币数量
				
			}).filter(gold->gold>0).filter(gold->ta.getGold()>=gold).findFirst().isPresent();
		}
		RedPointParam rpp = new RedPointParam(teamId, ERedPoint.球馆修补.getId(), show?1:0);
		EventBusManager.post(EEventType.奖励提示, rpp);
		return rpp;
	}

	public int addPlayerLineup(long teamId,int playerId){
		ArenaPlayerTeam apt = getTeamArenaPlayer(teamId);
		int id = apt.getId();
		apt.addPlayer(ArenaPlayer.createArenaPlayer(teamId,id ,playerId));
		return id;
	}
	
	private void defendConstruction(long defendTeamId, TeamArena other, EArenaCType ctype,
                                    String name, long tId, String logo, int level, String nodeName){
		boolean damage = false;
		int gold = 0;
		if(other.getDefend()>0){//有护盾，没有金币损耗，直接扣除护盾
			other.updateDefend(-1);
		}else{
			ArenaConstructionBean acb = GymConsole.getArenaBean(other.getLevel()).getConstruction(ctype);
			TeamArenaConstruction tc = other.getTeamArenaConstruction(acb.getCid());
			if(tc.getCurGold()!=tc.getMaxGold()){//已经损坏，直接摧毁
				gold = tc.getCurGold();
			}else{
//				gold = Math.round(acb.getCurLevelGold(tc.getMaxGold())/2*ArenaConsole.getPlayerDefendBuffer(tc.getPlayerGrade())/1000f);
				gold = Math.round(tc.getMaxGold()/2*GymConsole.getPlayerDefendBuffer(tc.getPlayerGrade())/1000f);
			}
			//
			tc.updateGold(-gold);
			damage = true;
		}
		sendMessage(defendTeamId,DefaultPB.DefaultData.newBuilder()
				.setCode(-gold)
				.setBigNum(defendTeamId)
				.setMsg(name + "," + ctype.getType() + "," + (damage?1:0)).build()
				,ServiceCode.Push_Arena_Defend_Tip);
		//增加复仇记录
		redis.addListValueL(RedisKey.getKey(defendTeamId, RedisKey.Arena_Vengeance)
				, new TeamNodeInfo(tId, nodeName,name, logo, level));
	}
	
	private void defendConstruction(long defendTeamId, TeamArena other, EArenaCType ctype, TeamNodeInfo attackTeam){
		defendConstruction(defendTeamId, other, ctype, attackTeam.getTeamName(), attackTeam.getTeamId()
				, attackTeam.getLogo(), attackTeam.getLevel(),attackTeam.getNodeName());
	}
	
	private void attackGold(long teamId, TeamArena ta, TeamArenaConstruction tc
			, ArenaBean bean, String defendName, boolean damage){
		int gold = 0;
		if(damage){//有护盾，获得一个固定值
			gold = bean.getAttackDefendGold();
		}else{
			gold = Math.round(bean.getAttackGold()* GymConsole.getPlayerAttackBuffer(tc.getPlayerGrade()) /1000f);
		}
		ta.updateGold(gold);
		//防守者在自身复仇列表中，将该玩家清除
		List<TeamNodeInfo> vengeanceList = redis.getList(RedisKey.getKey(teamId, RedisKey.Arena_Vengeance));
		TeamNodeInfo vengeance = vengeanceList.stream().filter(ven->ven.getTeamId() == tc.getTeamId()).findFirst().orElse(null);
		if(vengeance!=null){//防守者在自身复仇列表中
			redis.removeListValue(RedisKey.getKey(teamId, RedisKey.Arena_Vengeance), vengeance);
		}
		log.debug("攻击防守方建筑信息[{}]--[{}]--[{}]", tc.getMaxGold(),tc.getCurGold(),(damage?1:0));
		
		sendMessage(teamId,DefaultPB.DefaultData.newBuilder()
				.setCode(gold)
				.setBigNum(tc.getTeamId())
				.setMsg(defendName + "," + tc.getcId().getType() + "," + (damage?1:0)+","
									+tc.getPlayerId()+","+tc.getCurGold()+",").build()
				,ServiceCode.Push_Arena_Attack_Tip);
		
		redPointLogic(teamId);
	}
	
	//-----------------------------------------------------------------------------------------------------------
	/**
	 * @param attackTeam
	 * @param defendTeamId
	 */
	@RPCMethod(code = CrossCode.LocalArenaManager_updateAttackArenaTeamGold,pool=EServerNode.Logic,type=ERPCType.NONE)
	public void _updateAttackArenaTeamGold(TeamNodeInfo attackTeam,long defendTeamId,EArenaCType ctype){
		if(GameSource.isNPC(defendTeamId)) return;//不对NPC球馆做处理
		TeamArena ta = getTeamArena(defendTeamId);
		defendConstruction(defendTeamId, ta, ctype, attackTeam);
	}
	
	/**
	 * @param stealName
	 * @param gold
	 */
	@RPCMethod(code = CrossCode.LocalArenaManager_updateStealArenaTeamGold,pool=EServerNode.Logic,type=ERPCType.NONE)
	public void _updateStealArenaTeamGold(long defendTeamId,String stealName,int gold){
		if(GameSource.isNPC(defendTeamId)) return;//不对NPC球馆做处理
		TeamArena ta = getTeamArena(defendTeamId);
		ta.updateGold(-gold);
		sendMessage(defendTeamId,DefaultPB.DefaultData.newBuilder()
				.setBigNum(-gold)
				.setMsg(stealName + "," + 0 + "," + 0).build()
				,ServiceCode.Push_Arena_Steal_Tip);
	}
	
	/**
	 *  随机取玩家球馆
	 */
	@RPCMethod(code = CrossCode.LocalArenaManager_ranTeamArena,pool=EServerNode.Logic,type=ERPCType.NONE)
	public void _ranTeamArena(int num,EArenaRollType rollType){
		List<Long> tmpList = teamManager.getRanTeam(num+20);
		ArrayList<TeamArenaCross> ta =  Lists.newArrayList();
		tmpList.stream().map(tmp->getTeamArena(tmp)).forEach(t->{
			Team team = teamManager.getTeam(t.getTeamId());
			if(team == null) log.error("异常球队ID，检查---{}", t.getTeamId());
			ta.add(
				new TeamArenaCross(t.getTeamId(), t.getGold(), t.getLevel(),t.getDefend(),
						teamManager.getTeamNameById(t.getTeamId()), 
						new TeamNodeInfo(t.getTeamId(),team.getName(),team.getLogo(),team.getLevel()), Lists.newArrayList(t.getcMap().values())));
		});
		ArrayList<TeamArenaCross> result = null;
		if(rollType == EArenaRollType.攻击){
			result = Lists.newArrayList(ta.stream().filter(t->t.checkAttackConstruction()).limit(num).collect(Collectors.toList()));
		}
		if(rollType == EArenaRollType.偷取){
			result = Lists.newArrayList(ta.stream().filter(t->t.getGold()>0).limit(num).collect(Collectors.toList()));
		}
		if(result.size()!=num){
			result = Lists.newArrayList(ta.stream().limit(num).collect(Collectors.toList()));
		}
		//
		RPCMessageManager.responseMessage(result);
	}
	
	@RPCMethod(code = CrossCode.LocalArenaManager_getTeamArena,pool=EServerNode.Logic,type=ERPCType.NONE)
	public void _getTeamArena(long teamId){
		if(!teamManager.existTeam(teamId)){//玩家不是这个服务器的
			RPCMessageManager.responseMessage(new TeamArenaCross(-1));
			return;
		}
		TeamArena t = getTeamArena(teamId);
		Team team = teamManager.getTeam(t.getTeamId());
		TeamArenaCross tac = new TeamArenaCross(t.getTeamId(), t.getGold(), t.getLevel(),t.getDefend(),
				teamManager.getTeamNameById(t.getTeamId()), 
				new TeamNodeInfo(t.getTeamId(),team.getName(),team.getLogo(),team.getLevel()), Lists.newArrayList(t.getcMap().values()));
		RPCMessageManager.responseMessage(tac);
	}
	
	@RPCMethod(code=CrossCode.LocalArenaManager_playerDie,pool=EServerNode.Logic,type=ERPCType.NONE)
	public void _playerDie(long teamId,EArenaCType ctype){
		TeamArena ta = getTeamArena(teamId);
		TeamArenaConstruction tac = ta.getTeamArenaConstruction(ctype);
		tac.removePlayer();
		sendMessage(teamId,DefaultPB.DefaultData.newBuilder()
				.setCode(ErrorCode.Success.code)
				.setMsg(""+ctype.getType())
				.build(),ServiceCode.Push_Arena_Player_Die_Tip);
	}
	
	private ArenaPB.ArenaVengeanceData getArenaVengeanceData(TeamNodeInfo info){
		return ArenaPB.ArenaVengeanceData.newBuilder().setLogo(info.getLogo()).setName(info.getTeamName())
				.setTeamId(info.getTeamId())
				.setNodeName(info.getNodeName())
				.build();
	}
	
	private ArenaPB.ArenaAttackTeamData getArenaAttackTeamData(TeamArenaCross ta){
		List<ArenaPB.ArenaConstructionData> cList = Lists.newArrayList();
		ta.getcList().forEach(c->cList.add(getArenaConstructionData(c)));
		ArenaPB.ArenaConstructionMainData data = ArenaPB.ArenaConstructionMainData.newBuilder()
				.addAllCList(cList).build();
		return ArenaPB.ArenaAttackTeamData.newBuilder()
				.setGold(ta.getGold())
				.setLevel(ta.getLevel())
				.setName(ta.getName())
				.setTeamId(ta.getTeamId())
				.setCData(data) 
				.build();
		
	} 
	
	private ArenaPB.ArenaAttackTeamData getArenaAttackTeamData(TeamArenaCross ta,int steal){
		List<ArenaPB.ArenaConstructionData> cList = Lists.newArrayList();
		ta.getcList().forEach(c->cList.add(getArenaConstructionData(c)));
		ArenaPB.ArenaConstructionMainData data = ArenaPB.ArenaConstructionMainData.newBuilder()
				.addAllCList(cList).build();
		return ArenaPB.ArenaAttackTeamData.newBuilder()
				.setGold(ta.getGold())
				.setLevel(ta.getLevel())
				.setName(ta.getName())
				.setCData(data)
				.setTeamId(ta.getTeamId())
				.setNodeName(ta.getLocal().getNodeName())
				.build();
		
	}
	
	private ArenaPB.ArenaConstructionData getArenaConstructionData(TeamArenaConstruction c){
		
		return ArenaPB.ArenaConstructionData.newBuilder()
				.setCid(c.getcId().getType())
				.setCurGold(c.getCurGold())
				.setMaxGold(c.getMaxGold())
				.setPlayerGrade(c.getPlayerGrade().ordinal())
				.setPlayerId(c.getPlayerId())
				.build();
	}
	
	
	private ArenaPB.ArenaTeamData getArenaTeamData(TeamArena team){
		 return ArenaPB.ArenaTeamData.newBuilder()
				 		.setDefend(team.getDefend())
				 		.setGold(team.getGold())
				 		.setPower(team.getPower())
				 		.setLevel(team.getLevel())
				 		.setPowerSecond(team.getPowerSecond())
				 		.build();
	}
	
	
	public TeamArena getTeamArena(long teamId){
		TeamArena ta = teamAreanaMap.get(teamId);
		if(ta == null){
			TeamArenaPO tpo = arenaAO.getTeamArena(teamId);
			if(tpo == null){
				ta = TeamArena.createTeamArena(teamId);
				teamAreanaMap.put(teamId, ta);
				return ta;
			}
			List<TeamArenaConstructionPO> tcList = arenaAO.getTeamArenaConstruction(teamId);
			List<TeamArenaConstruction> list =  tcList.stream().map(t->new TeamArenaConstruction(t)).collect(Collectors.toList());
			ta = new TeamArena(tpo, list);
			teamAreanaMap.put(teamId, ta);
		}
		return ta;
	}
	
	public ArenaPlayerTeam getTeamArenaPlayer(long teamId){
		ArenaPlayerTeam result = teamAreanaPlayerMap.get(teamId);
		if(result == null){
			List<ArenaPlayerPO> list  = arenaAO.getArenaPlayerPO(teamId);
			List<ArenaPlayer>  playerList = list.stream().map(ap->new ArenaPlayer(ap)).collect(Collectors.toList());
			result = new ArenaPlayerTeam(playerList);
			teamAreanaPlayerMap.put(teamId, result);
			
		}
		return result;
	}
	
	
	
	@Override
	public void initConfig() {
		_powerMoney = ConfigConsole.getIntVal(EConfigKey.Team_Arena_Power_Price);
	}

	@Override
	public void instanceAfter() {
		teamAreanaMap = Maps.newConcurrentMap();
		teamAreanaPlayerMap = Maps.newConcurrentMap();
		// 监听事件
		EventBusManager.register(EEventType.登录, this);
		List<ArenaPB.ArenaAttackTeamData> dataList = Lists.newArrayList();
		
		_helpArenaList = Lists.newArrayList();
		TeamArenaCross tac1 = new TeamArenaCross(5,100,3,0,"飘发",new TeamNodeInfo(0,GameSource.serverName,"飘发","1",20),getHelpConstruction(5,3));
		TeamArenaCross tac2 = new TeamArenaCross(6,90,2,0,"忠诚",new TeamNodeInfo(0,GameSource.serverName,"忠诚","1",20),getHelpConstruction(5,2));
		TeamArenaCross tac3 = new TeamArenaCross(7,75,1,0,"胡子",new TeamNodeInfo(0,GameSource.serverName,"胡子","1",20),getHelpConstruction(5,1));
		_helpArenaList.add(tac1);
		_helpArenaList.add(tac2);
		_helpArenaList.add(tac3);
		dataList.add(getArenaAttackTeamData(tac1,0));
		dataList.add(getArenaAttackTeamData(tac2,0));
		dataList.add(getArenaAttackTeamData(tac3,0));
		_helpArenaData = ArenaPB.ArenaAttackMainData.newBuilder().addAllTeams(dataList).build();
	}
	
	private List<TeamArenaConstruction> getHelpConstruction(long teamId,int level){
		List<TeamArenaConstruction> lists = Lists.newArrayList();
		ArenaBean ab = GymConsole.getArenaBean(level);
		TeamArenaConstructionPO po1 = new TeamArenaConstructionPO();
		po1.setcId(EArenaCType.主体.getPosition());
		po1.setCurGold(ab.getConstruction(EArenaCType.主体).getGold5());
		po1.setMaxGold(po1.getCurGold());
		po1.setPlayerGrade(0);
		po1.setPlayerId(0);
		po1.setTeamId(teamId);
		po1.setUpdateTime(DateTime.now());
		lists.add(new TeamArenaConstruction(po1));
		
		TeamArenaConstructionPO po2 = new TeamArenaConstructionPO();
		po2.setcId(EArenaCType.交通工具.getPosition());
		po2.setCurGold(ab.getConstruction(EArenaCType.交通工具).getGold5());
		po2.setMaxGold(po2.getCurGold());
		po2.setPlayerGrade(0);
		po2.setPlayerId(0);
		po2.setTeamId(teamId);
		po2.setUpdateTime(DateTime.now());
		lists.add(new TeamArenaConstruction(po2));
		
		TeamArenaConstructionPO po3 = new TeamArenaConstructionPO();
		po3.setcId(EArenaCType.吉祥物.getPosition());
		po3.setCurGold(ab.getConstruction(EArenaCType.吉祥物).getGold5());
		po3.setMaxGold(po3.getCurGold());
		po3.setPlayerGrade(0);
		po3.setPlayerId(0);
		po3.setTeamId(teamId);
		po3.setUpdateTime(DateTime.now());
		lists.add(new TeamArenaConstruction(po3));
		
		TeamArenaConstructionPO po4 = new TeamArenaConstructionPO();
		po4.setcId(EArenaCType.酒店.getPosition());
		po4.setCurGold(ab.getConstruction(EArenaCType.酒店).getGold5());
		po4.setMaxGold(po4.getCurGold());
		po4.setPlayerGrade(0);
		po4.setPlayerId(0);
		po4.setTeamId(teamId);
		po4.setUpdateTime(DateTime.now());
		lists.add(new TeamArenaConstruction(po4));
		
		TeamArenaConstructionPO po5 = new TeamArenaConstructionPO();
		po5.setcId(EArenaCType.雕像.getPosition());
		po5.setCurGold(ab.getConstruction(EArenaCType.雕像).getGold5());
		po5.setMaxGold(po5.getCurGold());
		po5.setPlayerGrade(0);
		po5.setPlayerId(0);
		po5.setTeamId(teamId);
		po5.setUpdateTime(DateTime.now());
		lists.add(new TeamArenaConstruction(po5));
		return lists;
	}

	/**
	 * 登录回调
	 * @param param
	 */
	@Subscribe
	public void login(LoginParam param) {
		if(!param.isTodayFirst) return;
		int power = buffManager.getBuffSet(param.teamId, EBuffType.每日登陆球馆获得点能量).getValueSum();
		if(power > 0) {
			TeamArena ta = getTeamArena(param.teamId);
			ta.updatePower(power);
		}
	}
	
	@Override
	public void offline(long teamId) {
		teamAreanaMap.remove(teamId);
		teamAreanaPlayerMap.remove(teamId);
	}

    @Override
    public void dataGC(long teamId) {
        teamAreanaMap.remove(teamId);
        teamAreanaPlayerMap.remove(teamId);
    }
}
