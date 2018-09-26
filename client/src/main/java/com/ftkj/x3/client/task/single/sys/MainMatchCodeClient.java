package com.ftkj.x3.client.task.single.sys;

import com.ftkj.manager.match.MainMatchLevel;
import com.ftkj.manager.match.TeamMainMatch;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.sys.BattleClient;
import com.ftkj.x3.client.task.logic.sys.MainMatchClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 主线赛程
 *
 * @author luch
 */
@Component
public class MainMatchCodeClient extends SingleCodeTask {
    @Autowired
    private MainMatchClient mmc;
    @Autowired
    private TeamClient teamClient;
    @Autowired
    private BattleClient bc;

    public static void main(String[] args) {
        new MainMatchCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        //                long aid = User_AccountId;
        long aid = 1000001329L;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        //        teamClient.upgradeLev(uc, cu, 79);
        Ret ret = mmc.info(uc, cu);
        if (ret.isErr()) {
            return;
        }
        TeamMainMatch tmm = cu.getTeamMainMatch();
        log.info("tid {} lev size {}", cu.tid(), tmm.getLevels().size());
        log.info("last cfg regalur lev {}", mmc.getLastCfgRegularLev());
        log.info("tid {} last enable regalur lev {}", cu.tid(), mmc.getLastEnableLevel(tmm));

        ret = mmc.championshipInfo(uc, cu, 1106);
        if (ret.isErr()) {
            return;
        }
        //        ret = mmc.championshipInfo(uc, cu, 1206);
        //        if (ret.isErr()) {
        //            return;
        //        }

        enableAllLevs(uc, cu, tmm, 2703);
        //        bc.quickOrForceEndMatch(uc, cu, EBattleType.Main_Match_Normal);
        //        mmc.quickMatch(uc, cu, 1104);
        //        match(uc, cu, mmc.getLastEnableLevel(tmm));
        //        for (int i = 0; i < 4; i++) {
        //            match(uc, cu, 1101);
        //            match(uc, cu, 1102);
        //            match(uc, cu, 1103);
        //            match(uc, cu, 1104);
        //            match(uc, cu, 1105);
        //            match(uc, cu, 1106);
        //        }
        //                match(uc, cu, 1106);
        //        bc.matchSlow(uc, cu, () -> mmc.startMatch(uc, cu, 1801), EBattleType.Main_Match_Normal);
        //                long bid = bc.matchAndForceEnd(uc, cu, () -> mmc.startMatch(uc, cu, 1201), EBattleType.Main_Match_Normal);
        for (int i = 0; i < 5; i++) {
            mmc.quickMatch(uc, cu, 2703);
        }
        //        bc.playerStats(uc, cu, bid);
        sleep(10 * MINUT);
    }

    private void enableAllLevs(UserClient uc, ClientUser cu, TeamMainMatch tmm, int lastLevRid) {
        MainMatchLevel lev = tmm.getLevel(lastLevRid);
        if (lev == null) {
            mmc.gmEnablelevel(uc, cu, lastLevRid);
            log.info("tid {} lev size {}", cu.tid(), tmm.getLevels().size());
        }
    }
}
