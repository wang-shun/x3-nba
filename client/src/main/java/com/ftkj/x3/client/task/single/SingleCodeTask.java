package com.ftkj.x3.client.task.single;

import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.helper.LoginHelper;
import com.ftkj.x3.client.task.logic.LogicTask;

/**
 * 单个消息测试
 *
 * @author luch
 */
public abstract class SingleCodeTask extends LogicTask {
    public static final long User_AccountId = LoginHelper.User_AccountId;
    @Override
    protected final Ret run0(String[] args) {
        log.info("\n=================  single code ({}) test start  ================",
                getClass());
        run1(args);
        return succ();
    }

    protected void logspliter() {
        log.info("\n=================  single code ({})  ================",
                getClass());
    }

    protected abstract void run1(String[] args);
}
