package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

/**
 * 战斗行为配置对象
 *
 * @author tim.huang
 */
public final class BaseBattleActionBean implements BattleActionBean, Serializable {
    private static final long serialVersionUID = -7026590912072425093L;
    private final int id;
    /** 行为类型 */
    private final EBattleAction action;
    /**
     * 行为状态，成功或者失败行为
     * true为成功，false为失败
     */
    private final boolean sucessAction;
    /** 行为普通战术成功权重 */
    private final int normalTacticsWeight;
    /** 行为内线战术成功权重 */
    private final int insideTacticsWeight;
    /** 行为外线战术成功权重 */
    private final int outerTacticsWeight;
    /** 得分 */
    private final int score;
    /** 罚球次数 */
    private final int ftNum;
    /** 本行为执行后增加的动画轮数. 时长 = 轮数 * 200ms */
    private final int postRoundDelay;
    /** 执行技能后增加的动画轮数. 时长 = 轮数 * 200ms */
    private final int skillRoundDelay;
    /** 子行为列表 */
    private final ImmutableList<BaseSubActionBean> subActions;

    public BaseBattleActionBean(int id,
                                EBattleAction action,
                                int normalTacticsWeight,
                                int insideTacticsWeight,
                                int outerTacticsWeight,
                                int score,
                                int ftNum,
                                boolean status,
                                int postRoundDelay,
                                int skillRoundDelay, ImmutableList<BaseSubActionBean> subActions) {
        this.id = id;
        this.action = action;
        this.normalTacticsWeight = normalTacticsWeight;
        this.insideTacticsWeight = insideTacticsWeight;
        this.outerTacticsWeight = outerTacticsWeight;
        this.sucessAction = status;
        this.score = score;
        this.ftNum = ftNum;
        this.postRoundDelay = postRoundDelay;
        this.skillRoundDelay = skillRoundDelay;
        this.subActions = subActions;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public List<BaseSubActionBean> getSubActions() {
        return subActions;
    }

    @Override
    public int getPostRoundDelay() {
        return postRoundDelay;
    }

    @Override
    public int getSkillRoundDelay() {
        return skillRoundDelay;
    }

    @Override
    public boolean isSucessAction() {
        return sucessAction;
    }

    @Override
    public int getFtNum() {
        return ftNum;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getNormalTacticsWeight() {
        return normalTacticsWeight;
    }

    @Override
    public int getInsideTacticsWeight() {
        return insideTacticsWeight;
    }

    @Override
    public int getOuterTacticsWeight() {
        return outerTacticsWeight;
    }

    @Override
    public EBattleAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                "\"action\":" + action +
                ", \"sucess\":" + sucessAction +
                ", \"normal\":" + normalTacticsWeight +
                ", \"inside\":" + insideTacticsWeight +
                ", \"outer\":" + outerTacticsWeight +
                ", \"score\":" + score +
                ", \"ftNum\":" + ftNum +
                ", \"rd\":" + postRoundDelay +
                ", \"sd\":" + skillRoundDelay +
                ", \"subacts\":" + subActions +
                '}';
    }

    public static class RoundBuilder extends AbstractExcelBean {
        /** id */
        protected int id;
        /** 本行为执行后增加的动画轮数.         时长 = 轮数 * 200ms */
        protected int postDelay;
        /** 执行技能后增加的动画轮数.         时长 = 轮数 * 200ms */
        protected int skillDelay;
        /** 子行为列表 */
        protected ImmutableList<Integer> subActions;

        @Override
        public void initExec(RowData row) {
            this.subActions = ImmutableList.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, getSubActName())));
        }

        protected String getSubActName() {
            return "subs";
        }
    }

    public static final class Builder extends RoundBuilder {
        /** 行为类型 */
        private EBattleAction action;
        /**
         * 行为状态，成功或者失败行为.
         * 0:失败行为, 1:成功行为
         */
        private int stat;
        /** 行为普通战术成功权重 */
        private int normalWeight;
        /** 行为内线战术成功权重 */
        private int insideWeight;
        /** 行为外线战术成功权重 */
        private int outerWeight;
        /** 得分 */
        private int score;
        /** 罚球次数 */
        private int ftNum;

        @Override
        public void initExec(RowData row) {
            super.initExec(row);
            this.action = EBattleAction.convertByName(getStr(row, "type"));
        }

        @Override
		protected String getSubActName() {
            return "subs";
        }

        public List<Integer> getSubActions() {
            return subActions;
        }

        public BaseBattleActionBean build(ImmutableList<BaseSubActionBean> subActions) {
            return new BaseBattleActionBean(id,
                    action,
                    normalWeight,
                    insideWeight,
                    outerWeight,
                    score,
                    ftNum,
                    stat == 1,
                    postDelay,
                    skillDelay,
                    subActions);
        }
    }
}
