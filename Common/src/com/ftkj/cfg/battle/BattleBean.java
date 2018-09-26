package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.battle.BaseBattleBean.BattleBuilder;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.enums.battle.ResumeType;
import com.ftkj.manager.battle.model.DefaultBattleStep;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.BitUtil;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** 比赛配置 */
public class BattleBean implements Serializable {
    private static final long serialVersionUID = -7863058414127739865L;
    /** 最长比赛时间 10分钟 */
    public static final int MAX_MATCH_TIME_SEC = 10 * 60;
    /** 比赛结束时是否发放战术点和经验奖励 */
    public static final int Attr_Enable_End_Tactic_Exp = 1;
    private final BaseBattleBean baseBean;
    /** 比赛类型 */
    private final EBattleType type;
    /** 最长比赛时间(秒). */
    private final int maxMatchTime;
    /** 提示 */
    private final GroupBean hintGroup;
    /** 提示配置. map[actionType, set[hint.id]] */
    private final ImmutableMap<EActionType, ImmutableSet<Integer>> hints;
    /** 父行为 */
    private final BattleActionsBean actions;
    /** AI 规则id */
    private final int aiGroupId;
    /** 胜利的奖励(道具) */
    private final List<PropSimple> winProps;
    /** 胜利的奖励(掉落dropId,dropId,dropId) */
    private final String winDrop;
    /** 失败的奖励(道具) */
    private final List<PropSimple> loseProps;
    /** 失败的奖励(掉落dropId,dropId,dropId) */
    private final String loseDrop;
    /** 比赛属性配置, 按位运算. */
    private final long attr;

    public BattleBean(BaseBattleBean baseBean,
                      EBattleType type,
                      int maxMatchTime,
                      GroupBean hintGroup,
                      ImmutableMap<EActionType, ImmutableSet<Integer>> hints,
                      BattleActionsBean actions,
                      int aiGroupId,
                      List<PropSimple> winProps,
                      String winDrop,
                      List<PropSimple> loseProps,
                      String loseDrop,
                      long attr) {
        this.baseBean = baseBean;
        this.type = type;
        this.maxMatchTime = maxMatchTime;
        this.hintGroup = hintGroup;
        this.hints = hints;
        this.actions = actions;
        this.aiGroupId = aiGroupId;
        this.winProps = winProps;
        this.winDrop = winDrop;
        this.loseProps = loseProps;
        this.loseDrop = loseDrop;
        this.attr = attr;
    }

    public static final class Builder extends BattleBuilder {
        /** 比赛类型 */
        private int type;
        /** 相同比赛类型关联列表.给多人赛用. */
        private List<Integer> children = Collections.emptyList();
        /** 最长比赛时间(秒). */
        private int maxMatchTime;
        /** 比赛提示分组配置 */
        private int hintGroup;
        /** 父行为分组配置 */
        private int actionGroup;
        /** AI 规则id */
        private int aiGroupId;
        /** 胜利奖励（掉落） */
        private String winDrop;
        /** 胜利奖励（道具列表） */
        private String winProp;
        /** 失败奖励（掉落） */
        private String loseDrop;
        /** 失败奖励（道具列表） */
        private String loseProp;
        /** 比赛属性配置, 按位运算. */
        private long attr;

