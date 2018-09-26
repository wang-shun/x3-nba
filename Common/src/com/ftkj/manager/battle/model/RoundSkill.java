package com.ftkj.manager.battle.model;

import com.ftkj.cfg.SkillLevelBean;
import com.ftkj.console.SkillConsole;
import com.ftkj.server.GameSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tim.huang
 * 2017年9月5日
 * 回合技能信息
 */
public class RoundSkill implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final Logger log = LoggerFactory.getLogger(RoundSkill.class);

    private static final int animationRound = 6;
    private static final int animationPKRound = 13;
    private static final int responseInitRound = 7;
    private static final int responseNPCInitRound = 7;
    private static final int STATUS_Null = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_START = 2;

    private long homeTeamId;
    private long awayTeamId;
    /** 主场技能 */
    private Skill homeSkill;
    /** 客场技能 */
    private Skill awaySkill;
    /** 进攻方胜利 */
    private boolean homeWin;
    /** 开始小节 */
    private int startRound;
    /** 响应小节  初始化为-1 */
    private int initResponseRound;
    /** 会暂停推送的倒计时 */
    private AtomicInteger cd;
    /** 正常执行推送，不过当前轮次相同，则立即调用nono */
    private AtomicInteger responseRound;
    /** 是否已经计算 */
    private boolean calc = false;
    private int stat;
    /** 技能随机种子 */
    private Random random;

    public RoundSkill() {
        this.cd = new AtomicInteger(-1);
        this.responseRound = new AtomicInteger(-1);
        this.random = new Random();
        clear();
    }

    public void clear() {
        this.homeSkill = null;
        this.awaySkill = null;
        this.homeWin = false;
        this.startRound = -1;
        this.initResponseRound = -1;
        this.stat = STATUS_Null;
        this.cd.set(-1);
        this.responseRound.set(-1);
        this.calc = false;
    }

    /**
     * 使用技能
     *
     * @param skillStrategy 技能释放规则. 0或不填: 等待一段时间后释放. 1 : 立刻释放
     */
    public boolean updateSkill(Skill skill, boolean home, int skillStrategy, int roundCounter) {
        useSkill();
        boolean trigger = false;
        if (home) {
            homeSkill = skill;
        } else {
            awaySkill = skill;
        }
        if ((homeSkill != null && awaySkill != null) || skillStrategy == 1) {//响应技能，将当前小节定为响应小节
            startRound = roundCounter + 1;
            initResponseRound = roundCounter + 1;
            responseRound.set(-1);
            trigger = true;
        } else if (startRound <= 0) {//第一个使用技能
            //记录第一个技能的触发小节
            startRound = roundCounter + getResponseInitRound();
            //初始化响应技能的最晚时间
            initResponseRound = roundCounter;
        }

        log.debug("btskill up. htid {} atid {} home {} round start {} resp {} trigger {} hs {} as {}",
                homeTeamId, awayTeamId, home, startRound, initResponseRound, trigger, homeSkill, awaySkill);
        return trigger;
    }

    public Skill getSkillActionTeam(boolean seach) {
        if (!seach && this.responseRound.get() > 0) {
            return null;
        }
        if (this.homeSkill == null) {
            return this.awaySkill;
        } else if (this.awaySkill == null) {
            return this.homeSkill;
        } else {
            return homeWin ? this.homeSkill : this.awaySkill;
        }
        //		return home?getHomeSkill():getAwaySkill();
    }

    public long getTeamId() {
        //没有触发技能
        if (this.homeSkill == null && this.awaySkill == null) {
            return 0;
        }

        //判断取出进攻方
        if (this.homeSkill != null && this.homeSkill.attackSkill()) {//
            return this.homeTeamId;
        } else if (this.awaySkill == null || this.awaySkill.attackSkill()) {
            return this.awayTeamId;
        } else {
            return this.homeTeamId;
        }
    }

    public void playerAnimation() {
        if (this.homeSkill != null && this.awaySkill != null) {
            this.cd.set(animationPKRound);//
        } else {
            this.cd.set(animationRound);//
        }
        //判断谁获胜了
        if (!calc) {
            //        if (this.ok!=OK_STATUS_READY) {
            calcSkillWin();
        }
        //是否已经执行逻辑
        this.stat = STATUS_READY;
    }

    /** 技能比拼，判断出获胜的技能所属 */
    public void calcSkillWin() {
        Skill attack;
        Skill defend = null;
        boolean isHome = false;
        Skill hs = homeSkill;
        Skill as = awaySkill;

        if (hs == null) {//单技能
            attack = as;
        } else if (as == null) {//单技能
            attack = hs;
            isHome = true;
        } else if (hs.getSkill().attack()) {
            attack = hs;
            defend = as;
            isHome = true;
        } else {
            attack = as;
            defend = hs;
        }
        if (attack == null) {
            return;
        }

        SkillLevelBean attackBean = SkillConsole.getSkillLevelBean(attack.getStep(), attack.getLevel());
        SkillLevelBean defendBean = defend == null ? null : SkillConsole.getSkillLevelBean(defend.getStep(), defend.getLevel());

        int attackNum = attackBean == null ? 1 : attackBean.getS1();
        int defendNum = defendBean == null ? 0 : attack.isType() == defend.isType() ? defendBean.getS3() : defendBean.getS2();
        int total = attackNum + defendNum;
        int ran = random.nextInt(total);
        if (ran <= attackNum) {//进攻方胜利
            this.homeWin = isHome;
        } else {
            this.homeWin = !isHome;
        }
        this.calc = true;
        log.debug("btskill calcwin. ran {} onum {} dnum {} home {}", ran, attackNum, defendNum, homeWin);
    }

    public boolean isCalc() {
        return calc;
    }

    public void updateStartRound() {
        this.startRound = -1;
    }

    private int getResponseInitRound() {
        return GameSource.isNPC(this.awayTeamId) ? responseNPCInitRound : responseInitRound;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public boolean hasSkill() {
        return this.homeSkill != null || this.awaySkill != null;
    }

    public Skill getSkill(long teamId) {
        return teamId == this.homeTeamId ? this.homeSkill : this.awaySkill;
    }

    private void useSkill() {
        responseRound.set(getResponseInitRound());
    }

    int decrCD() {
        return this.cd.decrementAndGet();
    }

    int decrResponseRound() {
        return this.responseRound.decrementAndGet();
    }

    public Skill getHomeSkill() {
        return homeSkill;
    }

    public Skill getAwaySkill() {
        return awaySkill;
    }

    public int getStartRound() {
        return startRound;
    }

    public long getSkillTeamId() {
        return this.homeWin ? this.homeTeamId : this.awayTeamId;
    }

    public long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"htid\":" + homeTeamId +
                ", \"atid\":" + awayTeamId +
                ", \"hs\":" + homeSkill +
                ", \"as\":" + awaySkill +
                ", \"hw\":" + homeWin +
                ", \"sr\":" + startRound +
                //                ", \"initrr\":" + initResponseRound +
                ", \"rr\":" + responseRound +
                ", \"cd\":" + cd +
                ", \"calc\":" + calc +
                ", \"stat\":" + stat +
                '}';
    }
}
