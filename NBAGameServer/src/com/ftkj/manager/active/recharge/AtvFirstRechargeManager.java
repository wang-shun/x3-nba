package com.ftkj.manager.active.recharge;

import com.ftkj.annotation.IOC;
import com.ftkj.console.PropConsole;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPropType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.prop.bean.PropPlayerBean;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.ServiceCode;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 首冲任意金额
 *
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.充值})
@ActiveAnno(redType = ERedType.活动, atv = EAtv.首冲送礼包, clazz = AtvFirstRechargeManager.AtvFirstRecharge.class)
public class AtvFirstRechargeManager extends ActiveBaseManager {
    private static final Logger log = LoggerFactory.getLogger(AtvFirstRechargeManager.class);
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TaskManager taskManager;

    /**
     * 首冲活动
     *
     * @author Jay
     * @time:2017年9月8日 上午10:50:33
     */
    public static class AtvFirstRecharge extends ActiveBase {
        private static final long serialVersionUID = 1L;

        public AtvFirstRecharge(ActiveBasePO po) {
            super(po);
        }

        public void setMoney(int money) {
            this.setiData1(money);
        }

        public int getMoney() {
            return this.getiData1();
        }
    }

    /**
     * 充值回调
     *
     * @param param
     */
    @Subscribe
    public void addMoneyFK(RechargeParam param) {
        if (getStatus(param.time) != EActiveStatus.进行中) { return; }
        long teamId = param.teamId;
        AtvFirstRecharge atvObj = getTeamData(teamId);
        int needMoney = getConfigInt("money", 0);
        if (param.fk >= needMoney && atvObj.getiData2() == 0) {
            atvObj.setMoney(param.fk);
            atvObj.setiData2(1);
            atvObj.save();
        }
        //
        redPointPush(teamId);
    }

    @ClientMethod(code = ServiceCode.AtvFirstRechargeManager_getAward)
    public void getAward() {
        long teamId = getTeamId();
        AtvFirstRecharge atvObj = getTeamData(teamId);
        if (atvObj.getiData2() != 1) {
            log.debug("不满足领取条件！");
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
            return;
        }
        List<PropSimple> propsList = getAwardConfigList().get(0).getPropSimpleList();
        List<PropSimple> resultList = Lists.newArrayList();
        for (PropSimple ps : propsList) {
            PropBean pb = PropConsole.getProp(ps.getPropId());
            if (pb == null) {
                continue;
            }
            if (pb.getType() == EPropType.Wrap_Player || pb.getType() == EPropType.Player) {
                PropExtPlayerBean prb = PropConsole.getPlayerProp(pb);
                if (prb == null) {
                    continue;
                }
                ErrorCode ret = addPlayer(teamId, ps, prb, prb.isBind());
                if (ret.isError()) {
                    sendErrorCode(ret);
                    return;
                }
            } else {
                resultList.add(ps);
            }
        }
        atvObj.setiData2(2);
        atvObj.save();
        propManager.addPropList(teamId, resultList, true, getActiveModuleLog());

        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        redPointPush(teamId);
    }

    private ErrorCode addPlayer(long teamId, PropSimple ps, PropPlayerBean playerBean, boolean bind) {
        if (playerBean == null) {
            return ErrorCode.Player_Prop_Bean_Null;
        }
        return playerManager.addPlayerAuto(teamId, playerBean.getHeroId(), ps.getNum(), bind, ModuleLog.getModuleLog(EModuleCode.充值福利, "首充送球员"));
    }

    /**
     * 主界面
     */
    @Override
    @ClientMethod(code = ServiceCode.AtvFirstRechargeManager_showView)
    public void showView() {
        long teamId = getTeamId();
        AtvFirstRecharge atvObj = getTeamData(teamId);
        sendMessage(AtvCommonData.newBuilder()
            .setAtvId(getId())
            .setValue(atvObj.getiData2())
            .build());
    }

    @Override
    public boolean checkHideWindow(long teamId) {
        return getTeamData(teamId).getiData2() == 2;
    }

    @Override
    public int redPointNum(long teamId) {
        AtvFirstRecharge atvObj = getTeamData(teamId);
        int num = atvObj.getiData1() == 1 ? 1 : 0;
        return num;
    }

}
