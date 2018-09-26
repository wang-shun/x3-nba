package com.ftkj.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.enums.EVersion;
import com.ftkj.manager.User;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Maps;

/**
 * 游戏数据源
 *
 * @author tim.huang
 * 2015年12月1日
 */
public class GameSource {
    private static final Logger log = LoggerFactory.getLogger(GameSource.class);
    public static final String MINA_SESSION_ATTR_KEY_TEAMID = "teamId";
    /** 服务器id */
    public static int shardId = 100;
    /** 语言版本 */
    public static EVersion charset;
    /** 是否调试模式 */
    public static boolean isDebug = false;
    /** 节点名称 */
    public static String serverName = "Tim";
    /** zk父级路径 */
    public static String zkPath = "/zgame";
    /** 节点池名称 */
    public static String pool = "logic";
    /** 顶级域名 */
    public static String topAddress = "ftxgame.com";
    /** 平台 */
    public static String platform = "ios";

    public static DateTime openTime = DateTime.now();
    /** 是否外网服务器环境 */
    public static boolean net = false;
    /** 是否统计调用记录*/
    public static boolean stat = true;
     /** 是否开启系统信息打印*/
    public static boolean statJob = true;
    /** 消息统计信息 */
    public static boolean MESSAGE_STATS = true;
    /** gm命令是否开启. 默认关闭 */
    public static boolean GM = false;
    private static final Object EMPTY = new Object();
    /** 服务器状态 */
    public static boolean isOpen = true;
    public static AtomicInteger cacheVersion = new AtomicInteger(0);
    //	private static Logger log = LoggerFactory.getLogger(GameSource.class);
    /** 在线玩家列表 */
    private static Map<Long, User> online_team = Maps.newConcurrentMap();
    /** 已经离线玩家列表 */
    private static ConcurrentMap<Long, Object> offline_team = new ConcurrentHashMap<>();
    //	private static List<Long> remove_team = Lists.newArrayList();
    /** 不在线需要回收内存的玩家 */
    private static ConcurrentMap<Long, Object> gc_team = new ConcurrentHashMap<>();

    public static void fullLog() {
        if (log.isInfoEnabled()) {
            log.info("gs full. sid {} charset {} debug {} name {} zk {} pool {} addr {} platform {}" +
                            " opentime {} net {} stat {} job {} gm {} open {}",
                    shardId, charset, isDebug, serverName, zkPath, pool, topAddress, platform,
                    openTime, net, stat, statJob, GM, isOpen);
        }
    }

    public static void onlineLog() {
        if (log.isInfoEnabled()) {
            log.info("gs online teams. sid {} online {} offline {} gc {}",
                    shardId, online_team.size(), offline_team.size(), gc_team.size());
        }
    }

    public static boolean isNPC(long teamId) {
        return teamId < 10000000;
    }

    public static boolean isHero(int heroId) {
        return heroId < 100000;
    }

    public static boolean isSuperHero(int heroId) {
        return heroId < 100;
    }

    public static User getUser(long teamId) {
        return online_team.get(teamId);
    }

    public static long getTeamId(long userId) {
        return shardId * 10000000000l + userId;
    }

    public static long getTeamId(long shardId, long userId) {
        return shardId * 10000000000l + userId;
    }

    public static int getSid(long teamId) {
        return Math.round(teamId / 10000000000l);
    }

    public static long getUserId(long teamId) {
        return teamId - shardId * 10000000000l;
    }

    public static boolean isOline(long teamId) {
        return online_team.containsKey(teamId);
    }

    public static void offlineUser(long teamId) {
        User user = online_team.remove(teamId);
        //Close_Connect
        if (user != null && user.getSession() != null && user.getSession().isActive()) {
            user.getSession().closeNow();
        }
    }

    public static Collection<User> getUsers() {
        return online_team.values();
    }

    public static Collection<Long> removeAllOffLine() {
        ConcurrentMap<Long, Object> result = new ConcurrentHashMap<>(offline_team);
        offline_team.clear();
        return result.keySet();
    }

    public static Collection<Long> removeAllGcTeams() {
        ConcurrentMap<Long, Object> result = new ConcurrentHashMap<>(gc_team);
        gc_team.clear();
        return result.keySet();
    }

    /**
     * 离线
     */
    public static User offline(long teamId, IoSession session) {
        offline_team.put(teamId, EMPTY);
        User user = getUser(teamId);
        if (user != null && session == user.getSession()) {
            online_team.remove(teamId);
        }
        log.debug("用户离线[{}]", teamId);
        return user;
    }

    public static void online(long teamId, IoSession session) {
        User user = new User(teamId, session);
        online_team.put(teamId, user);
        offline_team.remove(teamId);
        gc_team.remove(teamId);
        //		remove_team.remove(teamId);
        user.login();
        //		ServerStat.incOnline();
        log.debug("用户上线[{}]", teamId);
    }

    public static boolean inOffline(long tid) {
        return offline_team.containsKey(tid);
    }

    public static void removeGC(Long tid) {
        gc_team.remove(tid);
    }

    /** 检查是否回收数据, 不在线的玩家才会加入回收列表 */
    public static void checkGcData(long teamId) {
        if (!isNPC(teamId) && !isOline(teamId) && !inOffline(teamId)) {
            gc_team.put(teamId, EMPTY);
        }
    }

    public static void updateDebug(boolean debug) {
        isDebug = debug;
    }

    public static void updateOpen(boolean open) {
        isOpen = open;
    }

    /** 获得在线数据 */
    public static int getOlineCount() {
        return online_team.size();
    }

    /** 第一天是1，现在时间和开服时间的时间差 */
    public static int getServerStartDay() {
        DateTime d1 = GameSource.openTime;
        DateTime d2 = DateTime.now();
        return DateTimeUtil.getDaysBetweenNum(d1, d2, 0) + 1;
    }

}
