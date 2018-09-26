package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.subaction.RebAction.RandomRebAction;

import java.util.EnumMap;
import java.util.Map;

/**
 * 子行为处理
 */
public class SubActionFactory {

    private static Map<EActionType, SubAction> subActions = new EnumMap<>(EActionType.class);

    static {//初始化配置信息
        subActions.put(EActionType.pts, new PtsAction(EActionType.pts));
        subActions.put(EActionType._3p, new ThreePtsAction(EActionType._3p));
        subActions.put(EActionType.to, new ToAction(EActionType.to));
        subActions.put(EActionType.pf, new PfAction(EActionType.pf));
        subActions.put(EActionType.fouled, new FouledAction(EActionType.fouled));
        subActions.put(EActionType.blk, new BlkAction(EActionType.blk));
        subActions.put(EActionType.stl, new StlAction(EActionType.stl));
        subActions.put(EActionType.ast, new AstAction(EActionType.ast));
        subActions.put(EActionType.ft_act, new FtAction(EActionType.ft_act));
        subActions.put(EActionType.reb, new RandomRebAction(EActionType.reb));
        subActions.put(EActionType._2p_missed, new MissAction(EActionType._2p_missed));
        subActions.put(EActionType.Change_Possession, new PossessionAction(EActionType.Change_Possession));
        subActions.put(EActionType.tip_in, new TipInAction(EActionType.tip_in));
        subActions.put(EActionType.Pause, new PauseAction(EActionType.Pause));
        //        subActions.put(EActionType.skill, new PlayerSkillAction(EActionType.skill)); //目前不支持球员技能, 需要放到父行为生成
        subActions.put(EActionType.change_tactics, new TacticsAction(EActionType.change_tactics));
        subActions.put(EActionType.substitute, new SubstituteAction(EActionType.substitute));
        subActions.put(EActionType.Coach_Skill, new CoachSkillAction(EActionType.Coach_Skill));

    }

    public static SubAction getAction(EActionType type) {
        return subActions.get(type);
    }

    public static void main(String[] args) {
        String str = "";
        for (EActionType act : subActions.keySet()) {
            str += act.getConfigName() + " : " + act.getComment() + "\n";
        }
        System.out.println(str);
    }
}
