package com.ftkj.cfg.battle;

import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.CompareOp;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.util.excel.RowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 比赛提示配置.
 */
public class BattleHintBean implements Serializable {
    private static final long serialVersionUID = -9107082854716975554L;
    private static final Logger log = LoggerFactory.getLogger(BattleBean.Builder.class);
    /** 触发次数. 无限次 */
    public static final int TRIGGER_UNLIMITED = -1;
    /** 比赛提示id */
    private final int id;
    /** 提示类型 */
    private final HintType hintType;
    /** 触发次数. -1表示无限次 */
    private final int triggerNum;
    /** 数据值 vi1 */
    private final int vi1;
    /** 数据值 vi2 */
    private final int vi2;
    /** 数据值 vi3 */
    private final int vi3;
    //    /** 小节类型 */
    //    private final HintStepType stepType;
    /** 小节限制 */
    private final CompareOp stepOp;
    /** 小节 */
    private final EBattleStep step;
    /** 比赛数据对 */
    private final Map<EActionType, ActionCondition> conditions;
    /** 是否在所有小节中匹配数据 */
    private final boolean matchAllSteps;

    public BattleHintBean(int id,
                          HintType hintType,
                          int triggerNum,
                          int vi1, int vi2, int vi3,
                          //                          HintStepType stepType,
                          CompareOp stepOp, EBattleStep step,
                          Map<EActionType, ActionCondition> conditions) {
        this.id = id;
        this.hintType = hintType;
        this.triggerNum = triggerNum;
        this.vi1 = vi1;
        this.vi2 = vi2;
        this.vi3 = vi3;
        this.stepOp = stepOp;
        this.step = step;
        //        this.stepType = stepType;
        this.conditions = conditions;
        matchAllSteps = (step == EBattleStep.First_Period) && (stepOp == CompareOp.GreaterEqual);
    }

    public static final class GroupBuilder extends GroupBean.Builder {
        @Override
        public String getColName() {
            return "hints";
        }
    }

    public static final class Builder extends AbstractExcelBean {
        /** 比赛提示id */
        private int id;
        /** 提示类型 */
        private HintType hintType;
        /** 触发次数. -1表示无限次 */
        private int triggerNum;
        /** 数据值 vi1 */
        private int vi1;
        /** 数据值 vi2 */
        private int vi2;
        /** 数据值 vi3 */
        private int vi3;
        //        /** 小节类型 */
        //        private HintStepType stepType;
        /** 小节限制 */
        private CompareOp stepOp;
        /** 小节名称 */
        private EBattleStep step;
        /** 比赛数据对 */
        private Map<EActionType, ActionCondition> conditions;

        public int getId() {
            return id;
        }

        @Override
        public void initExec(RowData row) {
            hintType = HintType.convert(getStr(row, "hint_type"));
            triggerNum = getInt(row, "trigger_num");
            stepOp = CompareOp.convert(getStr(row, "step_op"));
            step = EBattleStep.convert(getStr(row, "step_v"));
            //            stepType = HintStepType.convert(getStr(row, "step_type"));
            //act_name1 act_op1	act_v1
            //行为类型1 比较器1	要求值1
            //string	int	string
            this.conditions = MMatchConditionBean.parseActionConditions(row, "act_name", "act_op", "act_v",
                    act -> {
                        if (act.isDisableAct()) {
                            log.warn("策划配置. 比赛提示 {} 不支持行为类型 {} 的提示", id, act.getConfigName());
                        }
                        return !act.isDisableAct();
                    });//过滤掉不支持的行为
        }

        public BattleHintBean build() {
            return new BattleHintBean(id, hintType, triggerNum, vi1, vi2, vi3,
                    //                    stepType,
                    stepOp, step, conditions);
        }
    }

    //    /**
    //     * 提示 - 小节类型	说明
    //     * start	小节的开始
    //     * end	小节的结束
    //     * whole	整个小节
    //     */
    //    public enum HintStepType {
    //        Start("start"),
    //        End("end"),
    //        Whole("whole"),
    //        //
    //        ;
    //        private String cfgName;
    //
    //        HintStepType(String cfgName) {
    //            this.cfgName = cfgName;
    //        }
    //
    //        public static final Map<String, HintStepType> nameCaches = new HashMap<>();
    //
    //        static {
    //            for (HintStepType t : values()) {
    //                if (nameCaches.containsKey(t.cfgName)) {
    //                    throw new Error("duplicate cfg name :" + t.cfgName);
    //                }
    //                nameCaches.put(t.cfgName, t);
    //            }
    //        }
    //
    //        public static HintStepType convert(String cfgName) {
    //            return nameCaches.get(cfgName);
    //        }
    //    }

    /**
     * 提示类型
     * 提示 – 提示类型	说明	注释
     * match	比赛行为	比赛行为满足 N 组数据
     * team	球队行为(目前不支持)
     * pr_any_all	双方任一球员行为	任一球员行为满足 N 组数据
     * pr_any_num	双方任一球员行为	任一球员行为满足 N 组数据的 vi1 个
     * pr_each	双方每个球员行为	每个球员行为满足 N 组数据
     * mvp	mvp
     */
    public enum HintType {
        Match("match"),
        Team("team"),
        Player_Any_All("pr_any_all"),
        Player_Any_Num("pr_any_num"),
        Player_Each("pr_each"),
        Mvp("mvp")
        //
        ;
        private String cfgName;

        HintType(String cfgName) {
            this.cfgName = cfgName;
        }

        public static final Map<String, HintType> nameCaches = new HashMap<>();

        static {
            for (HintType t : values()) {
                if (nameCaches.containsKey(t.cfgName)) {
                    throw new Error("duplicate cfg name :" + t.cfgName);
                }
                nameCaches.put(t.cfgName, t);
            }
        }

        public static HintType convert(String cfgName) {
            return nameCaches.get(cfgName);
        }

        @Override
        public String toString() {
            return cfgName;
        }
    }

    public int getId() {
        return id;
    }

    public HintType getHintType() {
        return hintType;
    }

    public int getTriggerNum() {
        return triggerNum;
    }

    /** 是否无限次触发 */
    public boolean isTriggerUnlimited() {
        return triggerNum == TRIGGER_UNLIMITED;
    }

    /** 是否是有限次触发 */
    public boolean isTriggerLimited() {
        return triggerNum != TRIGGER_UNLIMITED;
    }

    /** 是否只触发1次 */
    public boolean isTriggerOnce() {
        return triggerNum == 1;
    }

    public int getVi1() {
        return vi1;
    }

    public int getVi2() {
        return vi2;
    }

    public int getVi3() {
        return vi3;
    }

    //    public HintStepType getStepType() {
    //        return stepType;
    //    }

    public CompareOp getStepOp() {
        return stepOp;
    }

    public EBattleStep getStep() {
        return step;
    }

    /** other 是否满足配置的 step 和 stepOp 要求 */
    public boolean compareStep(EBattleStep other) {
        return stepOp.compare(other.getType(), step.getType());
    }

    /** 当前提示是否是在整场比赛中提示(不在特定一节中) */
    public boolean isMatchAllSteps() {
        return matchAllSteps;
    }

    public Map<EActionType, ActionCondition> getConditions() {
        return conditions;
    }
}
