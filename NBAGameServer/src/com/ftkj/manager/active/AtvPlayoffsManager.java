package com.ftkj.manager.active;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.server.GameSource;
import com.ftkj.util.DateTimeUtil;


@ActiveAnno(redType=ERedType.活动, atv = EAtv.季后赛总冠军活动)
public class AtvPlayoffsManager extends ActiveBaseManager {
	
	private static final int Email_ID = 40005;
	
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
                .setExtend(getConfigStr("voteEndtime"))
                .build());
	}
	
	@Override
	public void buyFinish(int tp) {
		if(getStatus() != EActiveStatus.进行中) {
			return;
		}
		if(tp < 1 || tp > 2) {
			log.debug("参数异常");
			return;
		}
		// 必须是在截止时间之前
		int endDay = DateTimeUtil.parseToLdt(getConfigStr("voteEndtime")).getDayOfYear();
		if(DateTime.now().getDayOfYear() > endDay) {
			log.debug("已截止投票!");
			return;
		}
		long teamId = getTeamId();
		ActiveBase atvData = getTeamData(teamId);
		atvData.setiData1(tp);
		atvData.save();
		//
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(tp)
				.setValue(tp)
				.setCode(ErrorCode.Success.code)
				.addAllFinishStatus(atvData.getFinishStatus().getList())
				.addAllAwardStatus(atvData.getAwardStatus().getList())
				.build());
	}
	
	
	@Override
	public void shootEvent() {
		super.shootEvent();
		if(getStatus() != EActiveStatus.进行中) {
			return;
		}
		int endDay = DateTimeUtil.parseToLdt(getConfigStr("voteEndtime")).getDayOfYear();
		if(DateTime.now().getDayOfYear() < endDay) {
			log.debug("投票时间还没有截止...");
			return;
		}
		// 结果
		int winTeam = getConfigInt("winTeam", 0);
		if(winTeam != 1 && winTeam != 2) {
			log.debug("winTeam={}异常，不是正常值", winTeam);
			return;
		}
		// 保证只发一次奖励
		ActiveBase sData = getShareData();
		if(sData.getiData1() == 1) {
			log.error("该奖励已发放，请不要重复发奖, atvId={}", this.getId());
			return;
		}
		sData.setiData1(1);
		sData.save();
		// 发奖
		List<PropSimple> awardList = getAwardConfigList().get(winTeam).getPropSimpleList();
		List<ActiveBasePO> list = activeAO.queryActiveDataByValue(GameSource.shardId, this.getId(), winTeam);
		String awardConfig = PropSimple.getPropStringByListNotConfig(awardList);
		for(ActiveBasePO po : list) {
			// 发奖励的
			po.setFinishStatus(winTeam + "");
			po.save();
			emailManager.sendEmail(po.getTeamId(), Email_ID, "", awardConfig);
		}
	}
	
	
}
