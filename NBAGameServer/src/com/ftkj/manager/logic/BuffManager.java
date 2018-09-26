package com.ftkj.manager.logic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.console.GameConsole;
import com.ftkj.db.ao.logic.IBuffAO;
import com.ftkj.db.domain.BuffPO;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.buff.Buff;
import com.ftkj.manager.buff.BuffSet;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.proto.BuffPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * buff管理
 *
 * @author Jay
 * @time:2017年7月28日 上午10:09:27
 */
public class BuffManager extends BaseManager implements OfflineOperation {

    @IOC
    private LeagueManager leagueManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private IBuffAO buffAO;
    //
    private Map<Long, Map<Integer, Buff>> teamBuffMap;

    @Override
    public void instanceAfter() {
        teamBuffMap = Maps.newConcurrentMap();
    }

    /**
     * 取teamBuff
     *
     * @param teamId
     * @return
     */
    public Map<Integer, Buff> getTeamBuffMap(long teamId) {
        Map<Integer, Buff> teamBuff = teamBuffMap.get(teamId);
        if (teamBuff == null) {
            List<BuffPO> poList = buffAO.getTeamBuffList(teamId);
            if (poList == null) {
                poList = Lists.newArrayList();
            }
            teamBuff = poList.stream().collect(Collectors.toConcurrentMap(BuffPO::getId, b -> new Buff(b)));
            teamBuffMap.put(teamId, teamBuff);
            GameSource.checkGcData(teamId);
        }
        return teamBuff;
    }

    @Override
    public void offline(long teamId) {
        teamBuffMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamBuffMap.remove(teamId);
    }

    /**
     * 取Buff
     *
     * @param teamId
     * @param type
     * @return
     */
    public List<TeamBuff> getBuff(long teamId, EBuffType type) {
        // getBuff 耗时测试
        //long startTime = System.currentTimeMillis();
        List<TeamBuff> buffList = Lists.newArrayList();
        Map<Integer, Buff> teamBuffMap = getTeamBuffMap(teamId);
        //
        for (EBuffKey buffKey : EBuffKey.values()) {
            TeamBuff buff = null;
            // vip才有的加成
            if (buffKey == EBuffKey.VIP加成 && type.isVip()) {
                buff = vipManager.getVipBuff(teamId, type);
                if (buff != null) {
                    buffList.add(buff);
                }
                continue;
            } else if (teamBuffMap.containsKey(buffKey.getStartID() + type.getId())) {
                buff = new TeamBuff(teamBuffMap.get(buffKey.getStartID() + type.getId()));
            }
            // 验证有效性
            if (buff != null && buff.getEndTime().isAfterNow()) {
                buffList.add(buff);
            }
        }
        return buffList;
    }

    /**
     * 取buff集合，包装后方便做一些计算
     *
     * @param teamId
     * @param type
     * @return
     */
    public BuffSet getBuffSet(long teamId, EBuffType type) {
        return new BuffSet(getBuff(teamId, type));
    }

    /**
     * 添加Buff
     *
     * @param buffKey
     * @param id       唯一表示，一般是teamId, 如果是联盟就是leagueId
     * @param tempBuff 具体加成
     */
    public void addBuff(long teamId, TeamBuff tempBuff) {
        if (tempBuff.getEndTime().isBeforeNow()) {
            return;
        }
        //
        Map<Integer, Buff> teamBuffs = getTeamBuffMap(teamId);
        Buff buff = teamBuffs.get(tempBuff.getBuffID());
        //
        if (buff != null && buff.getEndTime().isAfterNow() && !tempBuff.isOverlay()) {
            long addMillis = tempBuff.getEndTime().getMillis() - DateTime.now().getMillis();
            buff.setParams(tempBuff.getValues().getValueStr());
            buff.setEndTime(new DateTime(buff.getEndTime().getMillis() + addMillis));
        } else {
            buff = new Buff(new BuffPO(teamId, tempBuff));
            teamBuffs.put(buff.getId(), buff);
        }
        buff.save();
        // 推送buff
        sendMessage(teamId, getBuffData(buff), ServiceCode.BuffManager_push_buff);
        // buff事件
        EventBusManager.post(EEventType.Buff变更, buff);
    }

    /**
     * 移除buff
     *
     * @param teamId
     * @param key
     * @param type
     */
    public void removeBuff(long teamId, EBuffKey key, EBuffType type) {
        Buff buff = getTeamBuffMap(teamId).remove(key.getStartID() + type.getId());
        if (buff == null) { return; }
        buff.setEndTime(GameConsole.Min_Date);
        buff.save();
        // 推送buff
        sendMessage(teamId, getBuffData(buff), ServiceCode.BuffManager_push_buff);
    }

    public BuffPB.BuffData getBuffData(Buff buff) {
        return BuffPB.BuffData.newBuilder()
            .setId(buff.getId())
            .addAllValues(new DBList(buff.getParams()).getList())
            .setEndTime(buff.getEndTime().getMillis())
            .build();
    }

    /**
     * 登录取球队所有buff
     *
     * @param teamId
     * @return
     */
    public List<BuffPB.BuffData> getTeamBuffListData(long teamId) {
        List<BuffPB.BuffData> dataList = Lists.newArrayList();
        for (Buff buff : getTeamBuffMap(teamId).values()) {
            if (buff.getEndTime().isAfterNow()) {
                dataList.add(getBuffData(buff));
            }
        }
        return dataList;
    }

    /**
     * 调试接口
     *
     * @param teamId
     * @return
     */
    @ClientMethod(code = ServiceCode.BuffManager_buff_list)
    public void getTeamBuffList() {
        sendMessage(BuffPB.TeamBuffList.newBuilder().addAllBuffList(getTeamBuffListData(getTeamId())).build());
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Buff.getOrder();
    }
}
