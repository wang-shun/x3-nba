package com.ftkj.server.rpc.task;

import com.ftkj.util.lambda.TMap;

import java.io.Serializable;

@FunctionalInterface
public interface IRPCTask {

    void execute(int id, TMap maps, Serializable[] objects);

}
