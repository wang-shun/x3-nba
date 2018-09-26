package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ArenaConsole;
import com.ftkj.db.dao.logic.ArenaDao;
import com.ftkj.manager.arena.Arena;
import com.ftkj.manager.team.Team;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.FixedArrayList;
import com.ftkj.util.ListsUtil;
import com.ftkj.util.MemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 竞技场. 个人排名竞技.
 * 只加载排名前{@link #Max_Rank_Size}的球队竞技场信息,
 * 去重后保留{@link #Max_Rank_Size}个. 超出的玩家的排名为 {@link #Max_Rank_Size} + 1.
 *
 * @author luch
 */
public class ArenaManager extends AbstractBaseManager {
    private static final Logger log = LoggerFactory.getLogger(ArenaManager.class);
    @IOC
    private ArenaDao arenaDao;
    @IOC
    private JedisUtil redis;
    /** 竞技场排名信息只在内存中保留固定个数, 节约内存. 大于此个数的没有排名 */
    private static final int Max_Rank_Size = Arena.Max_Rank_Size;
    //    /** 排名重复时,保留的最大排名. 用于出bug时兼容 */
    //    public static final int Pad_Max_Rank = 20000;

    /** 已开启竞技场的球队的竞技场信息. 前{@link #Max_Rank_Size} 名 */
    private final ConcurrentMap<Long, Arena> arenas = new ConcurrentHashMap<>(Max_Rank_Size);
    /** 球队竞技场排名, 下标是rank-1, 元素是球队id. 有并发调用, 不要迭代, 使用get获取元素 */
    private final FixedArrayList<Long> ranks = new FixedArrayList<>(Long.class, Max_Rank_Size);

    /** 系统启动时加载竞技场信息 */
    public void init() {
        log.info("arena init ...");
        // 清除掉Redis缓存的竞技场排名数据
        redis.delRedisCache(RedisKey.Arena + "*");
        
        final long mem = MemUtil.freeMemory();
        //预初始化
        for (int rank = 1; rank <= Max_Rank_Size; rank++) {
            Long npcId = ArenaConsole.getNpcByRank(rank);
            if (npcId != null) {
                arenas.put(npcId, new Arena(npcId, rank, rank));
                ranks.set(rank - 1, npcId);
            }
        }
        final List<Arena> all = arenaDao.findByRankAsc(Max_Rank_Size);
        for (final Arena ar : all) {
            if (ar.getRank() <= 0 && ar.getRank() > Max_Rank_Size) {
                continue;
            }
            final int oldrank = ar.getRank();
            Long rtid = ranks.get(ar.getRank() - 1);
            boolean change = false;
            while (rtid != null && !GameSource.isNPC(rtid) && rtid != ar.getTeamId()) {
                ar.setRank(ar.getRank() + 1);//相同排名上已经有一个其他非npc球队, 当前球队排名后移一名
                change = true;
                Long rtid1 = ranks.get(ar.getRank() - 1);
                if (rtid1 == null || GameSource.isNPC(rtid1)) {
                    break;
                }
                rtid = rtid1;
            }
            if (change) {
                ar.save();
                redis.set(cacheKey(ar.getTeamId()), ar, RedisKey.DAY3);
                log.warn("arena init. rank {} otid {} ntid {} exists. newrank {} save",
                    oldrank, rtid, ar.getTeamId(), ar.getRank());
            }

            if (ar.getRank() <= Max_Rank_Size) {
                arenas.put(ar.getTeamId(), ar);
                ranks.set(ar.getRank() - 1, ar.getTeamId());
            }
        }
        log.info("arena init done. maxrank {} qsize {} rsize {} {}. mem used {}",
            Max_Rank_Size, all.size(), arenas.size(), ranks.size(), MemUtil.memoryUsed(mem));
    }

    //    /** 获取竞技场排名信息. 没有排名返回 -1 */
    //    public int getArenaRank(long teamId) {
    //        Arena arena = getArena(teamId);
    //        if (arena == null) {
    //            return -1;
    //        }
    //        return arena.getRank();
    //    }

    private Arena getArenaFromDb(long teamId) {
        String cacheKey = cacheKey(teamId);
        Arena ar = redis.getObj(cacheKey);
        if (ar != null) {
            return ar;
        }

        ar = arenaDao.findById(teamId);
        if (ar != null) {
            redis.set(cacheKey, ar, RedisKey.DAY3);
        }
        return ar;
    }

    /** 更新竞技场信息 */
    protected void update(Arena ar) {
        if (GameSource.isNPC(ar.getTeamId())) {
            return;
        }
        ar.save();
        String cacheKey = cacheKey(ar.getTeamId());
        redis.set(cacheKey, ar, RedisKey.DAY3);
    }

    private String cacheKey(long teamId) {
        return RedisKey.Arena + teamId;
    }

    //    /** 根据竞技场排名索引获取球队id. 0 <= rank < total size. 从0开始. */
    //    public Long getTeamIdByRankIdx(int rankIndex) {
    //        if (!ListsUtil.rangeCheck(ranks.size(), rankIndex)) {
    //            return null;
    //        }
    //        return getTeamIdOrNpc(rankIndex);
    //    }

    /** 根据竞技场排名获取球队id. 0 < rank <= total size */
    Long getTeamIdByRank(int rank) {
        if (!ListsUtil.rangeCheck(ranks.size(), rank - 1)) {
            return null;
        }
        return getTeamIdOrNpc(rank - 1);
    }

    private Long getTeamIdOrNpc(int rankIdx) {
        Long tid = ranks.get(rankIdx);
        if (tid == null) {
            int rank = rankIdx + 1;
            Long npcId = ArenaConsole.getNpcByRank(rank);
            if (npcId != null) {
                Arena ar = arenas.get(npcId);
                if (ar == null) {
                    ar = new Arena(npcId, rank, rank);
                    arenas.put(npcId, ar);
                }
                tid = npcId;
                ranks.set(rankIdx, npcId);
            }
        }
        return tid;
    }

    /**
     * 获取从 startIdx 到 endIdx 排名的球队id
     *
     * @param startIdx 包括
     * @param endIdx   不包括
     * @param consumer BiConsumer[下标, 球队id]
     */
    void forEachRanks(int startIdx, int endIdx, BiConsumer<Integer, Long> consumer) {
        if (startIdx < 0) {
            startIdx = 0;
        }
        if (endIdx > ranks.size()) {
            endIdx = ranks.size();
        }
        for (int i = startIdx; i < endIdx; i++) {
            Long tid = getTeamIdOrNpc(i);
            consumer.accept(i, tid);
        }
    }

    /**
     * 循环所有排名
     *
     * @param consumer BiConsumer[下标, 球队id]
     */
    public void forEachRanks(BiConsumer<Integer, Long> consumer) {
        forEachRanks(0, ranks.size(), consumer);
    }

    /** 循环所有有排名的竞技场信息 */
    void forEachArena(Consumer<Arena> consumer) {
        arenas.values().forEach(consumer);
    }

    /** 获取球队的竞技场信息 */
    public Arena getArena(long teamId) {
        Arena ar = arenas.get(teamId);
        if (ar != null) {
            return ar;
        }
        ar = getArenaFromDb(teamId);
        if (ar != null) {
            arenas.put(teamId, ar);
        }
        return ar;
    }

    /** 比赛胜利之后交换排名 */
    void swapRank(Arena ar, Arena target, int oldSelfRank, int oldTargetRank) {
        if (ar.getTeamId() == target.getTeamId()) {
            return;
        }
        
        if ((oldSelfRank - 1) < 5000 && (oldTargetRank - 1) < 5000) {
        	Long arTeamId = ranks.get(oldSelfRank - 1);
            Long targetTeamId = ranks.get(oldTargetRank - 1);
            // 根据排名获取球队的Id,看是否一致
            if (arTeamId == null || targetTeamId == null 
            		|| arTeamId.longValue() != ar.getTeamId() 
            		|| targetTeamId.longValue() != target.getTeamId()) {
    			log.warn("Arena swapRank teamId:{},targetTeamId:{},oldSelfRank:{}"
    					+ ",oldTargetRank:{},ranksTeamId:{},ranksTargetTeamId:{}"
    					, ar.getTeamId(), target.getTeamId(), oldSelfRank, oldTargetRank
    					, arTeamId, targetTeamId);
    		}
		}
        
        if (oldSelfRank < 100) {//临时加的日志,用于排查重名问题
			log.info("swapRank|myTeamId|{}|myArenaRank|{}|targetTeamId|{}"
				+ "|targetArenaRank|{}|---|myOldRank|{}|targetOldRank|{}"
				,ar.getTeamId(),ar.getRank(),target.getTeamId(),target.getRank(),oldSelfRank,oldTargetRank);
		}
        
        
        boolean targetIsNpc = GameSource.isNPC(target.getTeamId());
        synchronized (this) {//swap rank
            if (oldTargetRank < oldSelfRank || (oldSelfRank <= 0 && isNormalRank(oldTargetRank))) {
                ar.setRank(oldTargetRank);
                if (!targetIsNpc) {
                    target.setRank(oldSelfRank);
                }
                if (oldTargetRank < ar.getMaxRank()) {
                    ar.setMaxRank(oldTargetRank);
                }
                int newSelfIdx = oldTargetRank - 1;
                if (isNormalRankIdx(newSelfIdx)) {
                    ranks.set(newSelfIdx, ar.getTeamId());
                    addArenas(ar);
                } else {
                    removeArenas(ar);
                }
                int newTargetIdx = oldSelfRank - 1;
                if (!targetIsNpc) {
                    if (isNormalRankIdx(newTargetIdx)) {
                        ranks.set(newTargetIdx, target.getTeamId());
                        addArenas(target);
                    } else {
                        removeArenas(target);
                    }
                } else {
                    if (ListsUtil.rangeCheck(ranks, newTargetIdx)) {
                        ranks.set(newTargetIdx, null);
                    }
                }
                update(ar);
                if (!targetIsNpc) {
                    update(target);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("arena swapRank. tid1 {} rank {} {} tid2 {} rank {}",
                ar.getTeamId(), oldSelfRank, ar.getMaxRank(), target.getTeamId(), oldTargetRank);
        }
    }

    void addArenas(Arena ar) {
        arenas.put(ar.getTeamId(), ar);
    }

    private void removeArenas(Arena ar) {
        removeArenas(ar.getTeamId());
    }

    Arena removeArenas(long tid) {
        if (GameSource.isNPC(tid)) {
            return null;
        }
        Arena ar = arenas.get(tid);
        if (ar == null) {
            return null;
        }
        if (ar.getRank() <= 0 || ar.getRank() > Arena.Max_Rank_Size) {
            arenas.remove(tid);
        } else {
            Long rtid = getTeamIdByRank(ar.getRank());
            boolean rankMismatch = rtid != null && ar.getTeamId() != rtid;
            if (rankMismatch) {
                log.warn("arena remove rankmismatch. tid {} rank {} ttid {}", tid, ar.getRank(), rtid);
                arenas.remove(tid);
            }
        }
        return ar;
    }

    /** 是否是有效排名索引 */
    private boolean isNormalRankIdx(int rankIdx) {
        return rankIdx >= 0 && rankIdx < Max_Rank_Size;
    }

    /** 是否是有效排名 */
    private boolean isNormalRank(Arena ar) {
        return isNormalRank(ar.getRank());
    }

    /** 是否是有效排名 */
    private boolean isNormalRank(int rank) {
        return rank > 0 && rank <= Max_Rank_Size;
    }

    /** 新增竞技场信息 */
    Arena insertArena(Team team) {
        Arena ar;//激活模块
        synchronized (this) {
            int maxRank = Max_Rank_Size + 1;
            ar = new Arena();
            ar.setTeamId(team.getTeamId());
            ar.setRank(maxRank);
            ar.setMaxRank(maxRank);

            if (log.isDebugEnabled()) {
                log.debug("arena insert. tid {} maxrank {} size {}", team.getTeamId(), maxRank, arenas.size());
            }
            //            if (maxRank < Max_Rank_Size) {
            //                int rankIdx = maxRank - 1;
            //
            //                arenas.put(team.getTeamId(), ar);
            //                if (ListsUtil.rangeCheck(ranks.size(), rankIdx)) {
            //                    ranks.set(rankIdx, team.getTeamId());
            //                }
            //            }
        }
        ar.save();
        redis.set(cacheKey(ar.getTeamId()), ar, RedisKey.DAY3);
        return ar;
    }
}
