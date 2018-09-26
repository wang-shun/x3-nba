package com.ftkj.test.trade;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class SellPlayerTestCase extends BaseTestCase{
	
	// 连接服务器
	public SellPlayerTestCase() {
		super(IPUtil.localIP, 10000000440L);
	}
	
	@Test
	public void test() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.TradeManager_SellPlayer))
		/**
		 * 上架
		 * @param pid 球员唯一ID
		 * @param money 出售价格
		 * @param day 上架天数 1~3
		 */
		.send(5, 50, 1);
	}

}
