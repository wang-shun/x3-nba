package com.ftkj.test.match;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.MatchPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.IPUtil;

public class MatchSignTestCase extends BaseTestCase{
	
	public MatchSignTestCase() {
		super(IPUtil.localIP, 1000001329L);
//		super(IPUtil.getTestServerIp(), 999999);
	}
	
	@Test
	public void sign() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Match_Sign), 103, 1);
	}
	
//	@Test
//	public void signNpc() {
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.GMManager_Match_Sign_Npc), 103, 1, 7, 120);
//	}
	
	@Test
	public void showList() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Match_List, MatchPB.MatchListData.class));
	}
	@Test
	public void showLastRank() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Match_Last_Rank, MatchPB.MatchRankListData.class), 104);
	}
	
	@Test
	public void Match_Detai_View_Join() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Match_Detai_View_Join, MatchPB.MatchDetailData.class), 101, 1);
	}

	@Test
	public void TestTime() {
		Date dt = new Date(1513576800003L);
		DateTime a = new DateTime(dt);
		System.err.println(DateTimeUtil.getStringSql(a));
	}
	
}
