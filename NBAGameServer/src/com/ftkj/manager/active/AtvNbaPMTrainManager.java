package com.ftkj.manager.active;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.proto.AtvCommonPB;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;


@EventRegister({EEventType.得到球员, EEventType.身价更新})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.经理培训班, clazz=AtvNbaPMTrainManager.AtvPlayerPMTrain.class)
public class AtvNbaPMTrainManager extends ActiveBaseManager {

	@IOC
	private PlayerManager playerManager;
	
	private int[][] taskCondition = {
			{+100, 10},
			{+300, 20},
			{-50, 10},
			{-100, 20},
			// 底薪都是 < 40
			{1, 10},
			{2, 30},
	};
	
	/**
	 * 得到球员
	 * @param player
	 */
	@Subscribe
	public void signPlayer(Player player) {
		if(getStatus() != EActiveStatus.进行中) return;
		long teamId = player.getTeamId();
		AtvPlayerPMTrain atvObj = getTeamData(teamId);
		// 检查是否完成
		checkFinish(atvObj, Lists.newArrayList(player));
		atvObj.save();
		redPointPush(teamId);
	}
	
	/**
	 * 身价更新
	 * @param version
	 */
	@Subscribe
	public void updatePlayerPrice(Integer version) {
	}
	
//	@Override
//	public <T extends ActiveBase> void createInit(T t) {
//		long teamId = t.getTeamId();
//		TeamPlayer tp = playerManager.getTeamPlayer(teamId);
//		List<Player> plist = Lists.newArrayList(tp.getPlayers());
//		plist.addAll(tp.getStorages());
//		checkFinish((AtvPlayerPMTrain)t, plist);
//	}
	
	@Override
	public void showView() {
		long teamId = getTeamId();
		AtvPlayerPMTrain atvObj = getTeamData(teamId);
		TeamPlayer tp = playerManager.getTeamPlayer(teamId);
		List<Player> plist = Lists.newArrayList(tp.getPlayers());
		plist.addAll(tp.getStoragesAndMarket());
		checkFinish(atvObj, plist);
		redPointPush(teamId);
        sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
                .setAtvId(this.getId())
                .addAllFinishStatus(atvObj.getFinishStatus().getList())
                .addAllAwardStatus(atvObj.getAwardStatus().getList())
                .setValue(atvObj.getiData1())
                .setOther(atvObj.getiData2())
                .setExtend(atvObj.getsData3())
                .build());
	}

	
	private void checkFinish(AtvPlayerPMTrain atvData, List<Player> playerList) {
		// 计算底薪，统计上限50个
		if(atvData.lowPlayer.getSize() < 50) {
			List<Player> lowList = playerList.stream()
					.filter(p-> playerManager.isMinPricePlayer(p.getPlayerRid(), p.getPrice()))
					.collect(Collectors.toList());
			for(Player player : lowList) {
				//log.error("满足底薪，球员playerId={}, price={}, basePrice={}", player.getPlayerRid(), player.getPrice(), playerManager.getBasePrice(player.getPlayerRid()));
				if(atvData.lowPlayer.containsValue(player.getId())) {
					continue;
				}
				// 做个统计上限
				if(atvData.lowPlayer.getSize() >= 50) {
					continue;
				} 
				atvData.lowPlayer.addValue(player.getId());
			}
		}
		int lowCount = atvData.lowPlayer.count();
		//
		for(int i=0; i<taskCondition.length; i++) {
			if(atvData.condition.containsValue(i)) {
				continue;
			}
			for(Player player : playerList) {
				if(PlayerConsole.existCreateXPlayer(player.getPlayerRid())) {
					continue;
				}
				//log.error(" 经理培训活动 playerId={},市价={},身价={}", player.getPlayerRid(), player.getPlayerBean().getPrice(), player.getPrice());
				if(Math.abs(taskCondition[i][0]) >= 50) {
					if(taskCondition[i][0] > 0 && player.getPlayerBean().getPrice() - player.getPrice() >= Math.abs(taskCondition[i][0]) ) {
						//log.error("满足条件={}, playerId={}, price={}, basePrice={}, {}>={}", i, player.getPlayerRid(), player.getPrice(), playerManager.getBasePrice(player.getPlayerRid()), player.getPlayerBean().getPrice() - player.getPrice(), Math.abs(taskCondition[i][0]));
						atvData.condition.addValue(i);
						atvData.addScore(taskCondition[i][1]);
						break;
					}else if(taskCondition[i][0] < 0 && player.getPrice() - player.getPlayerBean().getPrice() >= Math.abs(taskCondition[i][0])) {
						//log.error("满足条件={}, playerId={}, price={}, basePrice={}, {}>={}", i, player.getPlayerRid(), player.getPrice(), playerManager.getBasePrice(player.getPlayerRid()), player.getPrice() - player.getPlayerBean().getPrice(), Math.abs(taskCondition[i][0]));
						atvData.condition.addValue(i);
						atvData.addScore(taskCondition[i][1]);
						break;
					}
				}
				// 底薪个数
				else if(Math.abs(taskCondition[i][0]) < 50 && lowCount >= taskCondition[i][0]){
					atvData.condition.addValue(i);
					atvData.addScore(taskCondition[i][1]);
					break;
				}
			}
		}
		//
		Map<Integer, SystemActiveCfgBean> awardMap = getAwardConfigList();
		for(int id : awardMap.keySet()) {
			if(atvData.getFinishStatus().containsValue(id)) {
				continue;
			}
			if(atvData.getScore() >= Integer.valueOf(awardMap.get(id).getConditionMap().get("score"))) {
				atvData.getFinishStatus().addValue(id);
			}
		}
		atvData.save();
	}
	
	public static class AtvPlayerPMTrain extends ActiveBase {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@ActiveDataField(fieldName="sData3")
		private DBList condition;
		@ActiveDataField(fieldName="sData4")
		private DBList lowPlayer;
		
		public AtvPlayerPMTrain(ActiveBasePO po) {
			super(po);
		}
		
		public void addScore(int score) {
			this.setiData1(this.getiData1() + score);
		}
		
		public int getScore() {
			return this.getiData1();
		}
		
	}
	
}
