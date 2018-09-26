package com.ftkj.manager.cap;

import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;

import com.ftkj.cfg.card.PlayerCardGroupBean;
import com.ftkj.console.PlayerCardConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.playercard.TeamPlayerCard;

public class CardCap extends CapModule {

	private TeamPlayerCard tpc;
	private int playerId;
	private Map<Integer, Integer> cardGroupCapMap;
	private Map<Integer, Integer> otherCap;
	
	/**
	 * 
	 * @param tpc
	 * @param playerId
	 * @param otherCap 新秀卡的额外加成
	 */
	public CardCap(TeamPlayerCard tpc, int playerId, Map<Integer, Integer> otherCap) { 
		super(AbilityType.Card);
		this.tpc = tpc;
		this.playerId = playerId;
		this.otherCap = otherCap;
		this.cardGroupCapMap = Maps.newHashMap();
		// 初始化卡组攻防，提高计算效率
		initGroupCapMap();
		initCap();
	}

	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.球星卡, true);
		rootNode.addChildList(ECapModule.球星卡球员加成);
	}
	
	private void initGroupCapMap() {
		for(int type : tpc.getCollectMap().keySet()) {
			PlayerCardGroupBean bean = PlayerCardConsole.getPlayerCardGroup(type);
			if(bean.getRate() == 0) {
				continue;
			}
			// 
			int groupTotal = tpc.getGroupCap(type);
			this.cardGroupCapMap.put(type, groupTotal);
			//System.err.println("卡组：" + type +"  技术总和:" + groupTotal);
		}
	}

	@Override
	public float[] analysis(ECapModule module) {
		float jg =0, fs = 0;
		if(module != ECapModule.球星卡球员加成) {
			return new float[] {jg, fs};
		}
		for(int type : cardGroupCapMap.keySet()) {
		    int totalCap = cardGroupCapMap.get(type) + (otherCap.get(type) != null ?otherCap.get(type):0);		
		    //System.err.println("卡组：" + type +"  技术总和:" + cardGroupCapMap.get(type) + "额外加成 :" + (otherCap.get(type) != null ?otherCap.get(type):0));
			float[] caps =  tpc.getPlayerCardCap(type, totalCap, playerId);
			jg += caps[0];
			fs += caps[1];
		}
		return new float[] {jg, fs};
	}

}
