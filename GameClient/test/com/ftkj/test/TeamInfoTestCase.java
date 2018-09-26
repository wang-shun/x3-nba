package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.proto.PlayerArchivePB;
import com.ftkj.proto.TargetPB;
import com.ftkj.proto.TeamPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class TeamInfoTestCase  extends BaseTestCase{
	
	// 连接服务器
	public TeamInfoTestCase() {
//		super("192.168.10.181", 10000003094L);
		super(IPUtil.testServerIP, 10000003094L);
	}
	
	@Test
	public void teamInfo() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.TeamManger_viewTeamInfo, TeamPB.TeamInfoData.class), 1020000003038L);
	}
	
	@Test
	public void playerInfo() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.TeamManger_viewPlayerDetail, PlayerArchivePB.TeamPlayerArchiveMainData.class), 1011000001329L);
	}

}
