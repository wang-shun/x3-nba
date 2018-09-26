package com.ftkj.manager.skill;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年9月11日
 * 玩家技能
 */
public class TeamSkill {
	
	/**
	 * 玩家球员技能
	 */
	private Map<Integer,PlayerSkill> playerSkillMap;
	
	public TeamSkill(List<PlayerSkill> psList){
		playerSkillMap = psList.stream().collect(Collectors.toMap(PlayerSkill::getPlayerId, key->key));
	}

	public PlayerSkill getPlayerSkill(int playerId) {
		return this.playerSkillMap.get(playerId);
	}

	public Map<Integer, PlayerSkill> getPlayerSkillMap() {
		return playerSkillMap;
	}
	
}
