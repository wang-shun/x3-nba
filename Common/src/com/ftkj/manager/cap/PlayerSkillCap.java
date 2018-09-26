package com.ftkj.manager.cap;

import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.SkillConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ECapModule;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.manager.skill.SkillTree;
import com.ftkj.manager.skill.TeamSkill;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PlayerSkillCap extends CapModule {
    private static final Logger log = LoggerFactory.getLogger(PlayerSkillCap.class);
	private TeamSkill teamSkill;
	private PlayerTalent pt;
	public PlayerSkillCap(int playerId, TeamSkill teamSkill,PlayerTalent pt) {
		super(AbilityType.Player_Skill);
		this.playerId = playerId;
		this.teamSkill = teamSkill;
		this.pt = pt;
		// 初始化攻防计算
		initCap();
	}

	@Override
	public void initConfig() {
		rootNode = new CapNode(ECapModule.球员技能, true);
		rootNode.addChildList(ECapModule.球员技能加成);
	}

	@Override
	public float[] analysis(ECapModule module) {
		float jg = 0, fs = 0 ;
		if(module == ECapModule.球员技能加成 && teamSkill != null) {
			PlayerSkill ps = teamSkill.getPlayerSkill(playerId);
			if(ps != null){
				PlayerBean  pb = PlayerConsole.getPlayerBean(playerId);
				EPlayerPosition position = pb.getPosition()[0];
				//1，3，5 被动技能
				Map<EActionType, Float> capMap = Maps.newHashMap();
				addSkillAbility(capMap, ps, playerId, position,2);
				addSkillAbility(capMap, ps, playerId, position,3);
				addSkillAbility(capMap, ps, playerId, position,5);
				//
				jg += getJg(pb, capMap, pt);
				fs += getFs(pb, capMap, pt);
			}
		}
		log.debug("技能攻防打印：jg={}, fs={}", jg, fs);
		return new float[]{jg, fs};
	}
	
	private Map<EActionType, Float> addSkillAbility(Map<EActionType, Float> capMap, PlayerSkill ps, int playerId,EPlayerPosition position, int step){
		SkillTree step1 = ps.getSkillTree(step);
		if(step1 != null){
			SkillPositionBean bean = SkillConsole.getSkillPositionBean(position,step,playerId);
			if(bean == null) {
				return capMap;
			}
			SkillBean skill = bean.getSkill1();
			if(step1.getS1()>0 && skill!=null){
				capMap.put(skill.getAddType(), capMap.getOrDefault(skill.getAddType(), 0f) + skill.getAddFloat(step1.getS1()));
			}
			SkillBean skil2 = bean.getSkill2();
			if(step1.getS2()>0 && skil2!=null){
				capMap.put(skil2.getAddType(), capMap.getOrDefault(skil2.getAddType(), 0f) + skil2.getAddFloat(step1.getS2()));
			}
			SkillBean skil3 = bean.getSkill3();
			if(step1.getS3()>0 && skil3!=null){
				capMap.put(skil3.getAddType(), capMap.getOrDefault(skil3.getAddType(), 0f) + skil3.getAddFloat(step1.getS3()));
			}
			SkillBean skil4 = bean.getSkill4();
			if(step1.getS4()>0 && skil4!=null){
				capMap.put(skil4.getAddType(), capMap.getOrDefault(skil4.getAddType(), 0f) + skil4.getAddFloat(step1.getS4()));
			}
		}
		return capMap;
	}

}
