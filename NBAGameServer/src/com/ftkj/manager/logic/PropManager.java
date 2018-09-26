package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.ItemConvertBean;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.console.DropConsole;
import com.ftkj.console.GameConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.db.ao.logic.IPropAO;
import com.ftkj.db.domain.PropPO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPropType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.manager.logic.log.GamePropLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.Prop;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.prop.bean.PropAutoBoxBean;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropBoxBean;
import com.ftkj.manager.prop.bean.PropCoachBean;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.prop.bean.PropMoneyBean;
import com.ftkj.manager.prop.bean.PropPlayerBean;
import com.ftkj.manager.prop.bean.PropPlayerGradeBean;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.vip.TeamVip;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PropPB;
import com.ftkj.proto.PropPB.OpenBox;
import com.ftkj.proto.PropPB.PropData;
import com.ftkj.proto.PropPB.PropSimpleData;
import com.ftkj.proto.PropPB.TeamPropsData;
import com.ftkj.proto.ShopPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2016年2月24日
 * 物品管理
 */
public class PropManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(PropManager.class);
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private IPropAO propAO;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private BeSignManager beSignManager;
    @IOC
    private CoachManager coachManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private LocalCustomPVPManager localCustomPVPManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private BuffManager buffManager;

    private Map<Long, TeamProp> teamPropMap;

    public TeamProp getTeamProp(long teamId) {
        TeamProp teamProp = teamPropMap.get(teamId);
        if (teamProp == null) {
            List<PropPO> tp = propAO.getPropList(teamId);
            if (tp != null) {
                teamProp = new TeamProp(tp);
                teamPropMap.put(teamId, teamProp);
            }
        }
        return teamProp;
    }

    /**
     * 添加道具列表
     */
    public List<PropPO> addPropList(long teamId, List<PropSimple> list, boolean send, boolean tip, List<PropPO> poList, ModuleLog module) {
        List<PropPO> sendList = Lists.newArrayList();
        if (poList == null) {
            poList = Lists.newArrayList();
        }

        for (PropSimple ps : list) {
            if (!opend(teamId, ps, poList, module)) {//没有递归打开礼包或者直接添加，走正常添加道具。
                sendList.add(addProp(teamId, ps, ps.getHour() != 0 ? DateTime.now().plusHours(ps.getHour()) : GameConsole.Max_Date, module));
                poList.add(new PropPO(ps.getPropId(), ps.getNum()));
            }
        }
        //
        if (send) {
            sendMessage(teamId, TeamPropsData.newBuilder().addAllPropList(getPropListData(sendList)).build(), ServiceCode.Prop_Change);
            TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
            sendMessage(tm.getTeamId(), teamMoneyManager.getTeamMoneyData(tm), ServiceCode.Push_Money);
        }
        return poList;
    }

    /**
     * 添加道具，默认推送道具变动
     */
    public List<PropPO> addPropList(long teamId, List<PropSimple> list, boolean tip, ModuleLog module) {
        if (GameSource.isNPC(teamId) || list == null) {
            return null;
        }
        return addPropList(teamId, list, true, tip, null, module);
    }

    public List<PropSimple> addPropList(long teamId, List<PropSimple> list, List<Integer> dropIds, boolean tip, ModuleLog module) {
        if (dropIds == null || dropIds.isEmpty()) {
            addPropList(teamId, list, tip, module);
            return list;
        }
        List<PropSimple> dropProps = getPropSimples(list, dropIds);
        List<PropPO> ret = addPropList(teamId, dropProps, tip, module);
        if (log.isDebugEnabled()) {
            log.debug("props add. tid {} m {} list {} drops {} all {} ret {}", teamId, module, list, dropIds, dropProps, ret);
        }
        return dropProps;
    }

    //TODO bug 道具需要递归展开?
    List<PropSimple> getPropSimples(List<PropSimple> props, List<Integer> dropIds) {
        if (dropIds == null || dropIds.isEmpty()) {
            return new ArrayList<>(props);
        }
        Map<Integer, PropSimple> ret = new HashMap<>();
        PropSimple.mergeProps(props, ret);

        for (Integer dropId : dropIds) {
            DropBean db = DropConsole.getDrop(dropId);
            if (db == null) {
                continue;
            }
            List<PropSimple> rollProps = db.roll();
            if (rollProps != null) {
                PropSimple.mergeProps(rollProps, ret);
            }
        }
        return new ArrayList<>(ret.values());
    }

    public void addProp(long teamId, PropSimple ps, boolean tip, ModuleLog module) {
        PropPO po = addProp(teamId, ps, ps.getHour() != 0 ? DateTime.now().plusHours(ps.getHour()) : GameConsole.Max_Date, module);
        sendMessage(teamId, TeamPropsData.newBuilder().setTip(tip).addAllPropList(getPropListData(Collections.singletonList(po))).build(), ServiceCode.Prop_Change);
    }

    private PropPO addProp(long teamId, PropSimple ps, DateTime endTime, ModuleLog module) {
        TeamProp tp = getTeamProp(teamId);
        PropPO po = tp.addProp(teamId, ps, endTime);
        GamePropLogManager.Log(teamId, po.getPropId(), ps.getNum(), po.getNum(), module);
        return po;
    }

    /**
     * 扣除道具或者货币
     */
    List<PropPO> usePropOrMoney(long teamId, List<PropSimple> list, boolean send, ModuleLog module) {
        List<PropPO> resultList = Lists.newArrayList();
        for (PropSimple ps : list) {
            PropBean prop = PropConsole.getProp(ps.getPropId());
            if (prop != null && prop.getType() == EPropType.Money) {
                PropMoneyBean pm = (PropMoneyBean) prop;
                moneyManager.updateTeamMoney(teamId, 0 - pm.getMoney() * ps.getNum(), 0 - pm.getGold() * ps.getNum(), 0, 0 - pm.getBdmoney() * ps.getNum(), true, module);
                continue;
            }
            // 道具正常扣除
            resultList.addAll(delProp(teamId, ps.getPropId(), ps.getNum(), false, false));
        }
        if (send) {
            sendMessage(teamId, PropPB.TeamPropsData.newBuilder()
                .addAllPropList(getPropListData(resultList))
                .setTip(false)
                .build(), ServiceCode.Prop_Change);
        }
        return resultList;
    }

    /**
     * 删除单个道具
     */
    public List<PropPO> delProp(long teamId, int pid, int num, boolean send, boolean tip) {
        PropBean pb = PropConsole.getProp(pid);
        return delPropByTid(teamId, pb.getTid(), num, send, tip);
    }

    /**
     * 删除单个道具
     */
    public List<PropPO> delPropByTid(long teamId, int tid, int num, boolean send, boolean tip) {
        TeamProp tp = getTeamProp(teamId);
        List<PropPO> dataList = tp.delProp(tid, num);
        if (dataList == null || dataList.size() < 1) {
            return dataList;
        }
        GamePropLogManager.Log(teamId, dataList.get(0).getPropId(), -num, dataList.get(0).getNum(), ModuleLog.getModuleLog(EModuleCode.道具, "del1"));
        if (send) {
            sendMessage(teamId, PropPB.TeamPropsData.newBuilder()
                .addAllPropList(getPropListData(dataList))
                .setTip(tip)
                .build(), ServiceCode.Prop_Change);
        }
        return dataList;
    }

    public List<PropPO> delProp(long teamId, PropSimple ps, boolean send, boolean tip) {
        return delProp(teamId, ps.getPropId(), ps.getNum(), send, tip);
    }

    /**
     * 批量删除，不要有重复道具
     */
    public List<PropPO> delPropList(long teamId, List<PropSimple> pList, boolean send, boolean tip) {
        if (GameSource.isNPC(teamId)) { return null; }
        TeamProp tp = getTeamProp(teamId);
        List<PropPO> dataList = Lists.newArrayList();
        for (PropSimple ps : pList) {
            PropBean pb = PropConsole.getProp(ps.getPropId());
            List<PropPO> tmpList = tp.delProp(pb.getTid(), ps.getNum());
            if (tmpList != null) {
                dataList.addAll(tmpList);
            } else {
                log.info("跨服玩家道具不足依然调用异常{},{}:{}", teamId, pb.getPropId(), ps.getNum());
            }
        }
        if (send) {
            sendMessage(teamId, PropPB.TeamPropsData.newBuilder()
                .addAllPropList(getPropListData(dataList))
                .setTip(tip)
                .build(), ServiceCode.Prop_Change);
        }
        return dataList;
    }

    /**
     * 移除道具，并推送包
     */
    public List<PropPO> delProp(long teamId, List<PropSimple> propList, boolean send, boolean tip) {
        List<PropPO> resultProp = new ArrayList<PropPO>();
        TeamProp tp = getTeamProp(teamId);
        boolean d = del(teamId, tp, propList, 0, propList.size(), resultProp);
        if (d && send) {
            sendMessage(teamId, PropPB.TeamPropsData.newBuilder()
                .addAllPropList(getPropListData(resultProp))
                .setTip(tip)
                .build(), ServiceCode.Prop_Change);
        }
        return resultProp;
    }

    /**
     * 递归判断道具集数量是否满足并且进行数量扣除操作
     */
    private boolean del(long teamId, TeamProp tp, List<PropSimple> propList, int start, int end, List<PropPO> resultProp) {
        if (start >= end) {
            return true;//判断是否已经将所有道具都判断完毕
        }
        Prop po;
        PropSimple ps = propList.get(start);
        po = tp.getPropByPropId(ps.getPropId());
        boolean allCheck = true;
        if (po == null || po.getNum() < ps.getNum()) {
            return false;//某个道具数量不足，无法扣除。跳出递归,结束.
        }
        allCheck = del(teamId, tp, propList, ++start, end, resultProp);//递归
        if (!allCheck) {
            return false;//递归完毕,进行判断,其中一环为false,不进行扣除操作
        }
        //数量足够,物品存在.进行扣除
        List<PropPO> dd = tp.delProp(po.getTid(), ps.getNum());

        //		dd.stream().forEach(p->);
        if (dd != null) {
            resultProp.addAll(dd);
            PropPO p = dd.get(0);
            GamePropLogManager.Log(teamId, p.getPropId(), p.getNum(), p.getNum() + ps.getNum(), ModuleLog.getModuleLog(EModuleCode.道具, "dels"));
        }
        return true;
    }

    /**
     * 出售道具
     *
     * @param tid
     * @param num
     */
    @ClientMethod(code = ServiceCode.Prop_Sale)
    public void saleProp(int tid, int num) {
        long teamId = getTeamId();
        //		TeamProp teamProp = getTeamProp(teamId);
        //		if(!teamProp.checkPropNum(tid, num)) {
        //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_0.code).build());
        //			return;
        //		}
        PropBean pb = PropConsole.getProp(tid);

        // 扣道具
        List<PropPO> resultList = delPropByTid(teamId, tid, num, true, true);
        if (resultList == null) {
            return;
        }
        if (pb.getSale() > 0) {
            moneyManager.updateTeamMoney(teamId, 0, pb.getSale() * num, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.道具, "sell"));
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
        // 道具变更
        //		sendMessage(teamId, PropPB.TeamPropsData.newBuilder().addAllPropList(getPropListData(resultList)).build(), ServiceCode.Prop_Change);
    }

    /**
     * 兑换界面
     *
     * @param viewType
     */
    @ClientMethod(code = ServiceCode.PropManager_showConvertMain)
    public void showConvertMain(int viewType) {
        long teamId = getTeamId();
        if (viewType < 0) {
            return;
        }
        List<ItemConvertBean> beanList = PropConsole.getConverViewItems(viewType);
        if (beanList == null || beanList.size() == 0) {
            return;
        }
        Map<Integer, Integer> convertMap = redis.getMapAllKeyValues(RedisKey.getDayKey(teamId, RedisKey.ItemConvert_Limit + viewType));
        Map<Integer, Integer> totalConvertMap = redis.getMapAllKeyValues(RedisKey.getKey(teamId, RedisKey.ItemConvert_Limit + viewType));
        // 只有是活动的时候，才显示拥有数据和已对限量
        List<ShopPB.ShopPropData> currPropList = getActiveConvertData(teamId, beanList);
        sendMessage(ShopPB.ConvertMainData.newBuilder()
            .addAllLimit(getConvertLimitDataList(convertMap))
            .addAllTotalLimit(getConvertLimitDataList(totalConvertMap))
            .addAllCurrProp(currPropList)
            .build());
    }

    /**
     * 查询单项兑换
     *
     * @param cid
     */
    @ClientMethod(code = ServiceCode.PropManager_showConvertItem)
    public void PropManager_showConvertItem(int cid) {
        ItemConvertBean item = PropConsole.getConvertBean(cid);
        if (item == null) {
            return;
        }
        long teamId = getTeamId();
        Map<Integer, Integer> convertMap = redis.getMapAllKeyValues(RedisKey.getDayKey(teamId, RedisKey.ItemConvert_Limit + item.getView()));
        Map<Integer, Integer> totalConvertMap = redis.getMapAllKeyValues(RedisKey.getKey(teamId, RedisKey.ItemConvert_Limit + item.getView()));
        List<ShopPB.ShopPropData> currPropList = getActiveConvertData(teamId, Lists.newArrayList(item));
        Map<Integer, Integer> itemMap = Maps.newHashMap();
        itemMap.put(cid, convertMap.get(cid));
        //
        Map<Integer, Integer> itemTotalMap = Maps.newHashMap();
        itemTotalMap.put(cid, totalConvertMap.get(cid));
        //
        sendMessage(ShopPB.ConvertMainData.newBuilder()
            .addAllLimit(getConvertLimitDataList(itemMap))
            .addAllTotalLimit(getConvertLimitDataList(itemTotalMap))
            .addAllCurrProp(currPropList)
            .build());
    }

    private List<ShopPB.ShopPropData> getActiveConvertData(long teamId, List<ItemConvertBean> beanList) {
        List<ShopPB.ShopPropData> currPropList = Lists.newArrayList();
        if (beanList.get(0).getActiveType() != -1) {
            ActiveBase convertData = ActiveBaseManager.getManager(beanList.get(0).getActiveType()).getTeamData(teamId);
            List<Integer> ids = Lists.newArrayList();
            beanList.stream().forEach(s -> ids.addAll(s.getNeedOther().stream().mapToInt(i -> i.getPropId()).boxed().collect(Collectors.toList())));
            List<Integer> disList = ids.stream().distinct().collect(Collectors.toList());
            disList.stream().forEach(s -> {
                currPropList.add(ShopPB.ShopPropData.newBuilder().setPropId(s).setNum(convertData.getPropNum().getValue(s - 1)).build());
            });
        }
        return currPropList;
    }

    private List<ShopPB.ConvertLimitData> getConvertLimitDataList(Map<Integer, Integer> convertMap) {
        List<ShopPB.ConvertLimitData> dataList = Lists.newArrayList();
        if (convertMap != null && convertMap.size() > 0) {
            // 这里的ID 是 Cid，不是Pid
            convertMap.forEach((k, v) -> dataList.add(ShopPB.ConvertLimitData.newBuilder()
                .setCid(k)
                .setNum(v == null ? 0 : v)
                .build()));
        }
        return dataList;
    }

    /**
     * 兑换道具接口
     * 道具合成通用接口
     *
     * @param cid
     * @param num
     */
    @ClientMethod(code = ServiceCode.PropManager_convertProp)
    public void convertProp(int cid, int num) {
        long teamId = getTeamId();
        ItemConvertBean bean = PropConsole.getConvertBean(cid);
        if (num < 1 || bean == null) {
            log.debug("参数异常{}", cid);
            sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        // 兑换结束时间
        DateTime endTime = GameConsole.Min_Date;
        if (bean.getActiveType() != -1) {
            SystemActiveBean activeBean = SystemActiveConsole.getSystemActive(bean.getActiveType());
            endTime = activeBean != null ? activeBean.getEndDateTime() : endTime;
        } else {
            endTime = bean.getEndTime();
        }
        if (endTime.isBeforeNow()) {
            log.debug("已截止兑换时间{}", cid);
            sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Active_4.code).build());
            return;
        }
        if (teamManager.getTeam(teamId).getLevel() < bean.getTeamLevel()) {
            log.debug("兑换等级不够", cid);
            sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
            return;
        }
        // 兑换每天限量
        Integer todayLimit = null;
        String dayKey = RedisKey.getDayKey(teamId, RedisKey.ItemConvert_Limit + bean.getView());
        if (bean.getLimit() > 0) {
            todayLimit = redis.hget(dayKey, cid);
            int limitMax = bean.getLimit() + bean.getVipLimit(vipManager.getVip(teamId).getLevel());
            if (todayLimit != null && todayLimit + num > limitMax) {
                //超过购买上限
                log.debug("超过每日兑换上限{}", limitMax);
                sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Prop_4.code).build());
                return;
            }
        }
        // 总兑换每天限量
        Integer totalLimit = 0;
        String viewKey = RedisKey.getKey(teamId, RedisKey.ItemConvert_Limit + bean.getView());
        if (bean.getTotalLimit() > 0) {
            totalLimit = redis.hget(viewKey, cid);
            if (totalLimit != null && totalLimit + num > bean.getTotalLimit()) {
                //超过购买上限
                log.debug("超过总兑换上限{}", bean.getTotalLimit());
                sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Prop_4.code).build());
                return;
            }
        }
        TeamProp teamProp = getTeamProp(teamId);
        for (PropSimple np : bean.getNeedProp()) {
            if (PropConsole.isMoney(np.getPropId())) {
                // 擂台货币数量检查
                PropMoneyBean pm = PropConsole.getProp(np.getPropId());
                if (pm == null || pm.getCustom() < 0 || localCustomPVPManager.getCustomTeam(teamId).getMoney() < np.getNum() * num) {
                    sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
                    return;
                }
            } else if (!teamProp.checkPropNum(np.getPropId(), np.getNum() * num)) {
                sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
                return;
            }
        }
        // 活动数据判断
        for (PropSimple np : bean.getNeedOther()) {
            ActiveBase atvObj = ActiveBaseManager.getManager(bean.getActiveType()).getTeamData(teamId);
            if (atvObj.getPropNum().getValue(np.getPropId() - 1) < np.getNum()) {
                log.debug("道具数量不足{}", cid);
                sendMessage(ShopPB.ConvertResultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
                return;
            }
        }
        // 统计限量
        if (bean.getLimit() > 0) {
            int count = todayLimit == null ? num : todayLimit + num;
            redis.putMapValueExp(dayKey, cid, count);
            redis.expire(dayKey, RedisKey.DAY2);
        }
        if (bean.getTotalLimit() > 0) {
            int count = totalLimit == null ? num : totalLimit + num;
            redis.putMapValue(viewKey, cid, count);
        }
        //
        List<PropSimple> awardList = tranProp(teamId, bean, num);
        sendMessage(ShopPB.ConvertResultData.newBuilder()
            .setCid(cid)
            .setNum(num)
            .setCode(ErrorCode.Success.code)
            .addAllAwardList(getPropSimpleListData(awardList))
            .addAllCurrProp(getActiveConvertData(teamId, PropConsole.getConverViewItems(bean.getView())))
            .build());
    }

    /**
     * 此接口不做验证，自己验证了再调用兑换
     * 道具兑换通用接口
     *
     * @param teamId
     * @param tranNum 兑换次数
     * @return
     */
    private List<PropSimple> tranProp(long teamId, ItemConvertBean convertBean, int tranNum) {
        // 道具和货币
        for (PropSimple np : convertBean.getNeedProp()) {
            PropBean chipBean = PropConsole.getProp(np.getPropId());
            if (chipBean.getType() == EPropType.Money) {
                // 使用效果,请在外面扣掉
                use(teamId, chipBean, 0 - np.getNum() * tranNum, true, true, Lists.newArrayList(), ModuleLog.getModuleLog(EModuleCode.兑换, ""));
            } else {
                delProp(teamId, new PropSimple(np.getPropId(), np.getNum() * tranNum), true, false);
            }
        }
        // 活动数据
        for (PropSimple np : convertBean.getNeedOther()) {
            ActiveBase atvObj = ActiveBaseManager.getManager(convertBean.getActiveType()).getTeamData(teamId);
            atvObj.getPropNum().setValueAdd(np.getPropId() - 1, 0 - np.getNum() * tranNum);
        }
        // 兑换奖励
        List<PropSimple> awardList = PropSimple.getPropListMult(convertBean.getTargetProp(), tranNum);
        addPropList(teamId, awardList, true, ModuleLog.getModuleLog(EModuleCode.兑换, ""));
        return awardList;
    }

    /**
     * 使用道具
     *
     * @param tid 道具模板ID
     * @param num
     */
    @ClientMethod(code = ServiceCode.Prop_Use)
    public void useProp(int tid, int num) {
        long teamId = getTeamId();
        useProp(teamId, tid, num);
    }

    /*
     * 使用道具
     */
    public void useProp(long teamId, int tid, int num) {
        // 验证道具数量，是否可以使用即可
        //		TeamProp teamProp = getTeamProp(teamId);
        //		if(!teamProp.checkPropNum(tid, num)) {
        //			return;
        //		}
        //		if(!teamProp.checkCanUse(tid)) {
        //			return;
        //		}
        PropBean pb = PropConsole.getProp(tid);
        if (pb.getType() == EPropType.Common) {//物品不可使用
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        int count = 0;
        if (pb.getDayLimit() > 0) {//判断是否每日限制道具
            count = getDayLimitCount(teamId, pb.getPropId()) + num;
            if (count >= pb.getDayLimit()) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_1.code).build());
                return;
            }
        }
        // 扣道具
        List<PropPO> rs = delPropByTid(teamId, tid, num, true, true);
        if (rs == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        if (count > 0) {
            setDayLimitCount(teamId, pb.getPropId(), count);
        }
        // 使用效果
        List<PropPO> result = use(teamId, pb, num, true, true, Lists.newArrayList(), ModuleLog.getModuleLog(EModuleCode.道具, "use"));
        if(pb.getType() != EPropType.PlayerGrade && pb.getType() != EPropType.PlayerBasePrice && pb.getType() != EPropType.Package) {
          String msg = result.stream().map(p -> p.getPropId() + "=" + p.getNum()).collect(Collectors.joining(","));
          sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(msg).build());
          //		sendMessage(teamId, PropPB.TeamPropsData.newBuilder().addAllPropList(getPropListData(result)).build(), ServiceCode.Prop_Change);
        }
    }

    /**
     * 每天使用限量
     */
    private int getDayLimitCount(long teamId, int propId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Prop_Day) + "_" + propId;
        String count = redis.getStr(key);
        return count == null ? 0 : Integer.parseInt(count);
    }

    private void setDayLimitCount(long teamId, int propId, int count) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Prop_Day) + "_" + propId;
        redis.set(key, "" + count, RedisKey.DAY);
    }

    /**
     * 使用物品
     *
     * @return 获得的 物品ID=数量,物品ID=数量 字符串格式
     */
    public List<PropPO> use(long teamId, PropBean prop, int num, boolean send, boolean tip, List<PropPO> poList, ModuleLog module) {
        switch (prop.getType()) {
            case Common: {
                poList.add(new PropPO(prop.getPropId(), num));
                break;
            }
            case Money: {
                PropMoneyBean pm = (PropMoneyBean) prop;
                if (pm.getCustom() > 0) {
                    localCustomPVPManager.updateCustomMoney(teamId, pm.getCustom() * num);
                    poList.add(new PropPO(4011, pm.getCustom() * num));
                }
                if (pm.getFeats() > 0) {
                    leagueManager.updateLeagueTeamFeats(teamId, pm.getFeats() * num);
                    poList.add(new PropPO(4012, num));
                }

                moneyManager.updateTeamMoney(teamId, pm.getMoney() * num, pm.getGold() * num, pm.getExp() * num, pm.getBdmoney() * num, tip, module);
                if (pm.getMoney() > 0) { poList.add(new PropPO(4004, pm.getMoney() * num)); }
                if (pm.getGold() > 0) { poList.add(new PropPO(4001, pm.getGold() * num)); }
                if (pm.getBdmoney() > 0) { poList.add(new PropPO(4003, pm.getBdmoney() * num)); }
                if (pm.getExp() > 0) { poList.add(new PropPO(4002, pm.getExp() * num)); }
                return poList;
            }
            case Package: {
                PropBoxBean pb = (PropBoxBean) prop;
                List<PropSimple> list = Lists.newArrayList();
                for (int i = 0; i < num; i++) {
                    list.addAll(pb.getDrop().roll());
                }
                //				String result = list.stream().map(ps->ps.getPropId()+StringUtil.DEFAULT_FH+ps.getNum()).collect(Collectors.joining(","));
                return addPropList(teamId, list, send, tip, poList, module);
            }
            case Package_Random: {
                PropAutoBoxBean pb = (PropAutoBoxBean) prop;
                List<PropSimple> list = Lists.newArrayList();
                for (int i = 0; i < num; i++) {
                    list.addAll(pb.getDrop().roll());
                }
                //				String result = list.stream().map(ps->ps.getPropId()+StringUtil.DEFAULT_FH+ps.getNum()).collect(Collectors.joining(","));
                return addPropList(teamId, list, send, tip, poList, module);
            }
            case PlayerGrade: {//球星卡
                PropPlayerGradeBean pb = (PropPlayerGradeBean) prop;
                int playerId = PlayerConsole.getRanPlayer(pb.getMinGrade(), pb.getMaxGrade(), EPlayerPosition.NULL, null).getPlayerRid();
                //添加球员到仓库
                //				PlayerBean player = PlayerConsole.getPlayerBean(playerId);
                TeamPlayer tp = playerManager.getTeamPlayer(teamId);
                PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, playerId, tp.getNewTalentId(), PlayerManager._initDrop, true);
                tp.putPlayerTalent(talent);
                beSignManager.addBeSignPlayer(teamId, playerId, num, talent.getId(), module);
                OpenBox.Builder builder = OpenBox.newBuilder();
                builder.addPlayerId(playerId);
                sendMessage(teamId, builder.build(), ServiceCode.open_box_get_player);
                return ImmutableList.of();
            }
            case Coach: {//教练
                PropCoachBean pb = (PropCoachBean) prop;
                coachManager.addCoach(teamId, pb.getCoachId());
                return ImmutableList.of();
            }
            case PK: return ImmutableList.of();
            case Player:
            case Wrap_Player: { //直接发球员 包装球员
                PropExtPlayerBean prb = PropConsole.getPlayerProp(prop);
                if (prb == null) {
                    return ImmutableList.of();
                }
                OpenBox.Builder builder = OpenBox.newBuilder();
                builder.addPlayerId(prb.getHeroId());
                sendMessage(teamId, builder.build(), ServiceCode.open_box_get_player);
                return addPlayer(teamId, prb, num, prb.isBind(), module);
            }
            case VipExp: {//VIP经验
                TeamVip vip = vipManager.getVip(teamId);
                vip.addExp(num);
                vip.save();
                vipManager.checkVipUplevel(vip);
                break;
            }
            case Buff: { // Buff类型道具
                String[] buffCfgs = prop.getConfig().split(",");
                Map<String, String> cfgMap = Maps.newHashMap();
                for (String cfgs : buffCfgs) {
                    String[] cfg = cfgs.split(":");
                    cfgMap.put(cfg[0], cfg[1]);
                }
                int days = Integer.valueOf(cfgMap.get("days")) * num;
                int buffid = Integer.valueOf(cfgMap.get("id"));
                int[] vals = Arrays.stream(cfgMap.get("val").split("_")).mapToInt(i -> new Integer(i)).toArray();
                EBuffKey key = EBuffKey.valueOfKey(buffid / 100 * 100);
                EBuffType type = EBuffType.valueOfKey(buffid % 100);
                TeamBuff teamBuff = new TeamBuff(key, type, vals, DateTime.now().plusDays(days), false);
                buffManager.addBuff(teamId, teamBuff);
                break;
            }
            case PlayerBasePrice:{
              PropPlayerGradeBean pb = (PropPlayerGradeBean) prop;
              int playerId = PlayerConsole.getRanPlayer(pb.getMinGrade(), pb.getMaxGrade(), EPlayerPosition.NULL, null).getPlayerRid();
              //添加球员到仓库
              //        PlayerBean player = PlayerConsole.getPlayerBean(playerId);
              TeamPlayer tp = playerManager.getTeamPlayer(teamId);
              PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, playerId, tp.getNewTalentId(), PlayerManager._initDrop, true);
              tp.putPlayerTalent(talent);
              num = playerManager.getPlayerMinPrice(playerId);
              beSignManager.addBeSignPlayer(teamId, playerId, num, talent.getId(), module);
              OpenBox.Builder builder = OpenBox.newBuilder();
              builder.addPlayerId(playerId);
              sendMessage(teamId, builder.build(), ServiceCode.open_box_get_player);
              return ImmutableList.of();
            }
        }
        return ImmutableList.of();
    }

    private List<PropPO> addPlayer(long teamId, PropPlayerBean prop, int num, boolean bind, ModuleLog module) {
        if (prop == null) {
            return ImmutableList.of();
        }
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, prop.getHeroId(), tp.getNewTalentId(), PlayerManager._initDrop, true);
        tp.putPlayerTalent(talent);
        int price;
        if (num == 9999) {  // 取当前市价
            price = PlayerConsole.getPlayerBean(prop.getHeroId()).getPrice();
        } else if (num == 8888) { // 取本服低薪
            price = playerManager.getPlayerMinPrice(prop.getHeroId());
        } else {
            price = num;
        }
        if (price <= 0) {
            price = 2051;
        }
        beSignManager.addBeSignPlayer(teamId, prop.getHeroId(), price, talent.getId(), bind, module);
        return ImmutableList.of();
    }

    private boolean opend(long teamId, PropSimple prop, List<PropPO> result, ModuleLog module) {
        PropBean bean = PropConsole.getProp(prop.getPropId());
        if (bean == null) { return true; }

        if (bean.getType() == EPropType.Money
            || bean.getType() == EPropType.Player
            || bean.getType() == EPropType.Wrap_Player
            || bean.getType() == EPropType.Package_Random
            || bean.getType() == EPropType.PlayerGrade
            || bean.getType() == EPropType.Coach
            || bean.getType() == EPropType.PlayerBasePrice) {
            use(teamId, bean, prop.getNum(), true, true, result, module);
            return true;
        }
        return false;
    }

    @Override
    public void instanceAfter() {
        this.teamPropMap = Maps.newConcurrentMap();
    }

    /**
     * 转协议
     */
    public static List<PropPB.PropData> getPropListData(Collection<PropPO> list) {
        List<PropPB.PropData> pdatas = Lists.newArrayList();
        for (PropPO po : list) {
            pdatas.add(getPropData(po));
        }
        return pdatas;
    }

    /**
     * 转协议
     */
    public static PropPB.PropData getPropData(PropPO po) {
        return PropData.newBuilder()
            .setPropId(po.getPropId())
            .setNum(po.getNum())
            .setId(po.getId())
            .setEndTime(po.getEndTime().getMillisOfSecond())
            .build();
    }

    /**
     * 转协议
     */
    public static PropPB.PropSimpleData getPropData(PropSimple po) {
        return PropSimpleData.newBuilder()
            .setPropId(po.getPropId())
            .setNum(po.getNum())
            .build();
    }

    /**
     * 转协议
     */
    public static List<PropPB.PropSimpleData> getPropSimpleListData(Collection<PropSimple> pss) {
        List<PropPB.PropSimpleData> ret = Lists.newArrayList();
        for (PropSimple po : pss) {
            ret.add(getPropData(po));
        }
        return ret;
    }

    @Override
    public void offline(long teamId) {
        teamPropMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamPropMap.remove(teamId);
    }
}
