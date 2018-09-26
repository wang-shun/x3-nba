package com.ftkj.manager.battle;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleTactics;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.util.lambda.LBInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 计算并查找可以执行行为的球员
 */
public class DefaultActionPlayerHandle implements ActionPlayerHandle {
    private static final Logger log = LoggerFactory.getLogger(DefaultActionPlayerHandle.class);

    /** 计算并查找可以执行行为的球员 */
    @Override
    public EPlayerPosition calcAndFindActPlayer(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran) {
        final EPlayerPosition pos = calcAndFindActPlayer0(type, team, otherTeam, ran);
        if (log.isDebugEnabled()) {
            log.debug("btapi weight final. 球队id {} 对方id {} 子行为 {} 最终位置 {}", team.getTeamId(), otherTeam.getTeamId(), type, pos);
            //            log.debug("btapi weight final. tid {} otid {} type {} pos {}", team.getTeamId(), otherTeam.getTeamId(), type, pos);
        }
        return pos;
    }

    /** 计算并查找可以执行行为的球员 */
    private EPlayerPosition calcAndFindActPlayer0(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran) {
        final EPlayerPosition p1 = EPlayerPosition.NULL;
        //是否强制命中球员
        if (team.getNextPlayer() != 0 && (team.getNextPlayerAction() == null || team.getNextPlayerAction() == type)) {
            BattlePosition player = team.getLineupPlayers().values().stream()
                .filter(p -> p.getPlayer().getPlayerId() == team.getNextPlayer())
                .findFirst()
                .orElse(null);
            team.setNextPlayer(0);
            team.setNextPlayerAction(null);
            if (player != null) {
                return player.getPosition();
            }
        }

        if (EActionType.pf == type || EActionType.fouled == type) {//犯规球员,特殊处理。
            return getPlayerByFoul(team, ran, p1);
        }

        //初始化球员池
        List<BattlePosition> prs = null;
        if (EActionType._3p == type) {//三分行为不命中C位置的球员
            prs = team.getLineupPlayers().values().stream()
                .filter(po -> po.getPosition() != p1)
                .filter(po -> EPlayerPosition.C != po.getPlayer().getPlayerPosition())
                .collect(Collectors.toList());
        }

        if (prs == null || prs.size() <= 0) {//球员池为空或者无球员,将场上所有球员放入池中
            prs = team.getLineupPlayers().values().stream()
                .filter(po -> po.getPosition() != p1)
                .collect(Collectors.toList());
        }
        //获得一个计算好占比的球员随机池
        final LBInt weights = new LBInt();
        final LBInt error = new LBInt();
        //		log.debug("开始计算球员行为命中情况-{}",type);
        List<PlayerPool> pool = prs.stream().map(bp -> {
            //把球员池中的球员增加偏移计算出本次行为该球员的占比
            float score;
            BattlePlayer pr = bp.getPlayer();
            if (EActionType._2p_missed == type) {//2分不中
                float ptsScore = calcWeightScore(team, otherTeam, bp, EActionType.pts);
                float fgScore = calcWeightScore(team, otherTeam, bp, EActionType.fgm);
                score = ptsScore * (1 - fgScore / 100F);
            } else if (EActionType.pts == type ||
                EActionType._3p == type ||
                EActionType.ast == type) {//对得分，三分，助攻额外计算偏移命中加成
                score = calcWeightScore(team, otherTeam, bp, type);
            } else {//其余行为不做偏移命中加成，直接获得球员的基础属性
                score = pr.getAbility().getAttrData(PlayerBean.getRealType(type));
            }
            float poscap = PlayerConsole.getPositionAPI().getCap(bp.getPosition(), pr.getPlayerPosition());
            int weight = Math.round(pr.getPower() * poscap * score);//行为权重=属性数据*体力*位置折扣
            if (weight <= 0) {
                error.increaseAndGet();
            }

            if (log.isTraceEnabled()) {
                log.trace("btapi weight pr. 球队id {} 球员 {} 体力 {} 场上位置 {} 球员位置 {} 错误削减 {} 子行为 {} 评分 {} 最终权重 {}",
                    team.getTeamId(), pr.getRid(), pr.getPower(), bp.getPosition(), pr.getPlayerPosition(), poscap, type, score, weight);
                //                log.trace("btapi weight pr. tid {} prid {} power {} pbpos {} prpos {} poscap {} type {} score {} weight {}",
                //                    team.getTeamId(), pr.getRid(), pr.getPower(), bp.getPosition(), pr.getPlayerPosition(), poscap, type, score, weight);
            }
            weights.sum(weight);
            return new PlayerPool(bp, weight);
        }).collect(Collectors.toList());

        if (weights.getVal() <= 0 && error.getVal() > 0) {
            log.trace("btapi warn weight {} error {}", weights.getVal(), error.getVal());
            return EPlayerPosition.values()[ran.nextInt(5)];
        }
        final int r = ran.nextInt(weights.getVal());
        int start = 0;
        int end = 0;
        for (PlayerPool pp : pool) {
            end += pp.getPro();
            if (r > start && r < end) {
                return pp.getPosition().getPosition();
            }
            start += pp.getPro();
        }
        //上面逻辑存在特殊情况，默认返回一个位置。保证战斗逻辑不出错
        return EPlayerPosition.values()[ran.nextInt(5)];
    }

