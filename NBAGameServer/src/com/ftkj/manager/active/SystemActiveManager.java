package com.ftkj.manager.active;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.longtime.AtvTeamCloseManager;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.SystemActivePB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * 活动接口
 *
 * @author Jay
 * @time:2017年9月13日 下午5:36:14
 */
public class SystemActiveManager extends BaseManager {

    @IOC
    private AtvTeamCloseManager atvTeamCloseManager;

    @Override
    public void instanceAfter() {

    }

    /**
     * 客户端主动拉取
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_getAllActive)
    public void getAllSystemActive() {
        sendMessage(getAllActiveData());
    }

    /**
     * 登录发送
     * 主动拉取
     * 所有活动的数据
     */
    public SystemActivePB.SystemActiveData getAllActiveData() {
        List<SystemActivePB.ActiveData> dataList = Lists.newArrayList();
        for (SystemActiveBean active : SystemActiveConsole.getAllSystemActiveEffect()) {
            dataList.add(getActiveData(active));
        }
        // 用户关闭了入口的列表也要返回
        long teamId = getTeamId();
        List<Integer> closeList = atvTeamCloseManager.getCloseList(teamId);
        return SystemActivePB.SystemActiveData.newBuilder().addAllActiveList(dataList).addAllCloseList(closeList).build();
    }

    /**
     * 单个活动数据的封装
     *
     * @param active
     * @return
     */
    public SystemActivePB.ActiveData getActiveData(SystemActiveBean active) {
        DateTime now = DateTime.now();
        return SystemActivePB.ActiveData.newBuilder()
                .setAtvId(active.getAtvId())
                .setStartTime(active.getStartDateTime().getMillis())
                .setEndTime(active.getEndDateTime().getMillis())
                .setStatus(SystemActiveConsole.getActiveStatus(active.getAtvId(), now).getStatusCode())
                .build();
    }

    /**
     * 活动变动推送,任务调度每秒检测，主动推送活动状态变更
     *
     * @param atvId
     */
    public void pushActiveChange(int atvId) {
        sendMessage(GameSource.getUsers(), getActiveData(SystemActiveConsole.getSystemActive(atvId)), ServiceCode.SystemActiveManager_Push_Active);
    }

    /**
     * 清除指定活动的用户内存数据
     *
     * @param atvId
     * @param teamId
     */
    public void clearTeamData(int atvId, long teamId) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.clearActiveBase(teamId);
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        }
    }
    
    //===========================================

    /**
     * 通用活动主界面回包
     *
     * @param atvid
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_ShowView)
    public void showView(int atvId) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.showView();
        }
    }

    /**
     * 通用的领取奖励接口，只领一次
     *
     * @param atvId
     * @param id
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_GetAward)
    public void getAtvAward(int atvId, int id) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.getAward(id);
        }
    }

    /**
     * 领取每天奖励
     *
     * @param atvId
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_GetDayAward)
    public void getAtvDayAward(int atvId) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.getDayAward();
        }
    }

    /**
     * 购买直接完成
     *
     * @param atvId
     * @param id
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_BuyFinish)
    public void buyFinish(int atvId, int id) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.buyFinish(id);
        }
    }

    /**
     * 抽奖接口
     *
     * @param atvId
     * @param id
     */
    @ClientMethod(code = ServiceCode.SystemActiveManager_lottery)
    public void lottery(int atvId, int id) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.lottery(id);
        }
    }

    public void gmResetAtv(long teamId, int atvId, AtvCommonPB.AtvCommonData cd) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.gmReset(teamId, cd);
        }
    }

}
