package com.ftkj.manager.battle.handle;

import com.ftkj.enums.ECustomPVPType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.prop.PropSimple;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author tim.huang
 * 2017年8月3日
 * 玩家自定义比赛
 */
public class BattleTeamCustom extends BaseBattleHandle {
    public BattleTeamCustom(BattleSource battleSource, BattleEnd end, BattleRoundReport round) {
        super(battleSource, end, round);
    }

    @Override
    public synchronized BattleTeam winTeam() {
        CustomPVPRoom room = super.getBattleSource().getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
        //参与计算的位置
        Predicate<EPlayerPosition> pospre = pos -> {
            if (room.getPositionCondition() == EPlayerPosition.NULL) {
                return pos != EPlayerPosition.NULL;
            } else {
                return pos == room.getPositionCondition();
            }
        };
        BattleTeam homeTeam = getBattleSource().getHome();
        BattleTeam awayTeam = getBattleSource().getAway();
        float homeScore = homeTeam.sumActionStatistics(pospre, room.getWinCondition());
        float awayScore = awayTeam.sumActionStatistics(pospre, room.getWinCondition()) + (room.getRoomScore() / 10f);
        return homeScore > awayScore ? homeTeam : awayTeam;
    }

    @Override
    protected void initPre() {
        CustomPVPRoom room = super.getBattleSource().getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
        if (room.getPkType() == ECustomPVPType.公平球员赛) {//公平赛，移除所有相关额外加成
            super.getBattleSource().getHome().getPlayers().forEach(player -> player.clearCap());
            super.getBattleSource().getAway().getPlayers().forEach(player -> player.clearCap());
            super.getBattleSource().getHome().getAbility().clearCap();
            super.getBattleSource().getAway().getAbility().clearCap();
        }
    }

    @Override
    public synchronized EndReport initEndReport() {
        BattleTeam win = winTeam();
        BattleSource bs = getBattleSource();
        BattleTeam loss = bs.getHome().getTeamId() == win.getTeamId() ? bs.getAway() : bs.getHome();
        loss.setWin(false);
        win.setWin(true);
        CustomPVPRoom room = bs.getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);

        List<PropSimple> winGifts = Lists.newArrayList();
        winGifts.add(new PropSimple(4011, room.getRoomMoney() * 2));
        List<PropSimple> lossGifts = Lists.newArrayList();
        addConfigAwardProps(bs, winGifts, lossGifts);
        return createReport(bs, win, winGifts, lossGifts);
    }

}
