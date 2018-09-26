package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.VipBean;
import com.ftkj.console.VipConsole;
import com.ftkj.constant.ChatPushConst;
import com.ftkj.db.ao.logic.ITeamVipAO;
import com.ftkj.db.domain.VipPO;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPayType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.vip.TeamVip;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.VipPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.Map;

public class VipManager extends BaseManager implements OfflineOperation {

    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private ITeamVipAO teamVipAO;
    @IOC
    private ChatManager chatManager;
    @IOC
    private TeamManager teamManager;

    /**
     * teamMap
     */
    private Map<Long, TeamVip> vipMap = Maps.newConcurrentMap();

    @Override
    public void instanceAfter() {
        // 注册事件监听
        for (EPayType type : EPayType.values()) {
            if (!type.isVip()) {
                continue;
            }
            EventBusManager.register(type.getEvent(), this);
        }
    }

    /**
     * 取VIP对象
     *
     * @param teamId
     * @return
     */
    public TeamVip getVip(long teamId) {
        TeamVip teamVip = vipMap.get(teamId);
        if (teamVip == null) {
            VipPO po = teamVipAO.getTeamVip(teamId);
            if (po == null) {
                po = new VipPO(teamId);
            }
            teamVip = new TeamVip(po);
            vipMap.put(teamId, teamVip);
            GameSource.checkGcData(teamId);
        }
        return teamVip;
    }

    @Override
    public void offline(long teamId) {
        vipMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        vipMap.remove(teamId);
    }

    /**
     * 充值
     *
     * @param param
     */
    @Subscribe
    public void addMoney(RechargeParam param) {
        if (!param.type.isVip()) {
            return;
        }
        TeamVip vip = getVip(param.teamId);
        vip.addMoney(param.fk);
        vip.save();
        checkVipUplevel(vip);
    }

    /**
     * 检查VIP是否升级
     *
     * @param vip
     */
    public void checkVipUplevel(TeamVip vip) {
        // 升级
        long teamId = vip.getTeamId();
        if (vip.updateLevel()) {
            log.debug("【{}】用户VIP升级：{}", teamId, vip.getLevel());
            EventBusManager.post(EEventType.VIP升级, teamId);

            // VIP升级广播
            Team team = teamManager.getTeam(teamId);
            if (vip.getLevel() > ChatPushConst.CHAT_PUSH_VIP_LEVEL_7) {
                chatManager.pushGameTip(EGameTip.VIP升级, 0, team.getName(), vip.getLevel() + "");
            }
        }
        // 推送VIP
        sendMessage(teamId, getVipData(teamId), ServiceCode.VipManager_push_vip);
    }

    /**
     * 购买VIP等级礼包
     */
    @ClientMethod(code = ServiceCode.VipManager_buy_gift)
    public void buyGift(int level) {
        long teamId = getTeamId();
        TeamVip vip = getVip(teamId);
        if (!vip.isVip() || vip.getLevel() < level) {
            log.debug("vip权限不足, 等级{}", vip.getLevel());
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        // 是VIP且等级满足可以购买
        TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
        VipBean bean = VipConsole.getVipLevelBean(level);
        if (vip.getBuyStatus().containsValue(level)) {
            log.debug("已购买过{}", level);
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Prop_4.code).build());
            return;
        }
        if (teamMoney.getMoney() < bean.getSalePrice()) {
            log.debug("球券不足{}", bean.getSalePrice());
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Money_1.code).build());
            return;
        }
        vip.getBuyStatus().addValue(level);
        vip.save();
        //
        ModuleLog moduleLog = ModuleLog.getModuleLog(EModuleCode.VIP, "VIP礼包");
        // 扣券
        moneyManager.updateTeamMoney(teamId, -bean.getSalePrice(), 0, 0, 0, true, moduleLog);
        // 发东西
        propManager.addPropList(teamId, bean.getGiftList(), true, moduleLog);
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 签到处的每日福利
     * 每日可以领取礼包
     * 领取状态存redis
     */
    @ClientMethod(code = ServiceCode.VipManager_sign_gift)
    public void everyDayGift() {
        long teamId = getTeamId();
        TeamVip vip = getVip(teamId);
        if (!vip.isVip()) {
            log.debug("不是VIP[{}]！", vip.getLevel());
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        // 是否已领
        String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Every_Day_Gift);
        int isGet = redis.getIntNullIsZero(key);
        if (isGet == 1) {
            log.debug("已领过奖励");
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
            return;
        }
        redis.set(key, "1", RedisKey.DAY);
        // 发奖
        propManager.addPropList(teamId, VipConsole.getVipLevelBean(vip.getLevel()).getFreeGift(), true, ModuleLog.getModuleLog(EModuleCode.VIP, "每日签到"));
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 每日礼包领奖状态
     *
     * @return
     */
    public int getEveryDayGiftStatus(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Every_Day_Gift);
        int isGet = redis.getIntNullIsZero(key);
        return isGet;
    }

    //	public Float getVipBuff(long teamId, EBuffType buf) {
    //		TeamVip vip = getVip(teamId);
    //		if(!vip.isVip()) return new Float(0);
    //		// 加成值
    //		return VipConsole.getVipLevelBean(vip.getLevel()).getBuffMap().get(buf.getId());
    //	}

    /**
     * VIP的buff加成
     *
     * @param teamId
     * @return
     */
    public TeamBuff getVipBuff(long teamId, EBuffType buf) {
        TeamVip vip = getVip(teamId);
        if (!vip.isVip()) {
            return null;
        }
        int value = VipConsole.getVipLevelBean(vip.getLevel())
            .getBuffMap()
            .get(buf.getId())
            .get("v");
        // 加成值
        return new TeamBuff(EBuffKey.VIP加成, buf, new int[]{value}, DateTime.now().plusDays(1), false);
    }

    public int getVipValue(long teamId, EBuffType buf) {
        TeamVip vip = getVip(teamId);
        if (!vip.isVip()) {
            return 0;
        }
        VipBean vb = VipConsole.getVipLevelBean(vip.getLevel());
        if (vb == null) {
            return 0;
        }
        return vb.getBuffValue(buf);
    }

    /**
     * 取VIP协议
     *
     * @param teamId
     * @return
     */
    public VipPB.VipData getVipData(long teamId) {
        TeamVip vip = getVip(teamId);
        return VipPB.VipData.newBuilder()
            .setTeamId(teamId)
            .setLevel(vip.getLevel())
            .setExp(vip.getTotalExp())
            .addAllBuyStatus(vip.getBuyStatus().getList())
            .build();
    }
}
