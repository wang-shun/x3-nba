package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.PKParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.statistics.TeamDayStatistics;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TeamDayStatsManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(TeamDayStatsManager.class);
    @IOC
    private TaskManager taskManager;
    private ConcurrentMap<Long, TeamNums> teams = new ConcurrentHashMap<>();

    @Override
    public void instanceAfter() {
        // 监听比赛结束事件
        EventBusManager.register(EEventType.比赛结束, this);
    }

    @Override
    public void offline(long teamId) {
        teams.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teams.remove(teamId);
    }

    public TeamDayStatistics getTeamDayStatistics(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Team_Day_Statistics);
        TeamDayStatistics data = redis.getObj(key);
        if (data == null) {
            data = new TeamDayStatistics(teamId);
            redis.set(key, data, RedisKey.DAY2);
        } else {
            data.redisGetInit();
        }
        return data;
    }

    private void save(long teamId, TeamDayStatistics data) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Team_Day_Statistics);
        redis.set(key, data, RedisKey.DAY2);
    }

    public TeamNums getNums(long tid) {
        LocalDate ld = LocalDate.now();
        TeamNums tn = teams.get(tid);
        if (tn != null) {
            if (!ld.isEqual(tn.ld)) {//新的一天
                tn = new TeamNums(tid, ld, RedisKey.getDayKey(tid, RedisKey.Team_Day_Num, ld));
            }
        }
        if (tn == null) {
            tn = new TeamNums(tid, ld, RedisKey.getDayKey(tid, RedisKey.Team_Day_Num, ld));
            redis.hgetall(tn.getRedisKey(), TeamDayNumType::convertByName, this::parseInt, tn::getNums);
        }
        return tn;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    public int getNums(long tid, TeamDayNumType key) {
        TeamNums tn = getNums(tid);
        return tn.getNum(key, 0);
    }

    public void saveTeamNums(long tid, TeamDayNumType key, int num) {
        if (key == null) {
            return;
        }
        TeamNums tn = getNums(tid);
        saveTeamNums(tn, key, num);
    }

    public void saveTeamNums(TeamNums nums, TeamDayNumType key, int num) {
        boolean empty = nums.getNums().isEmpty();
        nums.getNums().put(key, num);
        redis.hset(nums.getRedisKey(), key.getConfigName(), String.valueOf(num));
        if (empty) {
            redis.expire(nums.getRedisKey(), RedisKey.DAY3);
        }
    }

    /** gm命令重置所有日常次数 */
    public void gmResetAllDailyNum(long tid) {
        TeamNums tn = getNums(tid);
        if (tn == null) {
            return;
        }
        teams.remove(tid);
        redis.del(tn.getRedisKey());
    }
    //---------------------

    /**
     * 所有的比赛类型结束都会在这里
     *
     * @param param
     */
    @Subscribe
    private void pkEnd(PKParam param) {
        // 统计每天比赛次数
        if (GameSource.isNPC(param.teamId)) {
            return;
        }
        save(param.teamId, getTeamDayStatistics(param.teamId).addPkCount(param.type));
        log.debug("每天比赛[{}]场次数统计={}", param.type, getTeamDayStatistics(param.teamId));
//        // 统计多人赛比赛次数100~200
//        if (param.type.getId() >= 100 && param.type.getId() <= 200) {
//            taskManager.updateTask(param.teamId, ETaskCondition.多人赛, 1, "");
//        }  改为报名计算次数
    }

    /** 每日次数 */
    public static final class TeamNums {
        private long tid;
        private LocalDate ld;
        private String redisKey;
        private Map<TeamDayNumType, Integer> nums = new EnumMap<>(TeamDayNumType.class);

        public TeamNums(long tid, LocalDate ld, String redisKey) {
            this.tid = tid;
            this.ld = ld;
            this.redisKey = redisKey;
        }

        public long getTid() {
            return tid;
        }

        public LocalDate getLd() {
            return ld;
        }

        public String getRedisKey() {
            return redisKey;
        }

        public Map<TeamDayNumType, Integer> getNums() {
            return nums;
        }

        public int getNum(TeamDayNumType key, int dval) {
            return nums.getOrDefault(key, dval);
        }
    }
}
