package com.ftkj.manager.active;

import java.util.List;

import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.google.common.collect.Lists;

@ActiveAnno(redType=ERedType.活动, atv = EAtv.公测_折扣礼包)
public class AtvBetaGiftAwardManager extends ActiveBaseManager {

	/**
	 * 购买完成
	 */
	@Override
	public void buyFinish(int tp) {
		long teamId = getTeamId();
		ActiveBase atvObj = getTeamData(teamId);
		TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
		ErrorCode code = checkBuyFinish(atvObj, teamMoney, tp);
		List<PropSimple> awardList = Lists.newArrayList();
		if(code == ErrorCode.Success) {
			// 直接完成
			atvObj.getFinishStatus().addValue(tp);
			atvObj.getAwardStatus().addValue(tp);
			atvObj.save();
			// 直接发奖
			awardList = sendAward(teamId, atvObj, tp);
		}
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(tp)
				.setCode(code.code)
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.addAllAwardList(PropManager.getPropSimpleListData(awardList))
				.build());
	}

}
