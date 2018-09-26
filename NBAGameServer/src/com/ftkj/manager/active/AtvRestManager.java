package com.ftkj.manager.active;

import com.ftkj.annotation.IOC;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.logic.BuffManager;
import com.ftkj.manager.logic.MainMatchManager;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;

import org.joda.time.DateTime;

@ActiveAnno(redType = ERedType.活动, atv = EAtv.恢复精力活动)
public class AtvRestManager extends ActiveBaseManager {
    /*
     * 		每天12：00-14：00和18：00-20：00可分别领取一次挑战次数
     * 	每次领取恢复5次主线赛程挑战次数，领取的挑战次数不能超过10次上限
     * 		提示语：
            不在领取时间内：喝咖啡时间12：00-14：00，18：00-20：00！
            在领取时间内：
            玩家当前挑战次数小于等于5：恭喜您恢复5次挑战次数！
            玩家当前挑战次数大于等于6：您当前挑战次数充足，请稍后领取！

     */
    @IOC
    private MainMatchManager mainMatchManager;
    @IOC
    private BuffManager buffManager;

    @Override
    public void showView() {
        long teamId = getTeamId();
        ActiveBase atvObj = getTeamDataRedisDay(teamId);
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
     * 根据当前时间计算可领取奖励
     *
     * @param now
     * @return 返回 0是：不在领取时间段；1是：12：00-14：00； 2是：18：00-20：00
     */
    private int getTimeIndex(DateTime now) {
        int hour = now.getHourOfDay();
        if (hour >= 12 && hour < 14) {
            return 1;
        }
        if (hour >= 18 && hour < 20) {
            return 2;
        }
        return 0;
    }

    @Override
    public void getDayAward() {
        long teamId = getTeamId();
        DateTime now = DateTime.now();
        // 时间段
        int timeIndex = getTimeIndex(now);
        if (getStatus(now) != EActiveStatus.进行中) {
            sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(timeIndex).setCode(ErrorCode.Active_1.code).build());
            return;
        }
        if (timeIndex == 0) {
            log.debug("不在领取时间内：喝咖啡时间12：00-14：00，18：00-20：00！");
            sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(timeIndex).setCode(ErrorCode.MMatch_Num_Rest_GetError.code).build());
            return;
        }
        // 是否已领
        ActiveBase atvData = getTeamDataRedisDay(teamId);
        if (atvData.getAwardStatus().containsValue(timeIndex)) {
            log.debug("已领取过奖励");
            sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(timeIndex).setCode(ErrorCode.Active_7.code).build());
            return;
        }
        // 现有次数是否能领
        int count = mainMatchManager.getMatchNum(teamId);
        if (count > 5) {
            log.debug("每次领取恢复5次主线赛程挑战次数，领取的挑战次数不能超过10次上限");
            sendMessage(AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(timeIndex).setCode(ErrorCode.MMatch_Num_Beyond_Max.code).build());
            return;
        }
        // 领取，保存状态
        int addCount = getConfigInt("awardCount", 5);
        int vipSize = buffManager.getBuffSet(teamId, EBuffType.Main_Match_Num).getValueSum();
        ErrorCode code = mainMatchManager.addMatchNum(teamId, addCount + vipSize);
        if (code == ErrorCode.Success) {
            atvData.getAwardStatus().addValue(timeIndex);
            saveDataReidsDay(teamId, atvData);
        }
        sendMessage(AtvAwardData.newBuilder()
            .setAtvId(this.getId())
            .setAwardId(timeIndex)
            .setValue(addCount)
            .setCode(code.code)
            .addAllAwardStatus(atvData.getAwardStatus().getList())
            .build());
    }

}
