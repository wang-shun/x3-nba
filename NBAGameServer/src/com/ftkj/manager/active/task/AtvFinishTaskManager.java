package com.ftkj.manager.active.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.TaskParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.task.ITaskActiveRefrush;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 爆竹，每天随机完成一定数量的任务，可领取奖励
 *
 * @author Jay
 * @time:2018年2月1日 下午5:32:26
 */
@EventRegister({EEventType.活动任务完成})
@ActiveAnno(redType = ERedType.活动, atv = EAtv.彩球大战,
        clazz = AtvFinishTaskManager.AtvFinishTask.class, redisClass = AtvTodayFinishTask.class)
public class AtvFinishTaskManager extends ActiveBaseManager implements ITaskActiveRefrush {

    /**
     * 每天要完成的任务数量
     */
    private static int TASK_COUNT = 4;

    /**
     * 任务集
     */
    private static List<Integer> taskSet;
    /**
     * 今日任务
     */
    private static List<Integer> todayTaskList;

    @IOC
    private TaskManager taskManager;

    @Override
    public void instanceAfter() {
        super.instanceAfter();
        TASK_COUNT = getConfigInt("taskCount", TASK_COUNT);
        // 活动ID列表
        String[] taskIds = getConfigStr("taskIds").split("_");
        taskSet = Arrays.stream(taskIds).filter(s -> !s.equals("")).mapToInt(i -> Integer.valueOf(i)).boxed().collect(Collectors.toList());
        todayTaskList = Lists.newArrayList();
        initTodayTask();
    }

    /**
     * 总的任务状态
     *
     * @author Jay
     * @time:2018年2月23日 下午6:48:34
     */
    public static class AtvFinishTask extends ActiveBase {
        private static final long serialVersionUID = 1L;

        public AtvFinishTask(ActiveBasePO po) {
            super(po);
        }

        public void addFinishCount(int count) {
            this.setiData1(this.getiData1() + count);
        }

        public int getFinishCount() {
            return this.getiData1();
        }
    }

    /**
     * 每天0点刷新任务
     */
    @Override
    public void everyDayStart(DateTime time) {
        super.everyDayStart(time);
        initTodayTask();
        EventBusManager.post(EEventType.活动任务刷新, getRefrushTask());
    }

    /**
     * 刷新任务
     */
    private void initTodayTask() {
        todayTaskList.clear();
        if (taskSet.size() > 0) {
            List<Integer> taskIdx = RandomUtil.getRandomBySeed(DateTime.now().getDayOfYear() * 123456L, taskSet.size(), TASK_COUNT, false);
            todayTaskList.addAll(taskIdx.stream().mapToInt(i -> taskSet.get(i)).boxed().collect(Collectors.toList()));
        }
        log.info("atv id {} task list = {}", this.getId(), todayTaskList);
    }

    /**
     * 完成任务回调
     *
     * @param param
     */
    @Subscribe
    public void finishTask(TaskParam param) {
        if (getStatus() != EActiveStatus.进行中) {
            return;
        }
        int taskId = param.taskId;
        if (!todayTaskList.contains(taskId)) {
            return;
        }
        AtvTodayFinishTask atvObj = getTeamDataRedisDay(param.teamId);
        if (atvObj.getFinishStatus().containsValue(taskId)) {
            return;
        }
        // 任务进度
        int taskIndex = todayTaskList.indexOf(taskId);
        atvObj.getTaskPlan().setValueAdd(taskIndex, param.val);
        //
        if (param.status == EStatus.TaskFinish.getId() && !atvObj.getFinishStatus().containsValue(taskIndex)) {
            atvObj.getFinishStatus().addValue(taskIndex);
        }
        saveDataReidsDay(param.teamId, atvObj);
        //
        if (atvObj.getFinishStatus().count() == TASK_COUNT) {
            redPointPush(param.teamId);
        }
    }

