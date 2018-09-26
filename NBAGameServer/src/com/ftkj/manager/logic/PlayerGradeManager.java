package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.PlayerStarBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.ao.logic.IPlayerGradeAO;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.PlayerLevelParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerGrade;
import com.ftkj.manager.player.TeamPlayerGrade;
import com.ftkj.manager.player.api.PlayerGradeAPI;
import com.ftkj.manager.prop.bean.PropSimpleBean;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang 2017年9月28日 球员升级管理
 */
public class PlayerGradeManager extends BaseManager implements OfflineOperation{
	private static final Logger log = LoggerFactory.getLogger(PlayerGradeManager.class);
	@IOC
	private IPlayerGradeAO playerGradeAO;

	@IOC
	private PropManager propManager;
	@IOC
	private TeamManager teamManager;
	
	@IOC
	private TaskManager taskManager;
	@IOC
	private ChatManager chatManager;

	private Map<Long, TeamPlayerGrade> teamPlayerGradeMap;

	private int _maxGrade;
	private int _maxStarGrade;
	private int _commonXProp;
	private Set<Integer> _XPlayerCommon;
	private final int starRoundGrade =5;
	
	
	
	/**
	 * @param playerId
	 * 
	 */
	@ClientMethod(code = ServiceCode.PlayerGradeManager_levelupStar)
	public void levelupStar(int playerId){
		long teamId = getTeamId();
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
		PlayerGrade pg = tpg.getPlayerGrade(playerId);
		if (pg == null) {
			pg = new PlayerGrade(teamId, playerId);
			tpg.getPlayerGradeMap().put(playerId, pg);
		}
		if(pg.getStarGrade() >= _maxStarGrade){
			log.debug("已经满级了[{}]-{}-{}", teamId, playerId,pg.getStar());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
			return;
		}
		PlayerBean playerBean = PlayerConsole.getPlayerBean(playerId);
		int beanGrade = pg.getStarGrade();
		PlayerStarBean bean = PlayerConsole.getPlayerStarGradeBean(beanGrade);
		
		Team team =teamManager.getTeam(teamId);
		if(bean.getNeedLevel()>team.getLevel()){
			log.debug("球队等级不足[{}]-{}-{}-{}", teamId, playerId,bean.getNeedLevel(),team.getLevel());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
//		if(playerBean.getGrade()==EPlayerGrade.X){//X球员
//			beanGrade+=starRoundGrade;//X球员取配置5级开始，因为5级一个轮回
//			bean = PlayerConsole.getPlayerStarGradeBean(beanGrade);
//		}
		
		if(playerBean.getGrade()==EPlayerGrade.X && _XPlayerCommon.contains(playerId)){
			if(propManager.delProp(teamId, _commonXProp, bean.getXneedExp(), true, true)==null){
				log.debug("有个傻逼想作弊[{}]-{}-{}-{}", teamId, playerId,pg.getExp(),bean.getNeedExp());
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
				return;
			}
		}else{
			if(pg.getStar() < bean.getNeedExp()){//数量不够
				log.debug("有个傻逼想作弊[{}]-{}-{}-{}", teamId, playerId,pg.getStar(),bean.getNeedExp());
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
				return;
			}
			pg.updateStar(-bean.getNeedExp());
		}
//		if(pg.getGrade()<bean.getNeedLevel()){
//			log.debug("球员等级不足[{}]-{}-{}-{}", teamId, playerId,pg.getExp(),bean.getNeedExp());
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
//			return;
//		}
		pg.setStarGrade(pg.getStarGrade()+1);
		pg.save();
		log.debug("当前等级{}-{}-{}", teamId,playerId,pg.getStarGrade());
		EventBusManager.post(EEventType.球员升星, new PlayerLevelParam(teamId, playerId, pg.getStarGrade()));
		taskManager.updateTask(teamId, ETaskCondition.球员升星, 1, ""+pg.getStarGrade());
//		if(pg.getStarGrade()>=3 && pg.getStarGrade()<5)
//			chatManager.pushGameTip(EGameTip.球员升星34, 0, teamManager.getTeamName(teamId),"pid_"+playerBean.getPlayerId(),""+pg.getStarGrade());
//		if(pg.getStarGrade()>=5)
//			chatManager.pushGameTip(EGameTip.球员升星5, 0, teamManager.getTeamName(teamId),"pid_"+playerBean.getPlayerId(),""+pg.getStarGrade());
		//
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	public void makeStar(long teamId,int playerId){
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
		PlayerGrade pg = tpg.getPlayerGrade(playerId);
		if (pg == null) {
			pg = new PlayerGrade(teamId, playerId);
			tpg.getPlayerGradeMap().put(playerId, pg);
		}
		pg.updateStar(1);
		sendMessage(teamId,DefaultPB.DefaultData.newBuilder().setCode(playerId).build(),ServiceCode.Push_Player_Star);
	}
	
	public void _GMAddStar(long teamId){
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
		tpg.getPlayerGradeMap().values().forEach(player->player.updateStar(999));
	}
	
	
	@ClientMethod(code = ServiceCode.PlayerGradeManager_levelUP)
	public void levelUP(int playerId, int pid,int num) {
		long teamId = getTeamId();
		if(num>=999 || num<=0) {
			log.debug("有个傻逼想作弊[{}]-{}-{}-{}", teamId, playerId,pid,
					num);
			sendMessage(DefaultPB.DefaultData.newBuilder()
					.setCode(ErrorCode.Error.code).build());
			return;
		}
		//
		
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
		PlayerGrade pg = tpg.getPlayerGrade(playerId);
		if (pg == null) {
			pg = new PlayerGrade(teamId, playerId);
			tpg.getPlayerGradeMap().put(playerId, pg);
		}
		if (pg.getGrade() == _maxGrade) {// 超过最高等级限制
			log.debug("{}-{}超过最高等级上限[{}]", teamId, playerId,
					pg.getGrade());
			sendMessage(DefaultPB.DefaultData.newBuilder()
					.setCode(ErrorCode.Common_4.code).build());
			return;
		}
		
		Team team = teamManager.getTeam(teamId);
		if (pg.getGrade() >= team.getLevel()) {// 超过最高等级限制
			log.debug("{}-{}超过球队最高等级上限[{}]", teamId, playerId,
					pg.getGrade());
			sendMessage(DefaultPB.DefaultData.newBuilder()
					.setCode(ErrorCode.Common_4.code).build());
			return;
		}
		
		// 避免异常，先转换指定道具
		PropSimpleBean propBean = PropConsole.getProp(pid);

		if (propManager.delProp(teamId, pid, num, true, true) == null) {// 道具不足
			log.debug("{}-{}道具不足，无法升级球员[{}]", teamId, pid, playerId);
			sendMessage(DefaultPB.DefaultData.newBuilder()
					.setCode(ErrorCode.Common_4.code).build());
			return;
		}
		//
		PlayerGradeAPI.levelUpPlayerGrade(pg, propBean.getPlayerExp() * num);
		pg.save();
		EventBusManager.post(EEventType.球员升级, new PlayerLevelParam(teamId, playerId, pg.getGrade()));
		taskManager.updateTask(teamId, ETaskCondition.球员升级, 1, pg.getGrade()+"");
		sendMessage(DefaultPB.DefaultData.newBuilder()
				.setCode(ErrorCode.Success.code).build());
	}

//	public PlayerAbility getPlayerGradeAbility(PlayerGrade playerGrade){
//		PlayerAbility playerAbility = new PlayerAbility(EAbility.球员等级,
//				playerGrade.getPlayerId());
//		PlayerGradeBean bean = PlayerConsole.getPlayerGradeBean(playerGrade
//				.getGrade());
//		PlayerBean player = PlayerConsole.getPlayerBean(playerGrade.getPlayerId());
//
//		int [] power = bean.getPower(player.getPosition()[0]);
//		playerAbility.addInfo(EActionType.T_进攻, power[0]);
//		playerAbility.addInfo(EActionType.T_防守, power[1]);
//		return playerAbility;
//	}

	public PlayerPB.PlayerGradeMainData getPlayerGradeMainData(long teamId) {
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);

		List<PlayerPB.PlayerGradeData> playerGradeDataList = tpg
				.getPlayerGradeMap().values().stream()
//				.filter(playerGrade->playerGrade.getExp()>0)
				.map(playerGrade -> getPlayerGradeData(playerGrade))
				.collect(Collectors.toList());
		return PlayerPB.PlayerGradeMainData.newBuilder()
				.addAllPlayerGradeList(playerGradeDataList).build();
	}
	
//	public PlayerPB.PlayerGradeMainData getPlayerGradeStarMainData(long teamId,List<Integer> list){
//		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
//		List<PlayerPB.PlayerGradeData> playerGradeDataList = list.stream().map(pid->tpg.getPlayerGrade(pid))
//		.filter(player->player!=null)
//		.map(playerGrade -> getPlayerGradeData(playerGrade))
//		.collect(Collectors.toList());
//		
//		return PlayerPB.PlayerGradeMainData.newBuilder()
//				.addAllPlayerGradeList(playerGradeDataList).build();
//	}

	private PlayerPB.PlayerGradeData getPlayerGradeData(PlayerGrade playerGrade) {

		return PlayerPB.PlayerGradeData.newBuilder()
				.setExp(playerGrade.getExp()).setGrade(playerGrade.getGrade())
				.setPlayerId(playerGrade.getPlayerId())
				.setStar(playerGrade.getStar())
				.setStarGrade(playerGrade.getStarGrade())
				.build();

	}

	@Override
	public void offline(long teamId) {
		teamPlayerGradeMap.remove(teamId);
	}

    @Override
    public void dataGC(long teamId) {
        teamPlayerGradeMap.remove(teamId);
    }

    @Override
	public void initConfig() {
		_maxGrade = ConfigConsole.getIntVal(EConfigKey.Player_Level_Max_Grade);
		_commonXProp = ConfigConsole.getIntVal(EConfigKey.Player_Star_X_Prop);
		String tmpX  = ConfigConsole.getVal(EConfigKey.Player_Star_X_Common);
		_XPlayerCommon = Sets.newHashSet();
		Arrays.stream(StringUtil.toIntArray(tmpX, StringUtil.DEFAULT_ST)).forEach(id->_XPlayerCommon.add(id));
		_maxStarGrade = 5;
	}

	public TeamPlayerGrade getTeamPlayerGrade(long teamId) {
		TeamPlayerGrade tpg = teamPlayerGradeMap.get(teamId);
		if (tpg == null) {
			List<PlayerGrade> list = playerGradeAO.getPlayerGradeList(teamId);
			tpg = new TeamPlayerGrade(list);
			teamPlayerGradeMap.put(teamId, tpg);
		}
		return tpg;
	}

	@Override
	public void instanceAfter() {
		teamPlayerGradeMap = Maps.newConcurrentMap();
	}

	
	/**
	 * 转换等级
	 * @param teamId
	 * @param player1
	 * @param player2
	 */
	public void transGrade(long teamId, int player1, int player2) {
		TeamPlayerGrade tpg = getTeamPlayerGrade(teamId);
		PlayerGrade p1 = tpg.getPlayerGrade(player1);
		PlayerGrade p2 = tpg.getPlayerGrade(player2);
		if(p1 == null) {
			p1 = new PlayerGrade(teamId, player1);
			tpg.getPlayerGradeMap().put(player1, p1);
		}
		if(p2 == null) {
			p2 = new PlayerGrade(teamId, player2);
			tpg.getPlayerGradeMap().put(player2, p2);
		}
		int p1Grade = p1.getGrade();
		int p1Exp = p1.getExp();
		int p2Grade = p2.getGrade();
		int p2Exp = p2.getExp();
		p1.setGrade(p2Grade);
		p1.setExp(p2Exp);
		p2.setGrade(p1Grade);
		p2.setExp(p1Exp);
		p1.save();
		p2.save();
	}
	
}
