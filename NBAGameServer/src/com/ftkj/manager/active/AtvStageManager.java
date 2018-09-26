package com.ftkj.manager.active;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.StagePassParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.MainMatchManager;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主线赛程通关送球员
 *
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@EventRegister({EEventType.主线赛程通关})
@ActiveAnno(redType = ERedType.活动, atv = EAtv.主线赛程通关送球员)
public class AtvStageManager extends ActiveBaseManager {
    private static final Logger log = LoggerFactory.getLogger(AtvStageManager.class);
    @IOC
    private PlayerManager playerManager;

    @IOC
    private MainMatchManager mainMatchManager;

    @Subscribe
    public void callback(StagePassParam param) {
        int newValue = param.stageId;
        SystemActiveCfgBean cfg = getAwardConfigList().get(newValue);
        if (cfg == null) { return; }
        ActiveBase atvObj = getTeamData(param.teamId);
        if (atvObj.getFinishStatus().containsValue(newValue)) { return; }
        atvObj.getFinishStatus().addValue(newValue);
        atvObj.save();
        // 推送消息包
        pushData(param.teamId);
    }


    private void pushData(long teamId) {
        ActiveBase atvObj = getTeamData(teamId);
        sendMessage(teamId, AtvCommonPB.AtvCommonData.newBuilder()
            .setAtvId(this.getId())
            .addAllFinishStatus(atvObj.getFinishStatus().getList())
            .addAllAwardStatus(atvObj.getAwardStatus().getList())
            .setValue(atvObj.getiData1())
            .setOther(atvObj.getiData2())
            .setExtend(atvObj.getsData3())
            .build(), ServiceCode.SystemActiveManager_push_data);
    }

    /**
     * 验证是否有位置
     */
    @Override
    public ErrorCode checkGetAwardCustom(long teamId, ActiveBase atvObj, int id) {
    	int awardPlayerNum = 0;
    	Map<Integer, List<PropSimple>> map = getAwardPlayersNum(id);
    	if (map.get(1) != null) {
			awardPlayerNum = map.get(1).size();
		}
    	
        if (playerManager.getStorageSize(teamId) < awardPlayerNum) {
            return ErrorCode.Player_Storage_Full;
        }
        return ErrorCode.Success;
    }
    
    
    // 获得的球员直接入仓库,也要兼容奖励的不止是球员
    @Override
    public List<PropSimple> sendAward(long teamId, ActiveBase activeData, int id) {
    	sendAward(teamId, id);
        SystemActiveCfgBean cfg = getAwardConfigList().get(id);
        return new ArrayList<PropSimple>(cfg.getPropSimpleList());
    }

}
