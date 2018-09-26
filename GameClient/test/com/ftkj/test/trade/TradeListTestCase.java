package com.ftkj.test.trade;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.TradePB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class TradeListTestCase extends BaseTestCase{
	
	// 连接服务器
	public TradeListTestCase() {
		super(IPUtil.localIP, 10000000440L);
	}
	
	@Test
	public void test() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.TradeManager_QueryTradeList, TradePB.TradeListData.class))
		/**
		 * 球员关键字
		 * @param playerName 模糊查询，""是所有
		 * @param grade 球员等级，0 是所有
		 * @param pos 球员位置， 0是所有
		 * @param moneyOrder 球券排序 同上
		 * @param priceOrder 工资排序，0是默认， 1，升序 2，降
		 * @param page 显示分页
		 * 位置：1~5分别是PG,SG,SF,PF,C
		 * 注意：1页最大能显示16个球员
		 */
		.send("", "", 0, 0, 0, 0);
	}

}
