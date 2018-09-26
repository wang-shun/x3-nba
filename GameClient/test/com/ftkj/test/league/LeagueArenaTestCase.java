package com.ftkj.test.league;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.LeagueArenaPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LeagueArenaTestCase extends BaseTestCase{
	
	// 连接服务器
	public LeagueArenaTestCase() {
		super(IPUtil.localIP, 1000003230L);
//		super(IPUtil.localIP, 10000000499L);
	}
	
	/**
	 * 主界面
	 */
	@Test
	public void showview() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueArenaManager_showView, LeagueArenaPB.LeagueArenaMain.class))
		.send(1);
	}
	
	/**
	 * 联盟排行版
	 * @param leagueId
	 */
	@Test
	public void showLeagueRankView() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueArenaManager_showLeagueRankView, LeagueArenaPB.LeagueJfRank.class))
		.send();
	}
	
	/**
	 * 本盟积分贡献榜
	 * @param leagueId
	 */
	@Test
	public void showMyLeagueJfRank() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueArenaManager_showMyLeagueJfRank, LeagueArenaPB.MyLeagueJfRank.class))
		// 联盟ID
		.send();
	}
	
	/**
	 * 上一届联盟赛排名奖励
	 * @param currweek
	 */
	@Test
	public void showLastLeagueJfRank() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueArenaManager_showLastLeagueJfRank, LeagueArenaPB.LeagueLastWeekJfRank.class))
		// 第几周
//		.send(201731);
		.send();
	}
	
	
	/**
	 * 挑战球馆
	 * @param id 球馆ＩＤ
	 * @param pos　位置
	 */
	@Test
	public void challenge() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueArenaManager_challenge))
		// * @param id 球馆ＩＤ
		// * @param pos　位置
		.send(101, 3);
	}

}
