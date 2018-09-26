package com.ftkj.cfg.battle;

import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * AI 规则比赛行为要求配置.
 */
public class AIRuleActionConditionBean {
    private static final Logger log = LoggerFactory.getLogger(AIRuleActionConditionBean.class);
    /*
    教练释放技能 – 条件类型	说明	注释
    match	比赛行为(目前不支持)	比赛行为满足 N 组数据	数据组中的行为只能配置比赛提示类型的
    team	球队行为	任一球队行为满足 N 组数据	数据组中的行为只能配置球队提示类型的
    pr_any_all	任一球员行为	任一球员行为满足 N 组数据	数据组中的行为只能配置球员提示类型的
    pr_any_num	任一球员行为	任一球员行为满足 N 组数据的 vi1 个	数据组中的行为只能配置球员提示类型的
    pr_each	每个球员行为	每个球员行为满足 N 组数据	数据组中的行为只能配置球员提示类型的
    num_pr_any_num	指定个数球员行为	vi1 球员行为满足 N 组数据的 vi2 个	数据组中的行为只能配置球员提示类型的
     */

    /** AI 规则比赛行为要求类型 */
    public enum ActCondType {
        /** 比赛行为(目前不支持)	比赛行为满足 N 组数据 */
        match,
        /** 球队行为	任一球队行为满足 N 组数据 */
        team,
        /** 任一球员行为	任一球员行为满足 N 组数据 */
        pr_any_all,
        /** 任一球员行为	任一球员行为满足 N 组数据的 vi1 个 */
        pr_any_num,
        /** 每个球员行为	每个球员行为满足 N 组数据 */
        pr_each,
        /** 指定个数球员行为	vi1 球员行为满足 N 组数据的 vi2 个 */
        num_pr_any_num
    }

    public static final class AIActCondBean implements Serializable {
        private static final long serialVersionUID = -4205645450235963934L;
        /** 行为要求 id */
        private final int id;
        /** 类型 */
        private final ActCondType condType;
        /** 数据值 vi1 */
        private final int vi1;
        /** 数据值 vi2 */
        private final int vi2;
        /** 数据值 vi3 */
        private final int vi3;
        /** 比赛数据对 */
        private final Map<EActionType, ActionCondition> conditions;

        public AIActCondBean(int id, ActCondType condType, int vi1, int vi2, int vi3,
                             Map<EActionType, ActionCondition> conditions) {
            this.id = id;
            this.condType = condType;
            this.vi1 = vi1;
            this.vi2 = vi2;
            this.vi3 = vi3;
            this.conditions = conditions;
        }

        public int getId() {
            return id;
        }

        public ActCondType getCondType() {
            return condType;
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

        public Map<EActionType, ActionCondition> getConditions() {
            return conditions;
        }
    }

    public static final class Builder extends AbstractExcelBean {
        /** 行为要求 id */
        private int id;
        /** 类型 */
        private ActCondType condType;
        /** 数据值 vi1 */
        private int vi1;
        /** 数据值 vi2 */
        private int vi2;
        /** 数据值 vi3 */
        private int vi3;
        /** 比赛数据对 */
        private Map<EActionType, ActionCondition> conditions;

        public int getId() {
            return id;
        }

        @Override
        public void initExec(RowData row) {
            condType = ActCondType.valueOf(getStr(row, "cond_type").toLowerCase());
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

        public AIActCondBean build() {
            return new AIActCondBean(id, condType, vi1, vi2, vi3, conditions);
        }
    }
}