        @Override
        public void initExec(RowData row) {
            super.initExec(row);
            if (getBoolean(row, "endTacticExp")) {
                attr = BitUtil.addBit(attr, Attr_Enable_End_Tactic_Exp);
            }
            children = ImmutableList.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "children_")));
        }

        public BattleBean build(BattleActionsBean actions,
                                GroupBean hintGroup,
                                ImmutableMap<EActionType, ImmutableSet<Integer>> sts) {
            if (maxMatchTime <= 0) {
                maxMatchTime = MAX_MATCH_TIME_SEC;
            }
            return new BattleBean(buildBase(),
                    EBattleType.getBattleType(type),
                    maxMatchTime,
                    hintGroup,
                    sts,
                    actions,
                    aiGroupId,
                    PropSimple.parseItems(winProp),
                    winDrop,
                    PropSimple.parseItems(loseProp),
                    loseDrop,
                    attr);
        }

        public int getType() {
            return type;
        }

        public int getHintGroup() {
            return hintGroup;
        }

        public int getSkillPowerGroup() {
            return skillPowerGroup;
        }

        public int getPlayerPowerGroup() {
            return playerPowerGroup;
        }

        public int getActionGroup() {
            return actionGroup;
        }

        public List<Integer> getChildren() {
            return children;
        }
    }

    public BaseBattleBean getBaseBean() {
        return baseBean;
    }

    public int getSpeed() {
        return baseBean.getSpeed();
    }

    public DefaultBattleStep getSteps() {
        return baseBean.getSteps();
    }

    public ImmutableMap<EBattleStep, Integer> getStepDelay() {
        return baseBean.getStepDelay();
    }

    public boolean isQuickEnd() {
        return baseBean.isQuickEnd();
    }

    public int getSkillStrategy() {
        return baseBean.getSkillStrategy();
    }

    public int getPlayerPowerBean() {
        return baseBean.getPlayerPowerBean();
    }

    public int getSkillPowerBean() {
        return baseBean.getSkillPowerBean();
    }

    public ResumeType getResumeType() {
        return baseBean.getResumeType();
    }

    public int getSpeed(BaseBattleBean ot) {
        return baseBean.getSpeed(ot);
    }

    public DefaultBattleStep getSteps(BaseBattleBean ot) {
        return baseBean.getSteps(ot);
    }

    public EBattleType getType() {
        return type;
    }

    public int getMaxMatchTime() {
        return maxMatchTime;
    }

    public GroupBean getHintGroup() {
        return hintGroup;
    }

    public ImmutableMap<EActionType, ImmutableSet<Integer>> getHints() {
        return hints;
    }

    public int getAiGroupId() {
        return aiGroupId;
    }

    public List<PropSimple> getWinProps() {
        return winProps;
    }

    public String getWinDrop() {
        return winDrop;
    }

    public List<PropSimple> getLoseProps() {
        return loseProps;
    }

    public String getLoseDrop() {
        return loseDrop;
    }
    
    public static List<Integer> getDropIds(String dropIdStr){
      List<Integer> list = new ArrayList<>();
      if(dropIdStr != null && !"".equals(dropIdStr)) {
        String[] arr = dropIdStr.split(",");
        for(String dropId : arr) {
          list.add(Integer.parseInt(dropId));
        }
      }
      return list;
    }

    /** 解析回合配置(留空则使用默认配置) */
    public static List<BattleStepBean> parseStep(String str) {
        return parseSteps0(str);
    }

    /** 解析回合配置(留空则使用默认配置) */
    public static DefaultBattleStep parseSteps(String str) {
        return new DefaultBattleStep(parseSteps0(str));
    }

    /** 解析回合配置(留空则使用默认配置) */
    public static List<BattleStepBean> parseSteps0(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Integer> stepAndNum = AbstractExcelBean.splitToStrIntMap(str);
        List<BattleStepBean> steps = new ArrayList<>();
        int lower = 0;
        for (Map.Entry<String, Integer> e : stepAndNum.entrySet()) {
            EBattleStep type = EBattleStep.convert(e.getKey());
            int upper = lower + e.getValue();
            steps.add(new BattleStepBean(type, e.getValue(), new IntervalInt(lower + 1, upper)));
            lower = upper;
        }
        steps.sort(Comparator.comparingInt(o -> o.getStep().getType()));
        return ImmutableList.copyOf(steps);
    }

    public BattleActionsBean getActions() {
        return actions;
    }

    public long getAttr() {
        return attr;
    }

    public boolean isEnableEndTacticExp() {
        return BitUtil.hasBit(attr, Attr_Enable_End_Tactic_Exp);
    }

    @Override
    public String toString() {
        return "{" +
                "\"baseBean\":" + baseBean +
                ", \"type\":" + type +
                ", \"maxMatchTime\":" + maxMatchTime +
                ", \"hintGroup\":" + hintGroup +
                ", \"hints\":" + hints +
                ", \"actions\":" + actions +
                ", \"aiGroupId\":" + aiGroupId +
                ", \"winProps\":" + winProps +
                ", \"winDrop\":" + winDrop +
                ", \"loseProps\":" + loseProps +
                ", \"loseDrop\":" + loseDrop +
                '}';
    }

    /** 父行为分组配置 */
    public static final class ActionGroupBuilder extends GroupBean.Builder {
        @Override
        public String getColName() {
            return "actions";
        }
    }
}
