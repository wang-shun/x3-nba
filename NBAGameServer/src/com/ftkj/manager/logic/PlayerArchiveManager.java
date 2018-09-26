package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ArchiveConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.SkillConsole;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.equi.TeamEqui;
import com.ftkj.manager.equi.bean.PlayerEqui;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerGrade;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.player.TeamPlayerGrade;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.TeamSkill;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerArchivePB;
import com.ftkj.proto.TeamEquiPB.EquiData;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;

/**
 * 球员图鉴/转换
 * @author Jay
 * @time:2017年10月26日 下午3:07:16
 */
public class PlayerArchiveManager extends BaseManager {

	
	@IOC
	private EquiManager teamEquiManager;
	@IOC
	private SkillManager skillManager;	
	@IOC
	private PlayerGradeManager playerGradeManager;
	@IOC
	private PlayerManager playerManager;
	@IOC
	private PropManager propManager;
	
	@Override
	public void instanceAfter() {
	}

	
	
	/**
	 * 图鉴主界面；
	 * 包括：
	 * 1，主力
	 * 2，仓库
	 * 3，装备/球衣
	 * 4，技能
	 * 5，球员星级
	 * 6，球员等级
	 * 7，球员训练
	 * 
	 */
	@ClientMethod(code = ServiceCode.PlayerArchiveManager_showPlayerList)
	public void showPlayerList() {
		//long startTime = System.currentTimeMillis();
		long teamId = getTeamId();
		//
		TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
		TeamEqui teamEqui = teamEquiManager.getTeamEqui(teamId);
		TeamSkill teamSkill = skillManager.getTeamSkill(teamId);
		TeamPlayerGrade teamPlayerGrade = playerGradeManager.getTeamPlayerGrade(teamId);	
		//
		List<Integer> mainPlayer = teamPlayer.getPlayers().stream().mapToInt(p-> p.getPlayerRid()).boxed().collect(Collectors.toList());
		List<Integer> equiPlayer = teamEqui.getTeamAllEqui().stream().mapToInt(e-> e.getPlayerId()).distinct().boxed().collect(Collectors.toList());
		List<Integer> skillPlayer = teamSkill.getPlayerSkillMap().keySet().stream().collect(Collectors.toList());
		List<Integer> gradePlayer = teamPlayerGrade.getPlayerGradeMap().keySet().stream().collect(Collectors.toList());
		List<Integer> trainPlayer = teamPlayerGrade.getPlayerGradeMap().keySet().stream().collect(Collectors.toList());
		//
		List<Integer> playerList = Lists.newArrayList();
		playerList.addAll(mainPlayer);
		playerList.addAll(equiPlayer);
		playerList.addAll(skillPlayer);
		playerList.addAll(gradePlayer);
		playerList.addAll(trainPlayer);
		playerList = playerList.stream().distinct().collect(Collectors.toList());
		//
		List<PlayerArchivePB.PlayerArchiveData> dataList = Lists.newArrayList();
		for(int playerId : playerList) {
			dataList.add(getPlayerArchiveData(teamPlayer, teamEqui, teamSkill, teamPlayerGrade, playerId));
		}
		//log.error("球员图鉴耗时:{}", System.currentTimeMillis() - startTime);
		// 回包列表
		sendMessage(PlayerArchivePB.TeamPlayerArchiveMainData.newBuilder().addAllPlayerList(dataList).build());
	}
	
	/**
	 * 查看玩家阵容明细
	 * @param teamId
	 */
	public PlayerArchivePB.TeamPlayerArchiveMainData showPlayerInfoDetail(long teamId) {
		//
		TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
		TeamEqui teamEqui = teamEquiManager.getTeamEqui(teamId);
		TeamSkill teamSkill = skillManager.getTeamSkill(teamId);
		TeamPlayerGrade teamPlayerGrade = playerGradeManager.getTeamPlayerGrade(teamId);	
		//
		List<Integer> playerList = teamPlayer.getPlayers().stream().mapToInt(p-> p.getPlayerRid()).boxed().collect(Collectors.toList());
		//
		List<PlayerArchivePB.PlayerArchiveData> dataList = Lists.newArrayList();
		for(int playerId : playerList) {
			dataList.add(getPlayerArchiveData(teamPlayer, teamEqui, teamSkill, teamPlayerGrade, playerId));
		}
		// 回包列表
		return PlayerArchivePB.TeamPlayerArchiveMainData.newBuilder().addAllPlayerList(dataList).build();
	}
	