    /**
     * 要显示刷新出来的任务ID
     */
    @Override
    public void showView() {
        long teamId = getTeamId();
        AtvTodayFinishTask todayAtvObj = getTeamDataRedisDay(teamId);
        AtvFinishTask atvTask = getTeamData(teamId);
        sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
                .setAtvId(this.getId())
                // 任务列表
                .addAllOtherStatus(todayTaskList)
                // 任务进度
                .setExtend(todayAtvObj.getTaskPlan().getValueStr())
                // 今天点燃状态 1是已点
                .setValue(todayAtvObj.getTodayAwardStatus())
                // 总点燃次数
                .setOther(atvTask.getFinishCount())
                // 总点燃奖励是否可领
                .addAllFinishStatus(atvTask.getFinishStatus().getList())
                // 总点燃奖励是否已领
                .addAllAwardStatus(atvTask.getAwardStatus().getList())
                .build());
    }

    @Override
    public void gmReset(long teamId, AtvCommonPB.AtvCommonData cd) {
        AtvTodayFinishTask todayAtvObj = getTeamDataRedisDay(teamId);
        AtvFinishTask atvTask = getTeamData(teamId);
        boolean todaychange = false;
        boolean atvchange = false;
        if (cd.hasExtend()) {
            todayAtvObj.setTaskPlan(new DBList(cd.getExtend()));
            todaychange = true;
        }
        if (cd.hasValue()) {
            todayAtvObj.setTodayAwardStatus(cd.getValue());
            todaychange = true;
        }
        if (cd.hasOther()) {
            atvTask.setiData1((int) cd.getOther());
            atvchange = true;
        }
        if (cd.getFinishStatusCount() > 0) {
            atvTask.getFinishStatus().setList(cd.getFinishStatusList());
            atvchange = true;
        }
        if (cd.getAwardStatusCount() > 0) {
            atvTask.getAwardStatus().setList(cd.getAwardStatusList());
            atvchange = true;
        }
        //save
        if (todaychange) {
            saveDataReidsDay(teamId, todayAtvObj);
        }
        if (atvchange) {
            atvTask.save();
        }
    }

    /**
     * 领取每天完成任务奖励.
     */
    public void getDayAward() {
        if (checkTodo(getStatus() != EActiveStatus.进行中, ErrorCode.Active_1.code)) { return; }
        long teamId = getTeamId();
        AtvTodayFinishTask atvObj = getTeamDataRedisDay(teamId);
        if (checkTodo(atvObj.getFinishStatus().count() < TASK_COUNT, ErrorCode.Error.code)) { return; }
        if (checkTodo(atvObj.getTodayAwardStatus() != 0, ErrorCode.Error.code)) { return; }
        // 可以领奖
        atvObj.setTodayAwardStatus(1);
        saveDataReidsDay(teamId, atvObj);
        // 累计总完成状态
        int count = addTaskFinish(teamId);
        AtvFinishTask atvTask = getTeamData(teamId);
        // 发奖
        List<PropSimple> awardList = getAwardConfigList().get(0).getPropSimpleList();
        propManager.addPropList(teamId, awardList, true, getActiveModuleLog());
        sendMessage(AtvAwardData.newBuilder()
                .setAtvId(this.getId())
                .setAwardId(-1)
                .setValue(count)
                .setCode(ErrorCode.Success.code)
                .addAllAwardList(PropManager.getPropSimpleListData(awardList))
                .addAllFinishStatus(atvTask.getFinishStatus().getList())
                .addAllAwardStatus(atvTask.getAwardStatus().getList())
                .build());
    }

    /**
     * 检查额外奖励
     *
     * @param teamId
     */
    private int addTaskFinish(long teamId) {
        AtvFinishTask atvTask = getTeamData(teamId);
        atvTask.addFinishCount(1);
        //
        for (int id : getAwardConfigList().keySet()) {
            int needNum = Integer.valueOf(getAwardConfigList().get(id).getConditionMap().get("num"));
            if (atvTask.getFinishStatus().containsValue(id)) { continue; }
            if (atvTask.getFinishCount() < needNum) { continue; }
            atvTask.getFinishStatus().addValue(id);
        }
        atvTask.save();
        return atvTask.getFinishCount();
    }

    @Override
    public Map<Integer, DateTime> getRefrushTask() {
        Map<Integer, DateTime> map = Maps.newHashMap();
        if (getStatus() != EActiveStatus.进行中) {
            return map;
        }
        DateTime end = DateTimeUtil.getTodayEndTime();
        for (int taskId : todayTaskList) {
            map.put(taskId, end);
        }
        return map;
    }

}
