package com.ftkj.manager.active;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
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
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.server.GameSource;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

@EventRegister({EEventType.得到球员})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.公测_最佳经理, clazz=AtvBetaSignPlayerManager.AtvSignPlayer.class)
public class AtvBetaSignPlayerManager extends ActiveBaseManager {

	@IOC
	private PlayerManager playerManager;
	
	/**
	 * 主要收集阵容
	 */
	private Set<Integer> mainPlayerIds;
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		mainPlayerIds = Sets.newHashSet();
		getAwardConfigList().values().stream().forEach(c-> {
			mainPlayerIds.addAll(Arrays.stream(c.getConditionMap().get("playerIds").split("_")).mapToInt(s-> Integer.valueOf(s)).boxed().collect(Collectors.toSet()));
		});
	}
	
	/**
	 * 得到球员
	 * @param player
	 */
	@Subscribe
	public void signPlayer(Player player) {
		if(getStatus() != EActiveStatus.进行中) return;
		if(!mainPlayerIds.contains(player.getPlayerRid())) return;
		long teamId = player.getTeamId();
		AtvSignPlayer atvObj = getTeamData(teamId);
		if(atvObj.getCollectPlayerIds().containsValue(player.getPlayerRid())) return;
		atvObj.getCollectPlayerIds().addValue(player.getPlayerRid());
		// 检查是否完成
		checkFinish(atvObj);
		atvObj.save();
		redPointPush(teamId);
	}
	
	/**
	 * 检查是否完成收集
	 * @param atvObj
	 */
	private void checkFinish(AtvSignPlayer atvObj) {
		for(int awardId : getAwardConfigList().keySet()) {
			List<Integer> needIds = Arrays.stream(getAwardConfigList().get(awardId).getConditionMap().get("playerIds").split("_")).mapToInt(pid-> Integer.valueOf(pid)).boxed().collect(Collectors.toList());
			if(needIds.stream().anyMatch(pid-> !atvObj.getCollectPlayerIds().containsValue(pid))) {
				continue;
			}
			atvObj.getFinishStatus().addValue(awardId);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AtvSignPlayer createActiveData(long teamId) {
		AtvSignPlayer atvObj = new AtvSignPlayer(new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
		// 第一次创建，把阵容的所有球员加进入
		if(atvObj.getiData2() == 0) {
			atvObj.setiData2(1);
			List<Integer> playerIds = playerManager.getTeamPlayer(teamId).getAllPlayerIds().stream().filter(pid-> mainPlayerIds.contains(pid)).collect(Collectors.toList());
			for(int playerId : playerIds) {
				atvObj.getCollectPlayerIds().addValue(playerId);
			}
			checkFinish(atvObj);
			atvObj.save();
		} 
		return atvObj;
	}
	
	@Override
	public void showView() {
		long teamId = getTeamId();
        ActiveBase atvObj = getTeamData(teamId);
        sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
                .setAtvId(this.getId())
                .addAllFinishStatus(atvObj.getFinishStatus().getList())
                .addAllAwardStatus(atvObj.getAwardStatus().getList())
                .setValue(atvObj.getiData1())
                .setOther(atvObj.getiData2())
                .setExtend(atvObj.getsData3())
                .build());
	}
	
	
	/**
	 * 球队收集
	 */
	public static class AtvSignPlayer extends ActiveBase {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@ActiveDataField(fieldName="sData3")
		private DBList collectPlayerIds;

		public AtvSignPlayer(ActiveBasePO po) {
			super(po);
		}

		public DBList getCollectPlayerIds() {
			return collectPlayerIds;
		}
		
	}

}
