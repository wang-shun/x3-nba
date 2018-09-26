package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.ResumeType;
import com.ftkj.manager.battle.model.DefaultBattleStep;
import com.ftkj.manager.battle.model.Round;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

public class BaseBattleBean implements Serializable {
    private static final long serialVersionUID = 6165342301959162070L;
    /** 速度. 可修改的回合取模数，每200毫秒执行一次的话，需要多少次为1回合 {@link Round#x} */
    private final int speed;
    /** 小节配置 */
    private final DefaultBattleStep steps;
    /**
     * 小节开始前延迟配置.
     * 格式:小节名称:延迟轮数,
     * 最终延迟时间=延迟轮数 * 200毫秒
     */
    private final ImmutableMap<EBattleStep, Integer> stepDelay;
    /** 是否能快速结束(0:否, 1:是) */
    private final boolean quickEnd;
    /** 技能释放规则. 0或不填: 等待一段时间后释放. 1 : 立刻释放 */
    private final int skillStrategy;
    /** 技能能量每回合比率配置 */
    private final int playerPowerBean;
    /** 球员体力每回合比率配置 */
    private final int skillPowerBean;
    /** 暂停游戏后恢复策略 */
    private final ResumeType resumeType;

    public BaseBattleBean(int speed,
                          DefaultBattleStep steps,
                          ImmutableMap<EBattleStep, Integer> stepDelay,
                          boolean quickEnd,
                          int skillStrategy,
                          int playerPowerBean,
                          int skillPowerBean,
                          ResumeType resumeType) {
        this.speed = speed;
        this.steps = steps;
        this.stepDelay = stepDelay;
        this.quickEnd = quickEnd;
        this.skillStrategy = skillStrategy;
        this.playerPowerBean = playerPowerBean;
        this.skillPowerBean = skillPowerBean;
        this.resumeType = resumeType;

    }

    public int getSpeed() {
        return speed;
    }

    public DefaultBattleStep getSteps() {
        return steps;
    }

    public ImmutableMap<EBattleStep, Integer> getStepDelay() {
        return stepDelay;
    }

    public boolean isQuickEnd() {
        return quickEnd;
    }

    public int getSkillStrategy() {
        return skillStrategy;
    }

    public int getPlayerPowerBean() {
        return playerPowerBean;
    }

    public int getSkillPowerBean() {
        return skillPowerBean;
    }

    public ResumeType getResumeType() {
        return resumeType;
    }

    public int getSpeed(BaseBattleBean ot) {
        return ot != null ? ot.getSpeed() : speed;
    }

    public DefaultBattleStep getSteps(BaseBattleBean ot) {
        return ot != null ? ot.getSteps() : steps;
    }

    @Override
    public String toString() {
        return "{" +
                "\"speed\":" + speed +
                ", \"steps\":" + steps +
                ", \"stepDelay\":" + stepDelay +
                ", \"quickEnd\":" + quickEnd +
                ", \"skillStrategy\":" + skillStrategy +
                ", \"playerPowerBean\":" + playerPowerBean +
                ", \"skillPowerBean\":" + skillPowerBean +
                ", \"resumeType\":" + resumeType +
                '}';
    }

    public static class BattleBuilder extends AbstractExcelBean {
        /** 速度. 可修改的回合取模数，每200毫秒执行一次的话，需要多少次为1回合 {@link Round#x} */
        protected int speed;
        /** 小节配置 */
        protected DefaultBattleStep steps;
        /**
         * 小节开始前延迟配置.
         * 格式:小节名称:延迟轮数,
         * 最终延迟时间=延迟轮数 * 200毫秒
         */
        protected ImmutableMap<EBattleStep, Integer> stepDelay;
        /** 是否能快速结束(0:否, 1:是) */
        protected int quickEnd;
        /** 技能释放规则.         0或不填: 等待一段时间后释放         1 : 立刻释放 */
        protected int skillStrategy;
        /** 球员回合体力分组配置 */
        protected int playerPowerGroup;
        /** 技能能量分组配置 */
        protected int skillPowerGroup;
        /**
         * 暂停游戏后恢复策略.
         * client_act : 客户端触发行为后恢复
         * manual : 手动恢复
         */
        protected String resume;

        @Override
        public void initExec(RowData row) {
            steps = BattleBean.parseSteps(getStr(row, getStepColName()));
            stepDelay = ImmutableMap.copyOf(AbstractExcelBean.splitToMap(getStr(row, getDelayColName()),
                    EBattleStep::convert, toInt));
        }

        public BaseBattleBean buildBase() {
            return new BaseBattleBean(speed,
                    steps,
                    stepDelay,
                    quickEnd == 1,
                    skillStrategy,
                    playerPowerGroup,
                    skillPowerGroup,
                    ResumeType.convertByCfgName(resume));
        }

        private String getDelayColName() {
            return "step_delay";
        }

        private String getStepColName() {
            return "step_round";
        }
    }
}
