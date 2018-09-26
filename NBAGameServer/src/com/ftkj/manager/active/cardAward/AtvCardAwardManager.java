package com.ftkj.manager.active.cardAward;

import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.proto.AtvCardAwardPB;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 月卡周卡
 * @author Jay
 * @time:2017年9月15日 下午4:00:59
 */
public class AtvCardAwardManager extends BaseManager {
	
	public static int[] atvIds = {
//			EAtv.装备强化周卡.getAtvId(),
//			EAtv.装备经验月卡.getAtvId(),
//			EAtv.经验手册月卡.getAtvId(),
//			EAtv.训练点月卡.getAtvId(),
	};
	
	@Override
	public void instanceAfter() {
	}
	
	/**
	 * 主界面
	 */
	@ClientMethod(code = ServiceCode.AtvCardAwardManager_showView)
	public void showView() {
		// 格式 atvId,status,days
		long teamId = getTeamId();
		List<AtvCardAwardPB.AtvCardData> list = Lists.newArrayList();
		for(int atvId : atvIds) {
			ActiveBaseManager manager =  ActiveBaseManager.getManager(atvId);
			AtvCardAwardBaseManager managerImpl = (AtvCardAwardBaseManager)manager;
			list.add(managerImpl.getTeamActiveData(teamId));
		}
		sendMessage(AtvCardAwardPB.AtvCardAwardData.newBuilder().addAllAtvList(list).build());
	}
	
	/**
	 * 月卡周卡类领
	 * @param atvId
	 */
	@ClientMethod(code = ServiceCode.AtvCardAwardManager_getAward)
	public void getCardAward(int atvId) {
		AtvCardAwardBaseManager manager = (AtvCardAwardBaseManager) ActiveBaseManager.getManager(atvId);
		if(manager != null) {
			manager.getCardAward();
		}
	}


}
