package com.ftkj.test.trade;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class BuyPlayerTestCase extends BaseTestCase{
	
	// 连接服务器
	public BuyPlayerTestCase() {
		super(IPUtil.localIP, 10000000716L);
	}
	
	@Test
	public void test() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.TradeManager_BuyPlayer))
		/**
		 * 购买/下架
		 * @param  tradeId 交易ID
		 */
		.send(22);
	}

}
