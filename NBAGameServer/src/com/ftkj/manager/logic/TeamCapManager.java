package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.cap.Cap;
import com.ftkj.manager.cap.CapModule;
import com.ftkj.manager.cap.CardCap;
import com.ftkj.manager.cap.EquiCap;
import com.ftkj.manager.cap.EquiClothesCap;
import com.ftkj.manager.cap.FieldCap;
import com.ftkj.manager.cap.LeagueTrainCap;
import com.ftkj.manager.cap.PlayerCap;
import com.ftkj.manager.cap.PlayerGradeCap;
import com.ftkj.manager.cap.PlayerSkillCap;
import com.ftkj.manager.cap.PlayerStarCap;
import com.ftkj.manager.equi.TeamEqui;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.playercard.TeamPlayerCard;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 攻防计算模块
 *
 * @author Jay
 * @author luch
 */
public class TeamCapManager extends BaseManager {
    @IOC
    private PlayerManager playerManager;
    @IOC
    private EquiManager teamEquiManager;
    @IOC
    private PlayerCardManager playerCardManager; 
    @IOC
    private PlayerGradeManager playerGradeManager;
    @IOC
    private SkillManager skillManager;
    @IOC
    private GymManager localArenaManager;
    @IOC
    private LeagueHonorManager leagueHonorManager;
    @IOC
    private StarletManager starletManager;
    @IOC
    private TrainManager trainManager;

    @Override
    public void instanceAfter() {
    }