    /** 犯规球员,特殊处理 */
    private static EPlayerPosition getPlayerByFoul(BattleTeam team, Random ran, EPlayerPosition p1) {
        //排除掉单节4犯以上的球员
        List<BattlePosition> okList = team.getLineupPlayers().values().stream()
            .filter(po -> po.getPlayer().getRealTimeActionStats().getActionValue(EActionType.pf) < 4)
            .filter(po -> po.getPosition() != p1)
            .collect(Collectors.toList());
        //取当前池中球员的概率总和
        int num = okList.stream().map(BattlePosition::getRanFGCount).reduce(0, (a, b) -> a + b);
        //命中球员
        if (num > 0) {
            int start = 0, end = 0;
            int r = ran.nextInt(num);
            for (BattlePosition pos : okList) {
                end += pos.getRanFGCount();
                if (r > start && r < end) {
                    log.debug("btapi getplayerfoul. tid {} p1 {} r {} pos {}", team.getTeamId(), p1, r, pos.getPosition());
                    return pos.getPosition();
                }
            }
        }
        //处理特殊情况，单小节所有球员都4犯。从上场球员中取一个
//        BattlePosition player = team.getLineupPlayers().values().stream()
//            .filter(po -> po.getPlayer().getRealTimeActionStats().getActionValue(EActionType.pf) < 6)
//            .findFirst()
//            .orElse(null);
//        log.debug("btapi getplayerfoul. tid {} all prs pf >= 4, get first pf < 6 {}", team.getTeamId(), player != null);
//        if (player == null) {
//            log.debug("btapi getplayerfoul. tid {} all prs pf >= 6, random pr", team.getTeamId());
//            return EPlayerPosition.values()[ran.nextInt(5)];//场上所有球员6犯
//        }
//        return player.getPosition();
        //需求修改:首发全部四犯之后在触发犯规随机一个球员(上阵球员)
        return EPlayerPosition.values()[ran.nextInt(5)];//场上所有球员6犯
    }

    /** 计算球员行为权重评分 */
    private static float calcWeightScore(BattleTeam team, BattleTeam otherTeam, BattlePosition player, EActionType type) {
        final BattleTactics ot = team.getPkTactics(TacticType.Offense);
        final BattleTactics dt = otherTeam.getPkTactics(TacticType.Defense);
        final float otCapRate = ot.getTactics().getPosCapRate(ot.getLevel(), player.getPosition(), 0f);
        final float dtCapRate = dt.getTactics().getPosCapRate(dt.getLevel(), player.getPosition(), 0f);
        final float actVal = player.getPlayer().getAbility().getAttrData(PlayerBean.getRealType(type));
        float score;
        if (EActionType.pts == type) {
            score = actVal * (1 + otCapRate);
        } else if (EActionType.fgm == type) {
            score = actVal / (1 + Math.max(0, dtCapRate));
        } else {
            score = actVal * (1 + otCapRate) / (1 + Math.max(0, dtCapRate));
        }
        if (log.isTraceEnabled()) {
            log.trace("btapi weight score. 球队id {} 进攻战术等级 {} 加成 {} 防守战术等级 {} 加成 {} 子行为 {} 真实数据 {} 评分 {}",
                team.getTeamId(), ot.getLevel(), otCapRate, dt.getLevel(), dtCapRate, type, actVal, score);
            //            log.trace("btapi weight score. tid {} ot lev {} rate {} dt lev {} rate {} type {} val {} score {}",
            //                team.getTeamId(), ot.getLevel(), otCapRate, dt.getLevel(), dtCapRate, type, actVal, score);
        }
        return score;
    }

    static class PlayerPool {
        private BattlePosition position;
        private int pro;

        PlayerPool(BattlePosition position, int pro) {
            super();
            this.position = position;
            this.pro = pro;
        }

        public BattlePosition getPosition() {
            return position;
        }

        public int getPro() {
            return pro;
        }

    }
}
