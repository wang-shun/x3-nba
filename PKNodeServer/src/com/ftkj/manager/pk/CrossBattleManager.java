package com.ftkj.manager.pk;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.server.CrossCode;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.rpc.IZKMaster;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.util.tuple.Tuple2Long;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tim.huang
 * 2017年4月21日
 * 跨服战斗管理
 */
public class CrossBattleManager extends BaseManager implements IZKMaster {
    private static final Logger log = LoggerFactory.getLogger(CrossBattleManager.class);
    private AtomicLong ids;

    @RPCMethod(code = CrossCode.BattleManager_getBattleId, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getBattleId(EBattleType battleType) {
        long battleId = ids.incrementAndGet();
        RPCMessageManager.responseMessage(battleId, battleType);
        log.info("batch gen battleid, max battleid {}", battleId);
        redis.set(RedisKey.Cross_Battle_Id, "" + battleId);
    }

    @RPCMethod(code = CrossCode.BattleManager_getBattleIdList, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getBattleId(int count) {
        if (count < 0) {
            count = 1;
        }
        log.info("batch gen battleid, size {}", count);
        long prev;
        long next;
        do {
            prev = ids.get();
            next = prev + count;
        } while (!ids.compareAndSet(prev, next));
        log.info("batch gen battleid, battleid {} -> {} {}", prev, next, ids.get());
        redis.set(RedisKey.Cross_Battle_Id, "" + next);
        RpcTask.resp(new Tuple2Long(prev, next));
    }

    public long getBattleId() {
        long battleId = ids.incrementAndGet();
        redis.set(RedisKey.Cross_Battle_Id, "" + battleId);
        return battleId;
    }

    @Override
    public void masterInit(String nodeName) {
        long id = redis.getLong(RedisKey.Cross_Battle_Id);
        ids = new AtomicLong(id);
    }

    @Override
    public void instanceAfter() {
        ids = new AtomicLong();
    }

}
