package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SignMonthBean;
import com.ftkj.cfg.SignPeriodBean;
import com.ftkj.console.SignConsole;
import com.ftkj.db.ao.logic.ISignAO;
import com.ftkj.db.domain.SignPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.sign.TeamSign;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.SignPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

/**
 * 签到
 *
 * @author Jay
 * @time:2017年8月26日 上午11:52:15
 */
public class SignManager extends BaseManager implements OfflineOperation {

    @IOC
    private ISignAO signAO;
    private Map<Long, TeamSign> signMonthMap;
    private Map<Long, TeamSign> signPeriodMap;

    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private VipManager vipManager;

    @Override
    public void instanceAfter() {
        signMonthMap = Maps.newConcurrentMap();
        signPeriodMap = Maps.newConcurrentMap();

    }

    /**
     * 月份不能随便传，会更新最新签到月份的数据
     *
     * @param teamId
     * @param month
     * @return
     */
    public TeamSign getSignMonth(long teamId, int month) {
        TeamSign signMonth = signMonthMap.get(teamId);
        if (signMonth == null) {
            SignPO signMonthPO = signAO.getSignMonth(teamId);
            if (signMonthPO == null) {
                signMonthPO = new SignPO(teamId, 1, month);
            }
            signMonth = new TeamSign(signMonthPO);
            signMonthMap.put(teamId, signMonth);
        }
        // 新的月份处理
        if (signMonth.getPeriod() != month) {
            signMonth.setPeriod(month);
            signMonth.clearSignNum();
            signMonth.addPatchNum();
            signMonth.save();
        }
        return signMonth;
    }

    /**
     * 7天签到的对象
     *
     * @param teamId
     * @return
     */
    public TeamSign getSignPeriod(long teamId) {
        TeamSign signPeriod = signPeriodMap.get(teamId);
        if (signPeriod == null) {
            SignPO signPeriodPO = signAO.getSignPeriod(teamId);
            if (signPeriodPO == null) {
                signPeriodPO = new SignPO(teamId, 2, 0);
            }
            signPeriod = new TeamSign(signPeriodPO, 7);
            signPeriodMap.put(teamId, signPeriod);
            GameSource.checkGcData(teamId);
        }
        return signPeriod;
    }

    @Override
    public void offline(long teamId) {
        signPeriodMap.remove(teamId);
        signMonthMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        signPeriodMap.remove(teamId);
        signMonthMap.remove(teamId);
    }

    /**
     * 签到界面
     */
    @ClientMethod(code = ServiceCode.SignManager_getTeamSignData)
    public void showView() {
        long teamId = getTeamId();
        sendMessage(getTeamSignData(teamId));
    }

    /**
     * 球队的签到数据
     */
    public SignPB.SignMainData getTeamSignData(long teamId) {
        // 开服时间到现在的月份数
        DateTime now = DateTime.now();
        DateTime startTime = new DateTime(GameSource.openTime);
        // 从1 开始
        int monthPeriod = DateTimeUtil.getMonthsBetweenNum(startTime, now) + 1;
        int day = now.getDayOfMonth();
        //
        TeamSign signMonth = getSignMonth(teamId, monthPeriod);
        TeamSign signPeriod = getSignPeriod(teamId);
        checkResetPeriod(signPeriod);
        // 补签次数计算，今天天数-昨天签到次数
        boolean monthLastSign = DateTimeUtil.getDaysBetweenNum(signMonth.getLastSignTime(), now, 0) == 0; // 今天是否已签
        int yestodaySignNum = monthLastSign ? signMonth.getSignNum() - 1 : signMonth.getSignNum();
        yestodaySignNum += signMonth.getPatchNum(); // 补签也算在已签里面
        int patchNum = monthPeriod > 1 ? day - yestodaySignNum - 1 : day - yestodaySignNum - startTime.getDayOfMonth();
        //
        return SignPB.SignMainData.newBuilder()
                .setMonthStartDay(monthPeriod - 1 > 0 ? 1 : startTime.getDayOfMonth())
                .setMonthPeriod(monthPeriod)
                // 包括补签次数
                .setMonthSignNum(signMonth.getSignNum() + signMonth.getPatchNum())
                .setMonthPatchNum(patchNum)
                .setMonthLastSign(monthLastSign) //是否已签
                .setServenPeriod(signPeriod.getPeriod() + 1)
                .setServenSignNum(signPeriod.getSignNum())
                .addAllServenStatus(signPeriod.getAwardStatus().getList()) // 今天是否可领，有可领就显示可领
                .setVipSign(vipManager.getEveryDayGiftStatus(teamId) == 1) // 1是已领， 其他是未领
                .build();
    }

