package com.ftkj.manager.logic;

import com.ftkj.enums.ERedPoint;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.proto.RedPointPB;
import com.ftkj.proto.RedPointPB.AwardTipData;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.instance.InstanceFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RedPointManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(RedPointManager.class);
    /** map[teamId, map[RP, num]] */
    private Map<Long, Map<ERedPoint, Integer>> teamNums = Maps.newConcurrentMap();
    private List<IRedPointLogic> logicList = Lists.newArrayList();
    /**
     * 登录的列表，晚点推送
     */
    private Queue<Long> loginTeams = Queues.newConcurrentLinkedQueue();

    @Override
    public void instanceAfter() {
        //        ThreadPoolUtil.newScheduledPool("RedPointThread", 10);
        EventBusManager.register(EEventType.奖励提示, this);
        // 将所有的接口实现类注册进来, 本类的实例化一定要晚点
        logicList.addAll(InstanceFactory.get().getInstanceList(IRedPointLogic.class));
        log.debug("IRedPointLogic List size={}", logicList.size());
        EventBusManager.register(EEventType.登录, this);
    }

    /** 获取提示次数 */
    public Integer getNum(long teamId, ERedPoint rp) {
        Map<ERedPoint, Integer> ret = teamNums.get(teamId);
        return ret != null ? ret.getOrDefault(rp, 0) : 0;
    }

    /** 更新提示次数 */
    void upNum(long teamId, ERedPoint rp, int num) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        teamNums.computeIfAbsent(teamId, tid -> new EnumMap<>(ERedPoint.class))
                .merge(rp, num, (oldv, v) -> oldv + v);
    }

    /** 移出提示次数 */
    void removeNum(long teamId, ERedPoint rp) {
        Map<ERedPoint, Integer> ret = teamNums.get(teamId);
        if (ret == null) {
            return;
        }
        ret.remove(rp);
        if (ret.isEmpty()) {
            teamNums.remove(teamId);
        }
    }

    /**
     * 模块，提示.发送小红点提示协议方法.
     * 如果是不一样，则推送.这个方法会有个问题,如果玩家在线跨越了零点,那么定时器会调用Team.java中的login(),
     * 那么就会调用现在这个类的login(LoginParam param)方法,把在线的玩家球队Id添加到loginTeams这个队列
     * 中,那么当有奖励领取调用此方法时if条件不满足就不会发送小红点提示.这里方法存在这样一个问题.
     */
    @Subscribe
    private void eventCall(RedPointParam params) {
        if (!loginTeams.contains(params.teamId) && params.modeule != ERedPoint.默认.getId()) { // && oldStatus != params.statusNum
            log.debug("红点推送：{}", params);
            sendMessage(params.teamId, redResp(params), ServiceCode.RedPointManager_push_Change);
        }
    }
    
    /**
     * 有奖励领取发送小红点提示.
     * @param param
     */
    public void sendRedPointTip(RedPointParam param){
    	if (param != null) {
    		sendMessage(param.teamId, redResp(param), ServiceCode.RedPointManager_push_Change);
		}
    }

    /**
     * 调试用
     */
    @ClientMethod(code = ServiceCode.RedPointManager_login)
    public void testTeamRedPointStatus(long teamId) {
        sendMessage(getTeamRedPointStatus(teamId));
    }

    /**
     * 登录调用
     */
    RedPointPB.RedPointData getTeamRedPointStatus(long teamId) {
        List<RedPointPB.AwardTipData> ret = Lists.newArrayList();
        for (IRedPointLogic logic : logicList) {
            try {
                RedPointParam params = logic.redPointLogic(teamId);
                if (params != null && params.modeule != ERedPoint.默认.getId()) {
                    ret.add(redResp(params));
                }
                
                logic.redPoints(teamId).forEach(obj -> {
                	if (obj != null && obj.modeule != ERedPoint.默认.getId()) {
                        ret.add(redResp(obj));
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        loginTeams.remove(teamId);
        return RedPointPB.RedPointData.newBuilder()
                .addAllStatusList(ret)
                .build();
    }

    private  AwardTipData redResp(RedPointParam params) {
        return AwardTipData.newBuilder()
                .setId(params.modeule)
                .setNum(params.statusNum)
                .build();
    }

    /**
     * 登录回调
     */
    @Subscribe
    public void login(LoginParam param) {
        if (!loginTeams.contains(param.teamId)) {
            loginTeams.add(param.teamId);
        }
    }

    @Override
    public void offline(long teamId) {
    }

    @Override
    public void dataGC(long teamId) {
    }
}
