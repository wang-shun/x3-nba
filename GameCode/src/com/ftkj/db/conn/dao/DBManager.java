package com.ftkj.db.conn.dao;

import com.ftkj.server.instance.InstanceFactory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tim.huang
 * 2016年2月22日
 * 部分DB处理
 */
public class DBManager {
    private static final Logger log = LoggerFactory.getLogger(DBManager.class);
    /** 游戏内模块的数据缓存集合(单个逻辑服) */
    private static Map<String, List<AsynchronousBatchDB>> asynchronousGameMap = new ConcurrentHashMap<>();
    /** 公共数据缓存集合 */
    private static Map<String, List<AsynchronousBatchDB>> asynchronousDMap = new ConcurrentHashMap<>();
    /** 需要执行游戏内模块数据清理的sql集合(单个逻辑服) */
    private static Set<String> gameDelSql = new LinkedHashSet<>();
    /** 需要活动模块数据清理的sql集合 */
    private static Set<String> dataDelSql = new LinkedHashSet<>();
    private static AtomicLong num = new AtomicLong(0);
    private static AtomicBoolean ab = new AtomicBoolean(true);
    private static AtomicInteger lo = new AtomicInteger(0);
    private static GameConnectionDAO game;
    private static DataConnectionDAO data;

    public synchronized static void putAsynchronousGameDB(AsynchronousBatchDB db) {
        List<AsynchronousBatchDB> list = asynchronousGameMap.computeIfAbsent(db.getTableName(), k -> new ArrayList<>());
        list.add(db);
    }

    public synchronized static void putAsynchronousDataDB(AsynchronousBatchDataDB db) {
        List<AsynchronousBatchDB> list = asynchronousDMap.computeIfAbsent(db.getTableName(), k -> new ArrayList<>());
        list.add(db);
    }

    public static void putGameDelSql(String sql) {
        gameDelSql.add(sql);
    }

    public static void putDataDelSql(String sql) {
        dataDelSql.add(sql);
    }

    /**
     * 执行数据处理
     */
    @Deprecated
    public static void runOld(boolean isStop) {
        if (!isStop && lo.incrementAndGet() < 10 && !ab.getAndSet(false)) {
            return;
        }
        num.incrementAndGet();
        lo.set(0);
        //        long now = System.currentTimeMillis();
        if (game == null) {
            game = new GameConnectionDAO();
            game.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_game.getResName());
        }
        //        String date = DateTimeUtil.getNowTime();
        if (asynchronousGameMap.size() > 0) {
            log.debug("批量执行Game更新对象类型数量[{}]", asynchronousGameMap.size());
            Map<String, List<AsynchronousBatchDB>> result = Maps.newHashMap(asynchronousGameMap);
            asynchronousGameMap = Maps.newConcurrentMap();

            result.keySet().stream().forEach(key ->
            {
                log.debug("批量更新对象类型[{}]", key);
                final StringBuffer sb = new StringBuffer();
                result.get(key).stream().forEach(db -> {
                    db.unSave();
                    sb.append(db.getSource());
                    //                    log.error(StringUtil.formatString("", key, db.getSource(), date));
                });
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                AsynchronousBatchDB ab = result.get(key).get(0);

                game.loadData(is, ab.getTableName(), ab.getRowNames(), ab.isLoggerDebugEnabled(), ab.isLoggerInfoEnabled());
            });
        }

        if (isStop || num.get() % 10 == 0) {
            gameDelSql.stream().forEach(sql -> game.execute(sql));
        }

        if (data == null) {
            data = new DataConnectionDAO();
            data.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_data.getResName());
        }

        if (asynchronousDMap.size() > 0) {
            log.debug("批量执行Data更新对象类型数量[{}]", asynchronousDMap.size());
            Map<String, List<AsynchronousBatchDB>> result = Maps.newHashMap(asynchronousDMap);
            asynchronousDMap = Maps.newConcurrentMap();

            result.keySet().stream().forEach(key ->
            {
                final StringBuffer sb = new StringBuffer("");
                result.get(key).stream().forEach(db -> {
                    db.unSave();
                    sb.append(db.getSource());
                });
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                AsynchronousBatchDB ab = result.get(key).get(0);
                data.loadData(is, ab.getTableName(), ab.getRowNames(), ab.isLoggerDebugEnabled(), ab.isLoggerInfoEnabled());
            });
        }
        if (isStop || num.get() % 10 == 0) {
            dataDelSql.stream().forEach(sql -> data.execute(sql));
        }
        //		log.debug("批量更新数据耗时------------------>"+(System.currentTimeMillis()-now));
        ab.set(true);
    }

    // 新的保存方法  have to test
    public static void run(boolean isStop) {
        if (!isStop && lo.incrementAndGet() < 10 && !ab.getAndSet(false)) {
            return;
        }
        num.incrementAndGet();
        lo.set(0);
        if (game == null) {
            game = new GameConnectionDAO();
            game.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_game.getResName());
        }
        if (asynchronousGameMap.size() > 0) {
            log.debug("批量执行Game更新对象类型数量[{}]", asynchronousGameMap.size());
            Map<String, List<AsynchronousBatchDB>> result = Maps.newHashMap(asynchronousGameMap);
            asynchronousGameMap = Maps.newConcurrentMap();

            result.keySet().stream().forEach(key -> {
                log.debug("批量更新对象类型[{}]", key);

                List<AsynchronousBatchDB> list = result.get(key);
                list.stream().forEach(db -> {
                    db.unSave();
                });
                AsynchronousBatchDB ab = list.get(0);
                game.batchUpdate(list, ab.getTableName(), ab.getRowNames(), ab.isLoggerDebugEnabled(), ab.isLoggerInfoEnabled());

            });
        }

        if (isStop || num.get() % 10 == 0) {
            gameDelSql.stream().forEach(sql -> game.execute(sql));
        }

        if (data == null) {
            data = new DataConnectionDAO();
            data.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_data.getResName());
        }

        if (asynchronousDMap.size() > 0) {
            log.debug("批量执行Data更新对象类型数量[{}]", asynchronousDMap.size());
            Map<String, List<AsynchronousBatchDB>> result = Maps.newHashMap(asynchronousDMap);
            asynchronousDMap = Maps.newConcurrentMap();

            result.keySet().stream().forEach(key -> {
                List<AsynchronousBatchDB> list = result.get(key);

                list.forEach(db -> {
                    db.unSave();
                });
                AsynchronousBatchDB ab = list.get(0);
                data.batchUpdate(list, ab.getTableName(), ab.getRowNames(), ab.isLoggerDebugEnabled(), ab.isLoggerInfoEnabled());
            });
        }
        if (isStop || num.get() % 10 == 0) {
            dataDelSql.stream().forEach(sql -> data.execute(sql));
        }
        log.debug("批量更新数据完毕");
        ab.set(true);
    }
}
