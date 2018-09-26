package com.ftkj.manager.cap;

import com.ftkj.console.GymConsole;
import com.ftkj.console.LeagueConsole;
import com.ftkj.console.PlayerCardConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.gym.ArenaBean;
import com.ftkj.manager.gym.TeamArena;
import com.ftkj.manager.league.LeagueHonor;
import com.ftkj.manager.league.LeagueHonorBean;
import com.ftkj.manager.playercard.Card;
import com.ftkj.manager.playercard.TeamPlayerCard;

import java.util.List;
import java.util.Map;

/**
 * 场下攻防
 *
 * @author Jay
 * @time:2017年8月29日 上午10:38:19
 */
public class FieldCap extends CapModule {
    /**
     * 只统计现役
     * 所有已收集
     * 所有场下球员
     * 取最高品质前50，额外的都是+1点.
     */
    private Map<Integer, List<Card>> cards;
    private TeamArena ta;
    private LeagueHonor lh;
    private BaseAbility gym = new BaseAbility(AbilityType.Gym);
    private BaseAbility league = new BaseAbility(AbilityType.League);
//    private BaseAbility playerCard = new BaseAbility(AbilityType.Card);

    public FieldCap(TeamArena ta, LeagueHonor lh/*, TeamPlayerCard tpc*/) {
        super(AbilityType.Team);
        this.ta = ta;
        this.lh = lh;
//        this.cards = tpc.getCollectMap();
        // 初始化攻防计算
        initCap();
    }

    public Cap getCap() {
//    	return new Cap(gym.getCap(), league.getCap(), playerCard.getCap());
        return new Cap(gym.getCap(), league.getCap());
    }

    @Override
    public void initConfig() {
        rootNode = new CapNode(ECapModule.场下攻防, true);
        rootNode.addChildList(
                ECapModule.玩家球馆攻防加成,
                ECapModule.联盟成就攻防加成);

    }

    /**
     * 取球员的攻防
     */
    @Override
    public PlayerAbility getAbility() {
        PlayerAbility p = new PlayerAbility(this.abilityType, this.playerId);
        for (Map<EActionType, Float> map : this.capMap.values()) {
            for (EActionType type : map.keySet()) {
                p.addAttr(type, map.get(type));
            }
        }
        return p;
    }

    @Override
    protected void afterInitCap(ECapModule module, float ocap, float dcap) {
       /* if (module == ECapModule.球星卡收集张数加成) {
            playerCard.setAttr(EActionType.ocap, ocap);
            playerCard.setAttr(EActionType.dcap, dcap);
        } else */if (module == ECapModule.玩家球馆攻防加成) {
            gym.setAttr(EActionType.ocap, ocap);
            gym.setAttr(EActionType.dcap, dcap);
        } else if (module == ECapModule.联盟成就攻防加成) {
            league.setAttr(EActionType.ocap, ocap);
            league.setAttr(EActionType.dcap, dcap);
        }
    }

    public BaseAbility getGym() {
        return gym;
    }

    public BaseAbility getLeague() {
        return league;
    }

//    public BaseAbility getPlayerCard() {
//        return playerCard;
//    }

    @Override
    public float[] analysis(ECapModule module) {
        /*if (module == ECapModule.球星卡收集张数加成) {
            Cap cap = cardCap();
            return new float[]{cap.getOcap(), cap.getDcap()};
        } else */if (module == ECapModule.玩家球馆攻防加成 && ta != null) {
            ArenaBean bean = GymConsole.getArenaBean(ta.getLevel());
            return new float[]{bean.getAttack(), bean.getDefend()};
        } else if (module == ECapModule.联盟成就攻防加成 && lh != null && lh.isActivate(1)) {
            Cap cap = leagueCap();
            return new float[]{cap.getOcap(), cap.getDcap()};
        }
        return new float[]{0, 0};
    }

    private Cap leagueCap() {
        Cap cap = new Cap();
        for (int honorId : lh.getHonorMap().keySet()) {
            if (!lh.isActivate(honorId)) {
                continue;
            }
            // 按配置读攻防。
            LeagueHonorBean bean = LeagueConsole.getLeagueHonorBean(honorId, lh.getLevel(honorId));
            if (bean == null) {
                continue;
            }
            cap.setOcap(cap.getOcap() + bean.getValues()[0]);
            cap.setDcap(cap.getDcap() + bean.getValues()[1]);
        }
        return cap;
    }

//    @Deprecated
//    private Cap cardCap() {
//        Cap cap = new Cap();
//        for (int type : cards.keySet()) {
//            List<Card> list = cards.get(type);
//            if (list == null || list.size() == 0) {
//                continue;
//            }
//            int tempAtk = 0;// 当前卡组进攻
//            int tempDef = 0; // 当前卡组防守
//            int maxstarlv = PlayerCardConsole.getGroupTypeMaxStarLv(type);
//            for (int i = 0; i <= maxstarlv; i++) {
//                final int lv = i;
//                long num = list.stream().filter(c -> c.getStarLv() >= lv).count();
//                int[] cap1 = PlayerCardConsole.getGroupCollectCap(type, lv, num);
//                tempAtk += cap1[0];
//                tempDef += cap1[1];
//                //log.debug("已收集的球队数加成：卡组[{}],星级[{}],收集[{}], 进攻：{}, 防守：{}", tempType, lv, num, cap[0], cap[1]);
//            }
//            cap.setOcap(cap.getOcap() + tempAtk);
//            cap.setDcap(cap.getDcap() + tempDef);
//        }
//        return cap;
//    }

}
