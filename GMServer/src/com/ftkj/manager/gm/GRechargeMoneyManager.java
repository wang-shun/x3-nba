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

/**
 * @author tim.huang
 * 2017年11月24日
 * 充值管理
 */
public class GRechargeMoneyManager extends BaseManager {
	
	
	
	@UnCheck
	@ClientMethod(code=GMCode.GMManager_recharge)
	public void recharge(String node,String plat,long userId,int sid,int rmb,int money,String orderId
								,int type, long millis){
		IoSession session = getSession();
		long teamId = GameSource.getTeamId(sid, userId);
		// 时间戳传0是就默认的当前时间
		long currMillis = millis == 0 ? System.currentTimeMillis() : millis;
		RPCLinkedTask.build().appendTask((tid,maps,args)->{
			RPCMessageManager.sendLinkedTaskMessage(CrossCode.WebManager_addMoney, node, tid,teamId,money,type,currMillis);
		}).appendTask((tid,maps,args)->{
			int code = (int)(args[0]);
			MessageManager.sendMessage(session, DefaultPB.DefaultData.newBuilder().setCode(code).build(),GMCode.GMManager_recharge);
//			log.error("Rechare result:{},address:{}",code,session);
//			RPCMessageManager.sendLinkedTaskMessage(CrossCode.WebManager_addMoney, node, tid,teamId,money,type);
		}).start();
	}
	
	
	@Override
	public void instanceAfter() {

	}
	
	
	
	
	

}
