package com.ftkj.manager.cap;

import com.ftkj.cfg.PlayerGradeBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerGrade;

public class PlayerGradeCap extends CapModule {

	private PlayerGrade playerGrade;
	
	public PlayerGradeCap(int playerId, PlayerGrade pg) {
		super(AbilityType.Player_Lev);
		this.playerId = playerId;
		this.playerGrade = pg;
		// 初始化攻防计算
		initCap();
	}

	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.球员等级, true);
		rootNode.addChildList(ECapModule.球员等级加成);
	}

	@Override
	public float[] analysis(ECapModule module) {
		float jg = 0, fs = 0 ;
		if(module == ECapModule.球员等级加成 && playerGrade != null) {
			PlayerGradeBean bean = PlayerConsole.getPlayerGradeBean(playerGrade.getGrade());
			PlayerBean player = PlayerConsole.getPlayerBean(playerGrade.getPlayerId());
			if(player != null && bean != null) {
				int[] power = bean.getPower(player.getPosition()[0]);
				jg = power[0];
				fs = power[1];
			}
		}
		return new float[]{jg, fs};
	}

}
