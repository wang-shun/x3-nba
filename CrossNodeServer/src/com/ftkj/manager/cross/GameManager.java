package com.ftkj.manager.cross;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.server.CrossCode;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.instance.InstanceFactory;

public class GameManager extends BaseManager {

	/**
     * @param status 推送指定游戏节点的球员身价更新
     */
    @RPCMethod(code = CrossCode.WebManager_reloadNBAData, pool = EServerNode.Node, type = ERPCType.NONE)
    public void reloadNBAData(int status) {
        CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
        manager.resetCache();
    }

    /**
     * @param status 推送指定游戏节点的球员身价更新
     */
    @RPCMethod(code = CrossCode.WebManager_reloadAllNodeNBAData, pool = EServerNode.Node, type = ERPCType.ALLNODE)
    public void reloadAllNodeNBAData(int status) {
        CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
        manager.resetCache();
    }
    
    @RPCMethod(code = CrossCode.CrossLimitChallengeUpdateNotice, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void limitChallengeUpdate() {
      RPCMessageManager.sendMessage(CrossCode.LocalGameManagger_limit_challenge_update, null);
    }
    
	@Override
	public void instanceAfter() {
	  
	}

}
