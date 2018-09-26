package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.RoundReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_罚球
 */
public class FtAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(FtAction.class);

    public FtAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        //取该行为的球员
        RoundReport report = ctx.report();
        BattlePosition op = report.getOffensePlayer();
        BattlePosition bp;
        if (op == null) {
            bp = calcAndFindActPlayer(ctx);
            report.setOffensePlayer(bp);
        } else {
            bp = op;
            ctx.setBp(bp);
        }
        final int ftNum = ctx.bean().getFtNum();//得多少分罚多少球
        BattlePlayer pr = bp.getPlayer();
        //扣除体力,将体力变更放入回合战报行为中
        BattleSource bs = ctx.bs();
        if (ftNum != 0) {//每次罚球出手扣1体力
            bs.stats().upPower(pr, pr.calcPower(-ftNum * bs.getInfo().roundPower(EActionType.ft_act)));
        }
        int scoreFt = 0;
        boolean triggerReb = true;//是否需要抢篮板决定球权
        final float ftm = pr.getPlayer().getAbility(EActionType.ftm);
        for (int i = 1; i <= ftNum; i++) {
            boolean trigger = isTriggerByChance(ctx, (int) ftm);
            if (trigger) {//投篮命中
                scoreFt++;//得分+1
                if (i == ftNum) {
                    triggerReb = false;//最后一个求命中，不抢篮板
                }
            }
        }
        updatePlayerActStat(ctx, EActionType.fta, ctx.bean().getFtNum());
        final BattleTeam ball = ctx.ball();
        if (scoreFt > 0) {
            ctx.bs().updateScore(ctx.bs().getTeam(pr.getTid()),
                    ctx.bs().getOtherTeam(pr.getTid()), pr, ctx.step(), scoreFt);
            bs.stats().upRtAndStep(bp, report.getStep(), act(EActionType.ftm, scoreFt));

            if (ball.upRunPointPlayerId(pr.getPlayerId(), scoreFt)) {//更新单个连续得分
                ball.updateMorale(5);
                report.addSubAct(ball.getTeamId(), pr.getPlayerId(), EActionType.pr_run, ball.getRunPointNumWithPlayer(), 0, 0,ctx.bp().isForce());
                bs.stats().setRtAndStep(pr, ctx.step(), act(EActionType.pr_run, scoreFt));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("subact ft. bid {} btid {} otid {} ptid {} pid {} prid {} ftnum {} -> {} reb {}", bs.getId(),
                    ctx.ball().getTeamId(), ctx.otherBall().getTeamId(), pr.getTid(), pr.getPid(), pr.getRid(),
                    ftNum, scoreFt, triggerReb);
        }
        //添加行为
        report.addSubAct(ball.getTeamId(), pr.getPlayerId(), EActionType.ft_act, ftNum, scoreFt, 0,ctx.bp().isForce());
        updatePlayerSkillPower(ctx, getType());
        //如果需要抢篮板，需要额外在这里调用一次篮板行为
        if (triggerReb) {
            //TO DO 需要支持自定义比赛 CustomSubActionBean
            SubActionFactory.getAction(EActionType.reb).doAction(newContext(ctx));
        } else {
            ctx.swapBall();
            ctx.report().setNextBall(ctx.otherBall().getTeamId());
        }
    }

}
