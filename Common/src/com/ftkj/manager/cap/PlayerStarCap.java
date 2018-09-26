package com.ftkj.manager.cap;

import com.ftkj.cfg.PlayerStarBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerGrade;
import com.ftkj.manager.player.PlayerTalent;

/**
 * 球员升星
 * @author Jay
 * @time:2017年8月21日 上午11:26:58
 */
public class PlayerStarCap extends CapModule {

	/**
	 * 一套装备攻防
	 */
	private PlayerGrade playerStar;
	private PlayerBean pb;
	private PlayerTalent pt;
	public PlayerStarCap(int playerId, PlayerGrade playerStar,PlayerTalent pt) {
		super(AbilityType.Player_Star);
		this.playerId = playerId;
		this.playerStar = playerStar;
		pb = PlayerConsole.getPlayerBean(playerId);
		this.pt = pt;
		// 初始化攻防计算
		initCap();
	}
	
	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.球员升星, true);
		rootNode.addChildList(ECapModule.球员星级加成);
	}
	
	@Override
	public float[] analysis(ECapModule module) {
		float jg = 0, fs = 0 ;
		if(module == ECapModule.球员星级加成 && playerStar != null && pb != null) {
			PlayerStarBean bean = PlayerConsole.getPlayerStarGradeBean(playerStar.getStarGrade());
			jg = getJg(pb, bean.getCapMap(),pt);
			fs = getFs(pb, bean.getCapMap(),pt);
		}
		return new float[]{jg, fs};
	}
	

}