    /**
     * 取首发攻防，比赛用
     * 返回对象，可以单独取进攻，或者防守
     * A攻击 + B攻击 + C攻击 + D攻击 + E攻击 + 场下攻击
     */
    List<PlayerAbility> getStartingAbilities(long teamId) {
        // 主力球员
        List<PlayerAbility> list = Lists.newArrayList();
        for (Player player : playerManager.getTeamPlayer(teamId).getStartingPlayers()) {
            list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId)));
        }
        // 场下
        list.add(getTeamOtherCap(teamId).getAbility());
        return list;
    }

    /**
     * 首发攻防(不包括场下)
     */
    Cap getStartingTotalCap(long teamId) {
        Cap cap = new Cap();
        List<PlayerAbility> abilities = getStartingCap(teamId);
        abilities.forEach(a -> {
            cap.setOcap(cap.getOcap() + a.getAttrData(EActionType.ocap));
            cap.setDcap(cap.getDcap() + a.getAttrData(EActionType.dcap));
        });
        return cap;
    }

    /**
     * 首发攻防(不包括场下)
     */
    List<PlayerAbility> getStartingCap(long teamId) {
        List<PlayerAbility> list = Lists.newArrayList();
        for (Player player : playerManager.getTeamPlayer(teamId).getStartingPlayers()) {
            list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId)));
        }
        return list;
    }

    /**
     * 替补攻防(不包括场下)
     */
    Cap getBenchTotalCap(long teamId) {
        Cap cap = new Cap();
        List<PlayerAbility> abilities = getBenchCap(teamId);
        abilities.forEach(a -> {
            cap.setOcap(cap.getOcap() + a.getAttrData(EActionType.ocap));
            cap.setDcap(cap.getDcap() + a.getAttrData(EActionType.dcap));
        });
        return cap;
    }

    /**
     * 替补攻防(不包括场下)
     */
    List<PlayerAbility> getBenchCap(long teamId) {
        List<PlayerAbility> list = Lists.newArrayList();
        for (Player player : playerManager.getTeamPlayer(teamId).getBenchPlayers()) {
            list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId)));
        }
        return list;
    }

    /**
     * 取全队攻防，前台显示面板用，和前台攻防一致，包括替补球员攻防
     */
    public List<PlayerAbility> getTeamAllPlayerAbilities(long teamId) {
        // 阵容球员
        List<PlayerAbility> list = Lists.newArrayList();
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        for (Player player : tp.getPlayers()) {
            list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId)));
        }
        //        //替补球员
        //        for (Player player : tp.getPlayers()) {
        //            if (player.getLineupPosition() != EPlayerPosition.NULL) {
        //                list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent()));
        //            }
        //        }
        // 场下攻防
        list.add(getTeamOtherCap(teamId).getAbility());
        return list;
    }

    /**
     * 取全队(首发+替补)攻防，前台显示面板用，和前台攻防一致，包括替补球员攻防
     */
    public List<PlayerAbility> getLineupAbilities(long teamId) {
        // 阵容球员
        List<PlayerAbility> list = Lists.newArrayList();
        for (Player player : playerManager.getTeamPlayer(teamId).getPlayers()) {
            list.addAll(getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId)));
        }
        return list;
    }

    /**
     * 装备攻防测试
     */
    List<CapModule> getTestEquiCap(long teamId) {
        List<CapModule> list = Lists.newArrayList();
        for (Player player : playerManager.getTeamPlayer(teamId).getPlayers()) {
            list.add(getEquiCap(teamId, player.getPlayerRid(), player.getPlayerTalent()));
        }
        return list;
    }

    /**
     * 场下攻防
     */
    FieldCap getTeamOtherCap(long teamId) {
        return new FieldCap(
                localArenaManager.getTeamArena(teamId),
                leagueHonorManager.getCalAbilityLeagueHonor(teamId)
               //,playerCardManager.getTeamPlayerCard(teamId)
        );
    }

    /**
     * 取球员的进攻防守
     * 返回对象，包括进攻和防守
     */
    private List<PlayerAbility> getPlayerCap(long teamId, int playerId, PlayerTalent pt, TeamPlayerCard tpc) {
        return Arrays.asList(getPlayerBaseCap(playerId, pt).getAbility(),
                getEquiCap(teamId, playerId, pt).getAbility(),
                getEquiClothesCap(teamId, playerId, pt).getAbility(),               
                getPlayerGradeCap(teamId, playerId, pt).getAbility(),
                getPlayerStarCap(teamId, playerId, pt).getAbility(),
                getPlayerSkillCap(teamId, playerId, pt).getAbility(),
                getPlayerCardCap(tpc, playerId).getAbility(),
                getLeagueTrainCap(teamId, playerId).getAbility()
        		);
    }

    //    public List<CapModule> getPlayerCapModule(long teamId, int playerId, PlayerTalent pt) {
    //        List<CapModule> capList = Arrays.asList(getPlayerBaseCap(playerId, pt),
    //                getEquiCap(teamId, playerId, pt),
    //                getEquiClothesCap(teamId, playerId, pt),
    //                getPlayerTrainCap(teamId, playerId, pt),
    //                getPlayerGradeCap(teamId, playerId, pt),
    //                getPlayerStarCap(teamId, playerId, pt),
    //                getPlayerSkillCap(teamId, playerId, pt));
    //        return capList;
    //    }

    /**
     * 取球员的总攻防加成
     */
    PlayerAbility getPlayerAllCap(long teamId, int playerId, PlayerTalent pt) {
        List<PlayerAbility> list = getPlayerCap(teamId, playerId, pt, playerCardManager.getTeamPlayerCard(teamId));
        PlayerAbility p = new PlayerAbility(AbilityType.Player, playerId);
        for (PlayerAbility pa : list) {
            p.addAttr(EActionType.ocap, pa.getAttrData(EActionType.ocap));
            p.addAttr(EActionType.dcap, pa.getAttrData(EActionType.dcap));
        }
        return p;
    }

    Map<Integer, List<PlayerAbility>> getPlayerCap(long teamId, List<Player> players) {
        Map<Integer, List<PlayerAbility>> result = Maps.newHashMap();
        players.forEach(player ->
                result.put(player.getPlayerRid(), getPlayerCap(teamId, player.getPlayerRid(), player.getPlayerTalent(), playerCardManager.getTeamPlayerCard(teamId))));
        return result;
    }

    /**
     * 球员基础攻防(经过天赋加成后) * 系数
     */
    private CapModule getPlayerBaseCap(int playerId, PlayerTalent pt) {
        return new PlayerCap(playerId, pt);
    }

    /**
     * 装备模块攻防
     * 不包括球衣
     */
    private CapModule getEquiCap(long teamId, int playerId, PlayerTalent pt) {
        TeamEqui te = teamEquiManager.getTeamEqui(teamId);
        return new EquiCap(playerId, te.getPlayerEqui(playerId).getPlayerEqui(), pt);
    }

    /**
     * 球衣攻防
     */
    private CapModule getEquiClothesCap(long teamId, int playerId, PlayerTalent pt) {
        TeamEqui te = teamEquiManager.getTeamEqui(teamId);
        return new EquiClothesCap(playerId, te.getPlayerClothes(playerId).getPlayerEqui(), pt);
    }

    /**
     * 球员等级攻防
     */
    private CapModule getPlayerGradeCap(long teamId, int playerId, PlayerTalent pt) {
        return new PlayerGradeCap(playerId, playerGradeManager.getTeamPlayerGrade(teamId).getPlayerGrade(playerId));
    }

    /**
     * 球员升星攻防
     */
    private CapModule getPlayerStarCap(long teamId, int playerId, PlayerTalent pt) {
        return new PlayerStarCap(playerId, playerGradeManager.getTeamPlayerGrade(teamId).getPlayerGrade(playerId), pt);
    }

    /**
     * 球员技能攻防加成
     */
    private CapModule getPlayerSkillCap(long teamId, int playerId, PlayerTalent pt) {
        return new PlayerSkillCap(playerId, skillManager.getTeamSkill(teamId), pt);
    }

    /**
     * 球员的球星卡加成
     * @param teamId
     * @param playerId
     * @return
     */
    public CapModule getPlayerCardCap(TeamPlayerCard tpc, int playerId) {
    	return new CardCap(tpc, playerId, starletManager.getStarletTeamRedixValueMap(tpc.getTeamId()) != null?starletManager.getStarletTeamRedixValueMap(tpc.getTeamId()):Maps.newConcurrentMap());
    }
    
    /**
     * 联盟训练馆加成
     * @param teamId
     * @param playerId
     * @return
     */
    public CapModule getLeagueTrainCap(long teamId, int playerId) {
        return new LeagueTrainCap(playerId, trainManager.getLeagueTrainCap(teamId, playerId));
    }
}
