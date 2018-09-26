package com.ftkj.manager.player.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.player.Player;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年2月20日
 * 战斗战力计算
 */
public class PlayerAbilityAPI {

    /**
     * 攻防系数配置
     */
    public static Map<EActionType, Float> atkCapMap;
    public static Map<EActionType, Float> defCapMap;

    static {
        //
        atkCapMap = Maps.newHashMap();
        defCapMap = Maps.newHashMap();
        //
        atkCapMap.put(EActionType.pts, 1.2f);
        atkCapMap.put(EActionType.fgm, 0.2f);
        atkCapMap.put(EActionType.ftm, 0.5f);
        atkCapMap.put(EActionType._3pm, 0.5f);
        atkCapMap.put(EActionType.ast, 0.8f);
        atkCapMap.put(EActionType.reb, 0.3f);
        atkCapMap.put(EActionType.min, 0.5f);
        atkCapMap.put(EActionType.to, -0.5f);
        atkCapMap.put(EActionType.pf, -0.15f);
        //
        defCapMap.put(EActionType.reb, 0.7f);
        defCapMap.put(EActionType.stl, 1.3f);
        defCapMap.put(EActionType.min, 0.5f);
        defCapMap.put(EActionType.to, -0.5f);
        defCapMap.put(EActionType.pf, -0.15f);
        //
    }

    /**
     * 获得球员进攻防守属性
     *
     * @param player 球员自身基础攻防
     * @return
     */
    public static final PlayerAbility getPlayerAbility(Player player) {
        List<PlayerAbility> abilityList = Lists.newArrayList(player.getAbilitys().values());
        PlayerAbility nbaAbility = getPlayerBaseAbility(player.getPlayerRid());
        abilityList.add(nbaAbility);
        PlayerAbility ability = new PlayerAbility(AbilityType.Player, abilityList
                , PlayerConsole.getPositionAPI().getCap(player.getLineupPosition(),
                player.getPlayerPosition()));
        return ability;
    }

    //	/**
    //	 * 新接口,获取球员攻防属性,包括所有加成
    //	 * @param player
    //	 * @param otherList 加成列表
    //	 * @return
    //	 */
    //	public static final PlayerAbility getPlayerAbility(Player player, List<PlayerAbility> otherList){
    //		List<PlayerAbility> abilityList = Lists.newArrayList(player.getAbilitys().values());
    //		abilityList.addAll(otherList);
    ////		PlayerAbility nbaAbility = getPlayerBaseAbility(player.getPlayerId());
    ////		abilityList.add(nbaAbility);
    //		PlayerAbility ability = new PlayerAbility(EAbility.球员,abilityList
    //				,PlayerConsole.getPositionAPI().getCap(player.getLineupPosition(),
    //						player.getPlayerPosition()));
    //		return ability;
    //	}

    /**
     * 取球员加成攻防，比赛用
     *
     * @param player
     * @param isNPC
     * @return
     */
    public static final Map<AbilityType, PlayerAbility> getPlayerAbilityMap(Player player, boolean isNPC) {
        Map<AbilityType, PlayerAbility> map = Maps.newHashMap(player.getAbilitys());
        PlayerAbility nbaAbility = getPlayerBaseAbility(player.getPlayerRid());
        if (isNPC) {//npc球员，移除球员自身基础攻击防守
            nbaAbility.setAttr(EActionType.ocap, 0);
            nbaAbility.setAttr(EActionType.dcap, 0);
        }
        map.put(AbilityType.Player_Base, nbaAbility);
        return map;
    }

    /**
     * 取球员加成攻防，比赛用
     *
     * @param player
     * @param isNPC
     * @return
     */
    public static final Map<AbilityType, PlayerAbility> getPlayerAbilityMap(Player player, boolean isNPC, List<PlayerAbility> otherList) {
        Map<AbilityType, PlayerAbility> map = player.getAbilitys().values().stream().map(ab -> {
            PlayerAbility pa = new PlayerAbility(ab.getType(), player.getPlayerRid());
            ab.getAttrs().forEach((key, val) -> pa.addAttr(key, val));
            return pa;
        }).collect(Collectors.toMap(key -> key.getType(), val -> val));

        //		Map<EAbility,PlayerAbility> map = Maps.newHashMap(player.getAbilitys());
        //		PlayerAbility nbaAbility = getPlayerBaseAbility(player.getPlayerId());

        //		map.put(EAbility.球员基础, nbaAbility);
        if (otherList != null) {
            otherList.forEach(other -> {
                if (isNPC && other.getType() == AbilityType.Player_Base) {//npc球员，移除球员自身基础攻击防守
                    other.setAttr(EActionType.ocap, 0);
                    other.setAttr(EActionType.dcap, 0);
                }
                map.put(other.getType(), other);

            });
        }
        return map;
    }

    /**
     * 取球员基础攻防数值, 非系数结果值
     *
     * @param playerId
     * @return
     */
    public static final PlayerAbility getPlayerBaseAbility(int playerId) {
        PlayerAbility nbaAbility = new PlayerAbility(AbilityType.Player_Base, playerId);
        PlayerConsole.getPlayerBean(playerId).initAbility(nbaAbility);
        return nbaAbility;
    }
  
}
