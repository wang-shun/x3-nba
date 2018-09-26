package com.ftkj.manager.active;

import java.util.List;

import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;


@ActiveAnno(redType=ERedType.活动, atv = EAtv.节日礼包)
public class AtvFestivalManager extends ActiveBaseManager {

	@Override
	public void showView() {
		long teamId = getTeamId();
        ActiveBase atvObj = getTeamData(teamId);
        List<PropSimple> awardList = PropSimple.getPropBeanByStringNotConfig(getConfigStr("award"), "_", ":");
        //
        sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
                .setAtvId(this.getId())
                .addAllFinishStatus(atvObj.getFinishStatus().getList())
                .addAllAwardStatus(atvObj.getAwardStatus().getList())
                // 0未完成，1可领，2已领
                .setValue(atvObj.getiData1())
                .setOther(atvObj.getiData2())
                // 礼包提示
                .setExtend(getConfigStr("tip"))
                // 礼包奖励
                .addAllViewAward(PropManager.getPropSimpleListData(awardList))
                .build());
	}
	
	@Override
	public void getDayAward() {
		// 奖励是否存在
		if(getStatus() != EActiveStatus.进行中) {
			log.debug("兑换奖励类型没有找到!");
    		sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Active_1.code).build());
			return;
		}
    	SystemActiveBean bean = getBean();
    	if(bean == null) {
    		log.debug("兑换奖励类型没有找到!");
    		sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Active_3.code).build());
            return;
    	}
    	long teamId = getTeamId();
    	ActiveBase atvObj = getTeamData(teamId);
    	if(atvObj.getiData1() != 1) {
    		log.debug("不可领取奖励!");
    		sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Active_5.code).build());
    		return;
    	}
    	atvObj.setiData1(2);
    	atvObj.save();
    	List<PropSimple> awardList = PropSimple.getPropBeanByStringNotConfig(getConfigStr("award"), "_", ":");
    	propManager.addPropList(teamId, awardList, true, getActiveModuleLog());
    	sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(0)
				.setValue(atvObj.getiData1())
				.setCode(ErrorCode.Success.code)
				.addAllAwardList(PropManager.getPropSimpleListData(awardList))
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	/**
	 * 默认可领取礼包
	 */
	@Override
	public <T extends ActiveBase> void createInit(T t) {
		t.setiData1(1);
		t.save();
	}
}
