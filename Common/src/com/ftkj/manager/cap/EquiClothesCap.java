package com.ftkj.manager.cap;

import java.util.Collection;

import com.ftkj.console.EquiConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.equi.bean.Equi;
import com.ftkj.manager.equi.cfg.EquiBean;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;

/**
 * 球队的装备攻防计算模块
 * @author Jay
 * @time:2017年8月21日 上午11:26:58
 */
public class EquiClothesCap extends CapModule {

	/**
	 * 一套装备攻防
	 */
	private Collection<Equi> equiList;
	private PlayerBean pb;
	private PlayerTalent pt;
	public EquiClothesCap(int playerId, Collection<Equi> equiList,PlayerTalent pt) {
		super(AbilityType.Pr_Clothes);
		this.playerId = playerId;
		this.equiList = equiList;
		pb = PlayerConsole.getPlayerBean(playerId);
		this.pt = pt;
		// 初始化攻防计算
		initCap();
	}
	
	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.装备, true);
		rootNode.addChildList(ECapModule.装备基础加成, ECapModule.装备强化加成, ECapModule.装备随机加成);
	}
	
	@Override
	public float[] analysis(ECapModule module) {
		float jg = 0, fs = 0 ;
		for(Equi equi : equiList) {
			EquiBean equ = EquiConsole.getEquiBean(equi.getEquId());
			float add = 0f;
			if(module == ECapModule.装备基础加成) {
				add = 1f;
			}
			else if(module == ECapModule.装备强化加成) {
				add = EquiConsole.getEquiUpStrBean(equi.getStrLv()).getAdd();
			}
//			else if(module == ECapModule.装备升级加成) {
//				add = EquiConsole.getEquiUpLvBean(equi.getLv()).getAdd();
//			}
			jg += getJg(pb, equ.getCapMap(),pt) * add;
			fs += getFs(pb, equ.getCapMap(),pt) * add;
		}
		return new float[]{jg, fs};
	}

}