	/**
	 * 数据封装
	 * @param teamId
	 * @param playerId
	 * @return
	 */
	private PlayerArchivePB.PlayerArchiveData getPlayerArchiveData(TeamPlayer teamPlayer, TeamEqui teamEqui, TeamSkill teamSkill, TeamPlayerGrade teamPlayerGrade, int playerId) {
		// 装备  技能  升级  升星  训练  
		List<EquiData> equiList = EquiManager.getEquiListData(teamEqui.getPlayerAllEqui(playerId));
		PlayerSkill playerSkill = teamSkill.getPlayerSkill(playerId);		
		PlayerGrade p = teamPlayerGrade.getPlayerGrade(playerId);
		int playerLv = 0;
		int starLv = 0;
		// 总步数
		int train = 0;
		if(p != null) {
			playerLv = p.getGrade();
			starLv = p.getStarGrade();
		}
		
		int atkId = 0, defId = 0, atkLv =0, defLv = 0;
		Player player = teamPlayer.getPlayer(playerId);
		if(playerSkill != null) {
			atkId = playerSkill.getAttack();
			defId = playerSkill.getDefend();
			int[] lvs = SkillConsole.getPlayerSkillLv(playerSkill, player);
			atkLv = lvs[0];
			defLv = lvs[1];
		}
		return PlayerArchivePB.PlayerArchiveData.newBuilder()
				.setPlayerInfo(playerManager.getPlayerData(player))
				.addAllEquiList(equiList)
				.setLv(playerLv)
				.setStarLv(starLv)
				.setTrain(train)
				.setAtkId(atkId)
				.setAtkLv(atkLv)
				.setDefId(defId)
				.setDefLv(defLv)
				.build();
	}
	
	
	/**
	 * 转换
	 * items值： 0是不转，1是转换，
	 * iteam"串"分别代表：球队员等级，球员训练，装备，球衣，技能
	 * 装备，技能
	 *  等级(1),
	 * 	训练(2),
	 * 	装备(3),
	 *	球衣(4),
	 *	技能(5),
	 */
	@ClientMethod(code = ServiceCode.PlayerArchiveManager_transPlayer)
	public void trans(int player1, int player2, String items) {
		long teamId = getTeamId();
		List<Integer> itemList = Arrays.stream(items.split(",")).mapToInt(s-> new Integer(s)).boxed().collect(Collectors.toList());
		// 校验参数
		if(player1 == player2) {
			log.debug("操作异常，自己不能转给自己!");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(!PlayerConsole.existPlayer(player1) || !PlayerConsole.existPlayer(player2)) {
			log.debug("有不存在球员{},{}", player1, player2);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 
		if(items.equals("") || itemList.size() < 1) {
			log.debug("转成项为空：{}", items);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 是否专属技能球员检验
		if(itemList.get(ETransType.技能.ordinal()) == 1 && SkillConsole.checkPlayerSkillPositionBean(player1) != SkillConsole.checkPlayerSkillPositionBean(player2)) {
			log.debug("专属技能球员只能和专属转换!");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 所需转换卡检验
		int needNum = 0;
		for(int index=0; index < ETransType.values().length; index++) {
			if(itemList.get(index) != 1) continue;
			ETransType t = ETransType.values()[index];
			if(t == ETransType.装备) { // || t == ETransType.球衣
				PlayerEqui pe1 = teamEquiManager.getTeamEqui(teamId).getPlayerEqui(player1);
				PlayerEqui pe2 = teamEquiManager.getTeamEqui(teamId).getPlayerEqui(player2);
				int strLv = Math.max(pe1.getPlayerEqui().stream().mapToInt(e-> e.getStrLv()).max().orElse(0),
						pe2.getPlayerEqui().stream().mapToInt(e-> e.getStrLv()).max().orElse(0));
//				int lv = Math.max(pe1.getPlayerEqui().stream().mapToInt(e-> e.getLv()).max().orElse(0),
//						pe2.getPlayerEqui().stream().mapToInt(e-> e.getLv()).max().orElse(0));
				int lv = 0;
				int qua = Math.max(pe1.getPlayerEqui().stream().mapToInt(e-> e.getQuality()).max().orElse(0),
						pe2.getPlayerEqui().stream().mapToInt(e-> e.getQuality()).max().orElse(0));
				needNum += ArchiveConsole.getEquiNeedNum(strLv, lv, qua);
				continue;
			}
//			if(t == ETransType.等级) {
//				TeamPlayerGrade tpg = playerGradeManager.getTeamPlayerGrade(teamId);
//				PlayerGrade pg1 = tpg.getPlayerGrade(player1);
//				PlayerGrade pg2 = tpg.getPlayerGrade(player2);
//				int grade = Math.max(pg1 == null ? 0 : pg1.getGrade(), pg2 == null ? 0 : pg2.getGrade());
//				needNum += ArchiveConsole.getPlayerGradeNeedNum(grade);
//				continue;
//			}
//			if(t == ETransType.训练) {
//				TeamTrainPlayer ttp = trainPlayerManager.getTeamTrainPlayer(teamId);
//				TrainPlayer tp1 = ttp.getTrainPlayer(player1);
//				TrainPlayer tp2 = ttp.getTrainPlayer(player2);
//				int lv = Math.max(tp1 == null ? 0 : tp1.getItem(), tp2 == null ? 0 : tp2.getItem());
//				needNum += ArchiveConsole.getPlayerTrainNeedNum(lv);
//				continue;
//			}
			if(t == ETransType.技能) {
				TeamSkill ts = skillManager.getTeamSkill(teamId);
				PlayerSkill ps1 = ts.getPlayerSkill(player1);
				PlayerSkill ps2 = ts.getPlayerSkill(player2);
				int lv = Math.max(ps1 == null ? 0 : ps1.getMaxStep(), ps2 == null ? 0 : ps2.getMaxStep());
				needNum += ArchiveConsole.getPlayerSkillNeedNum(lv);
				continue;
			}
		}
		log.debug("需要转换卡数量：{}", needNum);
		PropBean pb = PropConsole.getProp(ArchiveConsole.TransCard_Item_ID);
		if(pb == null) {
			log.debug("道具不存在：{}", needNum);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_3.code).build());
			return;
		}
		if(!propManager.getTeamProp(teamId).checkPropNum(ArchiveConsole.TransCard_Item_ID, needNum)) {
			log.debug("转换卡数量不足：{}", needNum);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
			return;
		}
		// 扣掉转换卡
		propManager.delProp(teamId, ArchiveConsole.TransCard_Item_ID, needNum, true, false);
		// 转换选项
		for(int i=0; i<itemList.size(); i++) {
			if(itemList.get(i) == 0) continue;
			ETransType t = ETransType.values()[i];
			if(t == ETransType.装备) {
				teamEquiManager.transEqui(teamId, player1, player2); 
				continue;
			}
//			if(t == ETransType.球衣) {
//				teamEquiManager.transClothes(teamId, player1, player2);
//				continue;
//			}
//			if(t == ETransType.等级) {
//				playerGradeManager.transGrade(teamId, player1, player2);
//				continue;
//			}
//			if(t == ETransType.训练) {
//				trainPlayerManager.transTrain(teamId, player1, player2);
//				continue;
//			}
			if(t == ETransType.技能) {
				PlayerBean  pb1 = PlayerConsole.getPlayerBean(player1);
				PlayerBean  pb2 = PlayerConsole.getPlayerBean(player2);
				skillManager.transSkill(teamId, player1, player2, 
						playerManager.getTeamPlayer(teamId).getplayerPosition(pb1), 
						playerManager.getTeamPlayer(teamId).getplayerPosition(pb2));
				continue;
			}
		}
		// 回包结果
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(items).build());
	}
	
	
	public enum ETransType {
		等级(1),
		训练(2),
		装备(3),
		球衣(4),
		技能(5),
		;
		
		protected int type;
		private ETransType(int type) {
			this.type = type;
		}
	}
	
	
}
