package com.ftkj.test.playercard;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.PlayerCardPB;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class PlayerCardTestCase extends BaseTestCase {

	public PlayerCardTestCase() {
		super(IPUtil.localIP, 1000001322L);
	}
	
	@Test
	public void showView() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.PlayerCardManager_View, PlayerCardPB.PlayerCardMainData.class));
	}
	
	@Test
	public void oneKeyMakeCard() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.BeSignManager_oneKeyMakeCard, CollectData.class));
	}
	
	@Test
	public void MakeCard() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.ScoutManager_MakeCard), 0);
	}
	@Test
	public void MakeCard31507() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.BeSignManager_makeCard), 0);
	}
	
	@Test
	public void composite() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.PlayerCardManager_composite));
	}
	
	@Test
	public void upStarLv() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.PlayerCardManager_upStarLv), 0, 2327577);
	}
}
