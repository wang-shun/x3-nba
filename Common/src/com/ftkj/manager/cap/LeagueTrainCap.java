package com.ftkj.manager.cap;


import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.player.api.PlayerAbilityAPI;


/**
 * 球员升星
 * @author Jay
 * @time:2017年8月21日 上午11:26:58
 */
public class LeagueTrainCap extends CapModule {

	private int capRate;
	private int playerId;
	public LeagueTrainCap(int playerId, int capRate) {
		super(AbilityType.League_Train);
		this.playerId = playerId;
		this.capRate = capRate;
		// 初始化攻防计算
		initCap();
	}
	
	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.联盟训练馆, true);
		rootNode.addChildList(ECapModule.联盟训练馆加成);
	}
	
	@Override
	public float[] analysis(ECapModule module) {
		float jg = 0, fs = 0 ;
		if(module == ECapModule.联盟训练馆加成) {
		    PlayerAbility playerAbility = PlayerAbilityAPI.getPlayerBaseAbility(playerId);
			jg = (float)(playerAbility.get(EActionType.ocap) * (this.capRate * 0.01));
			fs = (float)(playerAbility.get(EActionType.dcap) * (this.capRate * 0.01));
		}
		return new float[]{jg, fs};
	}
	

}
