package com.ftkj.x3.client.task.single.sys;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.BattleClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 比赛
 *
 * @author luch
 */
@Component
public class BattleCodeClient extends SingleCodeTask {
    @Autowired
    private TeamClient teamClient;
    @Autowired
    private BattleClient bc;

    public static void main(String[] args) {
        new BattleCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        //        long aid = User_AccountId + 5;
        long aid = 1000147025L;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        bc.battleStats(uc, cu);
        //        bc.quickOrForceEndMatch(uc, cu, EBattleType.Ranked_Match);
        //        bc.matchSlow(uc, cu, () -> bc.startMatch(uc, cu, 1201), EBattleType.Main_Match_Normal);
        //       long bid = bc.matchAndForceEnd(uc, cu, () -> mmc.startMatch(uc, cu, 1201), EBattleType.Main_Match_Normal);
        //       bc.playerStats(uc, cu, bid);
        //        sleep(10 * MINUT);
    }
}
