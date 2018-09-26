package com.ftkj.manager.gm;

import org.apache.mina.core.session.IoSession;

import com.ftkj.annotation.UnCheck;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GMCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.MessageManager;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.google.common.collect.Sets;

/**
 * 活动管理
 * @author Jay
 * @time:2018年1月31日 下午6:13:26
 */
public class GMSystemActiveManager extends BaseManager {

	@Override
	public void instanceAfter() {
	}

	/**
	 * 同步配置表的所有活动，如果活动已经存在，则时间不会发生变化
	 * @param shardId
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_syncAllSystemActive)
	public void syncAllSystemActive(String nodes) {
		log.error("{}:同步活动配置", nodes);
		if("All".equals(nodes)){
			RPCMessageManager.sendMessage(CrossCode.WebManager_syncAllSystemActive, nodes);
		}else{
			RPCMessageManager.sendMessageNodes(CrossCode.WebManager_syncAllSystemActive, Sets.newHashSet(nodes.split(",")));
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 批量修改活动时间
	 * 状态，先预留，可能以后有用
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_updateActiveTimeBatch)
	public void updateActiveTimeBatch(String nodes, String atvIds, long starTime, long endTime, int status) {
		log.error("批量修改活动时间");
		if("All".equals(nodes)){
			RPCMessageManager.sendMessage(CrossCode.WebManager_updateActiveTimeBatch, nodes, atvIds, starTime, endTime, status);
		}else{
			RPCMessageManager.sendMessageNodes(CrossCode.WebManager_updateActiveTimeBatch, Sets.newHashSet(nodes.split(",")), atvIds, starTime, endTime, status);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 修改活动配置 200000008
	 * @param nodes 节点 string
	 * @param atvId 活动ID int
	 * @param userConfig 活动配置 string
	 * @param shootEvent 是否触发时间 int : 0不触发事件，1触发事件
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_updateActiveConfigBatch)
	public void updateActiveTimeBatch(String nodes, int atvId, String userConfig, int shootEvent) {
		log.error("修改活动配置，是否发生事件, nodes={}, atvId={}, config={}, event={}", nodes, atvId, userConfig, shootEvent);
		if("All".equals(nodes)){
			RPCMessageManager.sendMessage(CrossCode.WebManager_updateActiveConfigBatch, nodes, atvId, userConfig, shootEvent);
		}else{
			RPCMessageManager.sendMessageNodes(CrossCode.WebManager_updateActiveConfigBatch, Sets.newHashSet(nodes.split(",")), atvId, userConfig, shootEvent);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 发布活动生效，刷新推送到客户端
	 * @param shardIds
	 * @param atvIds
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_queryActiveData)
	public void queryActiveData(String node, long userId,int sid, int atvId) {
		IoSession session = getSession();
		long teamId = GameSource.getTeamId(sid, userId);
		log.error("查询活动数据={},{}", teamId, atvId);
		RPCLinkedTask.build().appendTask((tid,maps,args)->{
			RPCMessageManager.sendLinkedTaskMessage(CrossCode.WebManager_queryActiveData, node, tid, teamId, atvId);
		}).appendTask((tid,maps,args)->{
			String json = (String)(args[0]);
			MessageManager.sendMessage(session, DefaultPB.DefaultData.newBuilder().setCode(0).setMsg(json).build(),GMCode.GMManager_pushTeamActiveData);
		}).start();
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 清空活动数据，新开活动，重开活动，限制一下，非结束活动，不能清
	 * @param shardIds
	 * @param atvIds
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_clearActiveData)
	public void clearActiveData(String nodes, String atvIds) {
		log.error("重置活动数据={}", atvIds);
		if("All".equals(nodes)){
			RPCMessageManager.sendMessage(CrossCode.WebManager_clearActiveData, nodes, atvIds);
		}else{
			RPCMessageManager.sendMessageNodes(CrossCode.WebManager_clearActiveData, Sets.newHashSet(nodes.split(",")), atvIds);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 清空指定玩家指定活动数据
	 * @param shardId
	 * @param teamId
	 * @param atvId
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_clearTeamActiveData)
	public void clearTeamActiveData(String node, long userId,int sid, int atvId) {
		long teamId = GameSource.getTeamId(sid, userId);
		log.error("清空指定玩家={}活动={}数据", teamId, atvId);
		RPCMessageManager.sendMessage(CrossCode.WebManager_clearTeamActiveData, node, teamId, atvId);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
	
	/**
	 * 创建对应区的活动表
	 */
	@UnCheck
	@ClientMethod(code = GMCode.GMManager_createActiveDataTable)
	public void createActiveDataTable(String nodes) {
		log.error("创建活动表");
		if("All".equals(nodes)){
			RPCMessageManager.sendMessage(CrossCode.WebManager_createActiveDataTable, nodes);
		}else{
			RPCMessageManager.sendMessageNodes(CrossCode.WebManager_createActiveDataTable, Sets.newHashSet(nodes.split(",")));
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
	}
}
