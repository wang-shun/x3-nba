package com.ftkj.manager.battle.model;

import com.ftkj.cfg.battle.AIBean.AIRuleBean;
import com.ftkj.cfg.battle.AIBean.AIRuleGroupBean;
import com.ftkj.cfg.battle.AIBean.CoachRuleBean;
import com.ftkj.cfg.battle.AIBean.SubsituteRuleBean;
import com.ftkj.cfg.battle.AIBean.TacticRuleBean;
import com.ftkj.cfg.battle.AIBean.TacticRuleBean.Resp;
import com.ftkj.console.AIConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.subaction.CoachSkillAction;
import com.ftkj.manager.battle.subaction.TacticsAction;
import com.ftkj.util.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * AI 规则
 */
public class BattleAI implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(BattleAI.class);
    private static final long serialVersionUID = -8308538226253107341L;
    /** ai 规则配置iid */
    private int cfgRid;
    /** AI 规则配置. 根据 cfgRid 初始化 */
    private AIRuleGroupBean cfg;
    /** 当前 AI 规则. map[round, map[ruleRid, AIRule]] */
    private Map<Integer, Map<Integer, AIRule>> aiRules;

    public void setCfgRid(int cfgRid) {
        this.cfgRid = cfgRid;
    }

    public AIRuleGroupBean getCfg() {
        return cfg;
    }

    public void setCfg(AIRuleGroupBean cfg) {
        this.cfg = cfg;
    }

    private Map<Integer, AIRule> removeAiRules(int currRound) {
        return aiRules.remove(currRound);
    }

    /** 初始化 AI 规则 */
    public void init(long bid, long tid, int currRound) {
        if (cfgRid <= 0) {
            log.debug("btai init. bid {} tid {} cr {} aiid {}", bid, tid, currRound, cfgRid);
            return;
        }
        AIRuleGroupBean cfg = AIConsole.getBean(cfgRid);
        if (cfg == null) {
            log.warn("btai init. bid {} tid {} cr {} aiid {} cfg is null", bid, tid, currRound, cfgRid);
            return;
        }
        this.cfg = cfg;

        Map<Integer, Map<Integer, AIRule>> aiRules = new HashMap<>();
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        log.debug("btai init. bid {} tid {} cr {} aiid {} rule size {}",
                bid, tid, currRound, cfg.getId(), cfg.getRules().size());
        for (AIRuleBean rb : cfg.getRules()) {
            int rs = rb.getRoundStart();
            int round = rb.isRoundPeriodFixed() ? rb.getRoundPeriodMin() :
                    tlr.nextInt(rb.getRoundPeriodMin(), rb.getRoundPeriodMax());
            int triggerRound = currRound + rs + round;
            aiRules.computeIfAbsent(triggerRound, k -> new HashMap<>())
                    .put(rb.getId(), new AIRule(rb, triggerRound));
            log.debug("btai init. bid {} tid {} aiid {} rule {} cr {} rs {} rnd {} -> next {}", bid, tid, cfgRid,
                    rb.getId(), currRound, rs, round, triggerRound);
        }
        this.aiRules = aiRules;
    }

    /** 回合结束时处理 AI */
    public void handleAIPostRound(BattleSource bs,
                                  BattleTeam self,
                                  BattleTeam target,
                                  int currRound, ThreadLocalRandom tlr) {
        if (aiRules == null) {
            return;
        }
        Map<Integer, AIRule> rules = removeAiRules(currRound);
        if (rules == null) {
            return;
        }
        long bid = bs.getId();
        long tid = self.getTeamId();
        log.debug("btai trigger. bid {} tid {} round {} aiid {} rule size {}", bid, tid, currRound, cfg.getId(), rules.size());
        for (AIRule ar : rules.values()) {
            AIRuleBean rb = ar.getRuleBean();
            float chance = rb.isAlwayTrigger() ? 0f : tlr.nextFloat();
            if (log.isDebugEnabled()) {
                log.debug("btai trigger. bid {} tid {} round {} aiid {} chance {} ignore {} cfg {}", bid, tid, currRound,
                        rb.getId(), chance, rb.isIgnoreCD(), rb);
            }
            if (chance > rb.getChance()) {
                continue;
            }
            if (rb instanceof CoachRuleBean) {
                useCoachSkill(bs, tid, rb);
            } else if (rb instanceof TacticRuleBean) {
                updateTactic(bs, self, target, tlr, tid, (TacticRuleBean) rb);
            } else if (rb instanceof SubsituteRuleBean) {
                subsitutePlayers(bs, self, (SubsituteRuleBean) rb);
            }
        }

        for (AIRule ar : rules.values()) {
            AIRuleBean rb = ar.getRuleBean();
            int round = rb.isRoundPeriodFixed() ? rb.getRoundPeriodMin() :
                    tlr.nextInt(rb.getRoundPeriodMin(), rb.getRoundPeriodMax());
            int triggerRound = currRound + round;
            ar.setNextRound(triggerRound);
            ar.setNum(ar.getNum() + 1);
            aiRules.computeIfAbsent(triggerRound, k -> new HashMap<>())
                    .put(rb.getId(), ar);
            log.debug("btai next. bid {} tid {} aiid {} rule {} cr {} rnd {} -> next {}", bs.getId(), tid, cfgRid,
                    rb.getId(), currRound, round, triggerRound);
        }
    }

    private void subsitutePlayers(BattleSource bs, BattleTeam self, SubsituteRuleBean rb) {
        SubsituteRuleBean srb = rb;
        Integer subnum = srb.getResp().getOrDefault(cfg.getLev(), 0);
        if (subnum <= 0) {
            return;
        }
        List<BattlePosition> lpprs = self.getLineupPlayers().values().stream()
                .filter(bp -> bp.getPosition() != EPlayerPosition.NULL) //场上
                .filter(bp -> bp.getPlayer().getPower() <= srb.getLpPower())
                .collect(Collectors.toList());//场上要下场的球员
        if (lpprs.isEmpty()) {
            return;
        }
        List<Integer> targetPrids = new ArrayList<>(lpprs.size());
        List<EPlayerPosition> lpPos = new ArrayList<>(lpprs.size());
        for (BattlePosition bp : lpprs) {
            EPlayerPosition pos = bp.getPosition();
            BattlePlayer targetPr = self.getPlayers().stream()
                    .filter(pr -> pr.getLpPos() == EPlayerPosition.NULL && pr.getPlayerPosition() == pos) //对位球员
                    .filter(pr -> pr.getPower() >= srb.getSubPower())
                    .findFirst().orElse(null);
            if (targetPr != null) {
                lpPos.add(pos);
                targetPrids.add(targetPr.getRid());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("btai uppos. bid {} tid {} lppos {} targetPrids {}", bs.getId(), self.getTeamId(),
                    lpPos, targetPrids);
        }
        BaseBattleHandle.updatePlayerPosition(bs, self.getTeamId(),
                lpPos.subList(0, Math.min(lpPos.size(), subnum)).toArray(new EPlayerPosition[]{}),
                targetPrids.subList(0, Math.min(targetPrids.size(), subnum)),
                srb.isIgnoreCD());
    }

    private void updateTactic(BattleSource bs, BattleTeam self, BattleTeam target, ThreadLocalRandom tlr, long tid, TacticRuleBean rb) {
        TacticRuleBean trb = rb;
        Resp resp = trb.getResp();
        int rnd = tlr.nextInt(resp.getTotalWeight());
        TacticId ot = null;
        TacticId dt;
        if (rnd > 0 && rnd < resp.getDualWeight()) {//双克
            Tuple2<TacticId, TacticId> tp = getDualRestrain(target);
            ot = tp._1();
            dt = tp._2();
        } else if (rnd > resp.getDualWeight() && rnd < (resp.getDualWeight() + resp.getSingleWeight())) {//单克
            dt = getSingleRestrainDefense(target);
        } else {
            ot = self.getTacticId(getRandom(TacticType.Offense));
            dt = self.getTacticId(getRandom(TacticType.Defense));
        }
        if (log.isTraceEnabled()) {
            log.trace("btai tactic. bid {} tid {} rule {} resp {} ot {} -> {} dt {} -> {}",
                    bs.getId(), self.getTeamId(), rb.getId(), resp.getId(),
                    self.getPkTacticId(TacticType.Offense), ot, self.getPkTacticId(TacticType.Defense), dt);
        }
        if (ot == null && dt == null) {
            return;
        }
        if (ot == null) {
            ot = self.getPkTacticId(TacticType.Offense);
        }
        if (dt == null) {
            dt = self.getPkTacticId(TacticType.Defense);
        }
        ErrorCode ret = TacticsAction.updateTactic(bs, tid, TacticsConsole.getBean(ot), TacticsConsole.getBean(dt),
                rb.isIgnoreCD());
        if (log.isTraceEnabled()) {
            log.trace("btai tactic. bid {} tid {} ret {}", bs.getId(), self.getTeamId(), ret);
        }
    }

    private void useCoachSkill(BattleSource bs, long tid, AIRuleBean rb) {
        CoachRuleBean crb = (CoachRuleBean) rb;
        for (Integer skill : crb.getResp().getSkills()) {
            Tuple2<ErrorCode, List<BattleBuffer>> ret = CoachSkillAction.useCoach(bs, tid, skill, rb.isIgnoreCD());
            if (log.isTraceEnabled()) {
                log.trace("btai coach. bid {} tid {} aiid {} skill {} ret {}", bs.getId(), tid, rb.getId(), skill, ret._1());
            }
        }
    }

    public static TacticId getRandom(TacticType type) {
        TacticId id = TacticsConsole.getRandom(type).getId();
        int wc = 0;
        while (wc < 32 && (TacticId.跑轰战术.equals(id) || TacticId.全场紧逼.equals(id))) {
            wc++;
            id = TacticsConsole.getRandom(type).getId();
        }
        return id;
    }

    /** 获取单克对方的防守战术 */
    private TacticId getSingleRestrainDefense(BattleTeam target) {
        TacticId oot = target.getPkTacticId(TacticType.Offense);
        return TacticsConsole.getRestrainReverse(oot);
    }

    /** 获取双克战术 */
    private Tuple2<TacticId, TacticId> getDualRestrain(BattleTeam target) {
        TacticId oot = target.getPkTacticId(TacticType.Offense);
        TacticId odt = target.getPkTacticId(TacticType.Defense);
        TacticId byRestrainO = TacticsConsole.getRestrainReverse(oot);
        TacticId byRestrainD = TacticsConsole.getRestrainReverse(odt);
        return Tuple2.create(byRestrainO, byRestrainD);
    }

    public static final class AIRule implements Serializable {
        private static final long serialVersionUID = -5995397069025057556L;
        private final AIRuleBean ruleBean;
        /** 已经触发次数 */
        private int num;
        /** 下次触发回合 */
        private int nextRound;

        AIRule(AIRuleBean ruleBean, int nextRound) {
            this.ruleBean = ruleBean;
            this.nextRound = nextRound;
        }

        AIRuleBean getRuleBean() {
            return ruleBean;
        }

        public int getNum() {
            return num;
        }

        public int getNextRound() {
            return nextRound;
        }

        public void setNum(int num) {
            this.num = num;
        }

        void setNextRound(int nextRound) {
            this.nextRound = nextRound;
        }
    }
}
