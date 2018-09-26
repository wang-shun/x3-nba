package com.ftkj.x3.client.task.single.sys;

import com.ftkj.manager.match.RankedMatch;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.player.PlayerClient;
import com.ftkj.x3.client.task.logic.sys.RankedClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 跨服天梯赛
 *
 * @author luch
 */
@Component
public class RankedCodeClient extends SingleCodeTask {
    @Autowired
    private RankedClient rankedClient;
    @Autowired
    private PlayerClient playerClient;

    public static void main(String[] args) {
        new RankedCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long uid = 1000003540;
        UserClient uc = login(uid, "" + uid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        log.debug("single code test");

        Ret ret = rankedClient.info(uc, cu);
        if (ret.isErr()) {
            return;
        }
        //        updateRatingAndRankList(uc, cu);

        //        joinPool(uc, cu);
        rankedClient.rankList(uc, cu, 7);
        //        infoAndJoin(User_AccountId + 1);
        //        infoAndJoin(User_AccountId + 2);
        sleep(HOUR);
    }

    private void updateRatingAndRankList(UserClient uc, ClientUser cu) {
        rankedClient.rankList(uc, cu);
        uc.writeAndGet(createGM(GmCommand.RMatch_Refresh_Rank));
        rankedClient.rankList(uc, cu);
        uc.writeAndGet(createGM(GmCommand.RMatch_Rating, 400));
        cu.getRankedMatch().getCurrSeason().addRating(400);
        uc.writeAndGet(createGM(GmCommand.RMatch_Refresh_Rank));
        rankedClient.rankList(uc, cu);
    }

    private void infoAndJoin(long aid) {
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        log.debug("single code test");

        Ret ret = rankedClient.info(uc, cu);
        if (ret.isErr()) {
            return;
        }
        joinPool(uc, cu);
    }

    private void joinPool(UserClient uc, ClientUser cu) {
        RankedMatch rm = cu.getRankedMatch();
        log.info("rm tid {}", rm.getTeamId());
        playerClient.fillLineupPlayer(uc, cu, 5);
        Ret ret = rankedClient.joinPool(uc, cu);
        log.info("join ret {}", ret);
    }
}
