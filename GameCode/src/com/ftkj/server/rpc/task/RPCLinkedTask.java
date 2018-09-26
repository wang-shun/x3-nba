package com.ftkj.server.rpc.task;

import com.ftkj.manager.RPCManager;
import com.ftkj.util.lambda.TMap;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年4月19日
 * RPC链式任务
 * <p>
 * RPCLinkedTask.build().appendTask((tid,maps,args)->{
 * RPCMessageManager.sendLinkedTaskMessage(cid, null, tid,arg);
 * });
 */
public class RPCLinkedTask {
    private static final Logger log = LoggerFactory.getLogger(RPCLinkedTask.class);
    private volatile int index;

    private int maxInde;
    private IRPCTask task;

    private RPCLinkedTask next;

    private int id;

    private DateTime startTime;

    private TMap maps;

    private static final long Time_Out = 1000 * 5;

    private RPCLinkedTask(int id, IRPCTask task) {
        this(id, new TMap(), task);
    }

    private RPCLinkedTask(int id, TMap maps, IRPCTask task) {
        this.id = id;
        this.task = task;
        this.maps = maps;
    }

    public boolean isEnd() {
        return this.index > this.maxInde;
    }

    public static RPCLinkedTask build() {
        RPCLinkedTask rlt = new RPCLinkedTask(RPCManager.getId(), null);
        rlt.index = 1;
        rlt.startTime = DateTime.now();
        return rlt;
    }

    public RPCLinkedTask appendTask(IRPCTask task) {
        if (this.next != null) {
            this.next.appendTask(task);
        } else {
            this.next = new RPCLinkedTask(this.id, this.maps, task);
        }
        maxInde++;
        return this;
    }

    public void start(Serializable... objects) {
        RPCManager.putAndExceute(this, objects);
    }

    public RPCLinkedTask next() {
        RPCLinkedTask rlt = this;
        for (int i = 0; i < this.index; i++) {
            if (rlt == null) {
                log.debug("");
            }
            rlt = rlt.next;
        }
        this.index++;
        return rlt;
    }

    public void exceute(Serializable[] objects) {
        this.task.execute(this.id, this.maps, objects);
    }

    public boolean isTimeOut() {
        return DateTime.now().getMillis() - this.startTime.getMillis() > RPCLinkedTask.Time_Out;
    }

    public int getId() {
        return id;
    }

}
