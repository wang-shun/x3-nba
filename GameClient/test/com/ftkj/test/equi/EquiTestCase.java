package com.ftkj.test.equi;


import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.enums.equi.EEquiType;
import com.ftkj.proto.TeamEquiPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class EquiTestCase extends BaseTestCase{
	
	public EquiTestCase() {
//		super("121.10.118.38", 10000000544L);
		super(IPUtil.testServerIP, 1000001329L);
	}
	
	@Test
	public void equiList() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.EquiManager_showEquiList, TeamEquiPB.TeamEquiData.class));
	}
	
	@Test
	public void equiTran() {
		// 球员ID，套装ID，目标球员
		this.robot.action(DefaultAction.instanceService(ServiceCode.EquiManager_equiTransfer, TeamEquiPB.TeamEquiData.class))
//				.send(3451, 1, 3032980);
				.send(3032980, 2, 3201);
	}
	
	/**
	 * 升级
	 */
	@Test
	public void upLv() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.EquiManager_upLv), 4298, EEquiType.头带.id);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Lv), 6460, EEquiType.护腕.type);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Lv), 6460, EEquiType.球衣.type);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Lv), 6460, EEquiType.护膝.type);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Lv), 6460, EEquiType.球鞋.type);
	}
	
	/**
	 * 进阶通用
	 */
	@Test
	public void upQua() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.EquiManager_upQuality), 6460, EEquiType.头带.id, 1006, 1006, 1006);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Qua), 6460, EEquiType.护腕.type, 1012, 1012, 1012);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Qua), 6460, EEquiType.球衣.type, 1006, 1006, 1006);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Qua), 6460, EEquiType.护膝.type, 1015, 1015, 1015);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Qua), 6460, EEquiType.球鞋.type, 1018, 1018, 1018);
	}
	
	/**
	 * 强化
	 */
	@Test
	public void test() {
		// 球员ID，装备类型， 强化道具1，2，3
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.EquiManager_upStrLv), 9000266, EEquiType.头带.id, 1003, -1, -1);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Str), 6460, EEquiType.护腕.type, 1003, 1003, 1003);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Str), 6460, EEquiType.球衣.type, 1003, 1003, 1003);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Str), 6460, EEquiType.护膝.type, 1003, 1003, 1003);
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Team_Equi_Up_Str), 6460, EEquiType.球鞋.type, 1003, 1003, 1003);
	}
	
	/**
	 * 球衣进阶
	 */
	@Test
	public void cloQua() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.EquiManager_upQuaClothes), 9000266, 1020);
	}

}
