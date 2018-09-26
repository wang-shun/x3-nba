package com.ftkj.x3.client.task.single.sys;

import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ModuleConsole;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.arena.Arena;
import com.ftkj.x3.client.model.ClientArena;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.sys.ArenaClient;
import com.ftkj.x3.client.task.logic.sys.BattleClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map.Entry;

/**
 * 竞技场
 *
 * @author luch
 */
@Component
public class ArenaCodeClient extends SingleCodeTask {
    @Autowired
    private ArenaClient client;
    @Autowired
    private TeamClient teamClient;
    @Autowired
    private BattleClient bc;
    private EBattleType bt = EBattleType.Arena;

    public static void main(String[] args) {
        new ArenaCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        //                long aid = User_AccountId;
        long aid = 1000128791;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        //        teamClient.upgradeLev(uc, cu, ModuleConsole.getEnableLev(EModuleCode.Arena));
        Ret ret = client.info(uc, cu);
        if (ret.isErr()) {
            return;
        }

        client.rankList(uc, cu);
        //        client.matchHistory(uc, cu);

        //        ret = matchLoop(uc, cu, 5);
        //        if (ret.isErr()) {
        //            return;
        //        }

        //        multiTeamTargetTest();
        //        targetChangeTest();

        logout(uc);
        sleep(10 * MINUT);
    }

    private void targetChangeTest() {
        long tidgreater = 1000001316;
        long tidlower = 1000001329;
        UserClient uc1 = info(tidgreater);// 7
        UserClient uc2 = info(tidlower);// 9
        ClientUser cu1 = uc1.getUser();
        ClientUser cu2 = uc2.getUser();
        ClientArena ar1 = cu1.getArena();
        ClientArena ar2 = cu2.getArena();

        int rank1 = ar1.getRank();
        int rank2 = ar2.getRank();

        bc.matchSlow(uc2, cu2, () ->
            client.startMatch(uc2, cu2, ar1.getTeamId(), rank1), bt);// 9 <-> 7
        bc.gmForceEndMatch(uc2, cu2, true, bt);

        bc.matchAndForceEnd(uc1, cu1, () ->
            client.startMatch(uc1, cu1, ar2.getTeamId(), rank2), bt); // 7 <-> 9

        log.info("team1 {} oldtarget {} newtarget {}", uc1.tid(), uc2.tid(), ar1.getTargetRankAndTid().get(rank2));
    }

    /** 测试不同排名的球队的对手是否正常 */
    private void multiTeamTargetTest() {
        //        targets(1000001329);// 9
        //        targets(10000041975L);// 10
        info(1000038717);// 14
        info(1000002353);// 15
        info(1000026628);// 16
        //        targets(1000116134);// 19
        //        targets(1000001353);// 21
        //        targets(1000027867);// 22
    }

    private UserClient info(long aid) {
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        teamClient.upgradeLev(uc, cu, ModuleConsole.getEnableLev(EModuleCode.Arena));
        client.info(uc, cu);
        return uc;
    }

    private Ret matchLoop(UserClient uc, ClientUser cu, int matchNum) {
        for (int i = 0; i < matchNum; i++) {
            Ret ret = match(uc, cu);
            if (ret.isErr()) {
                return ret;
            }
            //            sleep(2000);
            //            bc.quickOrForceEndMatch(uc, cu, bt);
            bc.gmForceEndMatch(uc, cu, true, bt);
            client.info(uc, cu);
        }
        return succ();
    }

    private Ret match(UserClient uc, ClientUser cu) {
        ClientArena ar = cu.getArena();
        Entry<Integer, Long> rankAndTid = getTarget(ar);
        Ret ret = bc.matchSlow(uc, cu, () ->
            client.startMatch(uc, cu, rankAndTid.getValue(), rankAndTid.getKey()), bt);
        while (ret.ret() == ErrorCode.Arena_Target_Chnage.getCode()) {//对手发生变化
            sleep(SEC);
            Entry<Integer, Long> rankAndTid1 = getTarget(ar);
            ret = bc.matchSlow(uc, cu, () ->
                client.startMatch(uc, cu, rankAndTid1.getValue(), rankAndTid1.getKey()), bt);
        }
        return ret;
    }

    private Entry<Integer, Long> getTarget(ClientArena ar) {
        int rankRange = ConfigConsole.global().arenaNonTargetRankRange;
        return ar.getTargetRankAndTid().entrySet().stream()
            .filter(e -> {
                if (e.getKey() <= Arena.Target_Top_Fixed_Num) {
                    return Math.abs(ar.getRankOrLast() - e.getKey()) <= rankRange && e.getValue() != ar.getTeamId();
                } else {
                    return e.getValue() != ar.getTeamId();
                }
            })
            .findFirst()
            .orElse(null);
    }
}
