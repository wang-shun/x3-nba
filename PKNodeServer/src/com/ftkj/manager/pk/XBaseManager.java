package com.ftkj.manager.pk;

import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.server.rpc.RpcTask.RpcResp;

import java.io.Serializable;

/**
 *
 */
public abstract class XBaseManager extends BaseManager {
    protected XBaseManager() {
        super();
    }

    protected XBaseManager(boolean init) {
        super(init);
    }

    protected static <T extends Serializable> RpcResp<T> succ(T t) {
        return new RpcResp<>(ErrorCode.Success, t);
    }

    protected static <T extends Serializable> RpcResp<T> ret(ErrorCode ret) {
        return new RpcResp<>(ret, null);
    }

    @Override
    public void instanceAfter() {
    }
}