    /**
     * 月签到
     */
    @ClientMethod(code = ServiceCode.SignManager_signMonth)
    public void signMonth() {
        long teamId = getTeamId();
        // 开服时间到现在的月份数
        DateTime now = DateTime.now();
        DateTime startTime = new DateTime(GameSource.openTime);
        // 从1 开始
        int monthPeriod = DateTimeUtil.getMonthsBetweenNum(startTime, now) + 1;
        //int day = now.getDayOfMonth();
        //
        TeamSign signMonth = getSignMonth(teamId, monthPeriod);
        if (DateTimeUtil.getDaysBetweenNum(signMonth.getLastSignTime(), now, 0) == 0) {
            //log.debug("今天已经签到！");
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Sign_1.code).build());
            return;
        }
        // 奖励等于开服时间（日数）+ 签到次数 = 本月的某个奖励
        signMonth.addSignNum();
        signMonth.addTotalSign();
        signMonth.setLastSignTime(now);
        signMonth.save();
        // 计算7天的累计签到
        addServenSign(teamId);
        // 签到奖励
        int awardIndex = signMonth.getPatchNum() + signMonth.getSignNum();
        awardIndex = monthPeriod > 1 ? awardIndex : awardIndex + startTime.getDayOfMonth() - 1;
        //log.debug("签到第{}天奖励", awardIndex);
        SignMonthBean bean = SignConsole.getSignMonthBean(monthPeriod, awardIndex);
        if (bean != null) {
            // 奖励
            List<PropSimple> awardList = bean.getAwardList();
            // VIP翻倍
            if (bean.getMultiple() > 0 && vipManager.getVip(teamId).getLevel() >= bean.getVip()) {
                awardList = PropSimple.getPropListMult(awardList, bean.getMultiple());
            }
            //log.debug("签到奖励：{},{}; 奖励：{}", bean.getMonth(), bean.getDay(), awardList);
            propManager.addPropList(teamId, awardList, true, ModuleLog.getModuleLog(EModuleCode.签到, "月签到"));
        }
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 月补签
     */
    @ClientMethod(code = ServiceCode.SignManager_signMonthPatch)
    public void signMonthPatch() {
        long teamId = getTeamId();
        // 开服时间到现在的月份数
        DateTime now = DateTime.now();
        DateTime startTime = new DateTime(GameSource.openTime);
        // 从1 开始
        int monthPeriod = DateTimeUtil.getMonthsBetweenNum(startTime, now) + 1;
        int day = now.getDayOfMonth();
        //
        TeamSign signMonth = getSignMonth(teamId, monthPeriod);
        // 补签次数（漏签次数）计算： 本月到昨天为止的可签次数 - 本月已签次数 - ( 本月开始签的天数)
        boolean monthLastSign = DateTimeUtil.getDaysBetweenNum(signMonth.getLastSignTime(), now, 0) == 0; // 今天是否已签
        int yestodaySignNum = monthLastSign ? signMonth.getSignNum() - 1 : signMonth.getSignNum();
        yestodaySignNum += signMonth.getPatchNum(); // 补签也算在已签里面
        int patchNum = monthPeriod > 1 ? day - yestodaySignNum - 1 : day - yestodaySignNum - startTime.getDayOfMonth();
        if (patchNum <= 0) {
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Sign_3.code).build());
            return;
        }
        // 扣券数
        if (!moneyManager.checkTeamMoney(moneyManager.getTeamMoney(teamId), SignConsole.Sign_Patch_Fk, 0, 0, 0)) {
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Money_1.code).build());
            return;
        }
        moneyManager.updateTeamMoney(teamId, -SignConsole.Sign_Patch_Fk, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.签到, "补签"));
        //
        signMonth.addPatchNum();
        signMonth.addTotalPatch();
        signMonth.save();
        // 奖励等于开服时间（日数）+ 签到次数 = 本月的某个奖励
        int awardIndex = signMonth.getPatchNum() + signMonth.getSignNum();
        awardIndex = monthPeriod > 1 ? awardIndex : awardIndex + startTime.getDayOfMonth() - 1;
        //log.debug("签到第{}天奖励", awardIndex);
        SignMonthBean bean = SignConsole.getSignMonthBean(monthPeriod, awardIndex);
        if (bean != null) {
            // 奖励
            List<PropSimple> awardList = bean.getAwardList();
            // VIP翻倍
            if (bean.getMultiple() > 0 && vipManager.getVip(teamId).getLevel() >= bean.getVip()) {
                awardList = PropSimple.getPropListMult(awardList, bean.getMultiple());
            }
            //log.debug("签到奖励：{},{}; 奖励：{}", bean.getMonth(), bean.getDay(), awardList);
            propManager.addPropList(teamId, awardList, true, ModuleLog.getModuleLog(EModuleCode.签到, "月补签"));
        }
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 7天累计的签到，补签不算
     */
    private void addServenSign(long teamId) {
        TeamSign signPeriod = getSignPeriod(teamId);
        // 本周期的奖励没有全部领完，就不算签到
        if (signPeriod.getSignNum() == SignConsole.getSignPeriodSize(signPeriod.getPeriod())) {
            return;
        }
        signPeriod.addSignNum();
        signPeriod.addTotalSign(); // 记录总签到次数
        signPeriod.setLastSignTime(DateTime.now());
        signPeriod.save();
    }

    /**
     * 只是领奖，传指定天数0~6
     * 七天累计签到
     */
    @ClientMethod(code = ServiceCode.SignManager_signPeriod)
    public void signPeroid(int index) {
        long teamId = getTeamId();
        TeamSign signPeriod = getSignPeriod(teamId);
        //
        if (index < 0 || index >= 7) {
            log.debug("参数错误：idnex{}", index);
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_5.code).build());
            return;
        }
        if (signPeriod.getSignNum() <= index) {
            log.debug("领取签到奖励错误：{},{}，签到次数不足!", signPeriod.getSignNum(), index);
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_5.code).build());
            return;
        }
        // 奖励已领取
        if (signPeriod.getAwardStatus().getValue(index) != 0 || signPeriod.getPatchNum() > signPeriod.getSignNum()) {
            log.debug("已领取过奖励：{}", signPeriod.getPeriod());
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
            return;
        }
        //
        SignPeriodBean bean = SignConsole.getSignPeriodBean(signPeriod.getPeriod(), index + 1);
        if (bean == null) {
            log.debug("签到奖励错误：{}", signPeriod.getPeriod());
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Besign_2.code).build());
            return;
        }
        // 这里做领奖次数用/统计
        signPeriod.getAwardStatus().setValue(index, 1);
        signPeriod.addPatchNum();
        signPeriod.setLastSignTime(DateTime.now());
        // 如果已经全部领完，就进入下一个周期
        //		if(signPeriod.getPatchNum() == SignConsole.getSignPeriodSize(signPeriod.getPeriod())) {
        //			signPeriod.clearSignNum();
        //			signPeriod.setPeriod((signPeriod.getPeriod()+1) % SignConsole.PeriodSize);
        //			signPeriod.setStatus(DateTimeUtil.getTodayEndTime().getMillis() + "");
        //		}
        signPeriod.save();
        // 奖励
        propManager.addPropList(teamId, bean.getAwardList(), true, ModuleLog.getModuleLog(EModuleCode.签到, "7天签到"));
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 检查7天签到是否要显示新的周期
     *
     * @param signPeriod
     */
    private void checkResetPeriod(TeamSign signPeriod) {
        if (signPeriod == null) {
            return;
        }
        if ("".equals(signPeriod.getStatus()) || signPeriod.getStatus() == null) {
            return;
        }
        if (signPeriod.getPatchNum() != SignConsole.getSignPeriodSize(signPeriod.getPeriod())) {
            return;
        }
        if (DateTime.now().getDayOfYear() <= signPeriod.getLastSignTime().getDayOfYear()) {
            return;
        }
        //
        signPeriod.clearSignNum();
        signPeriod.setPeriod((signPeriod.getPeriod() + 1) % SignConsole.PeriodSize);
        signPeriod.save();
    }

    public static void main(String[] args) {
        System.err.println(4 % 3);
    }
}
