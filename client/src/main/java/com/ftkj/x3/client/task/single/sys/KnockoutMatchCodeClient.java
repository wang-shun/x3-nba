package com.ftkj.x3.client.task.single.sys;

import com.ftkj.enums.battle.EBattleType;
import com.ftkj.proto.MatchPB.MatchData;
import com.ftkj.proto.MatchPB.MatchListData;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.KnockoutMatchClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class KnockoutMatchCodeClient extends SingleCodeTask {
    @Autowired
    private KnockoutMatchClient client;
    @Autowired
    private TeamClient teamClient;

    public static void main(String[] args) {
        new KnockoutMatchCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = User_AccountId;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();

        multiUserSign(User_AccountId + 100, 10, 1, 9);//lev 1   10
        multiUserSign(User_AccountId + 200, 10, 11, 19);// lev 11   20
        multiUserSign(User_AccountId + 300, 10, 21, 29);// lev 21   80

        sleep(30 * MINUT);
    }

    private void multiUserSign(long aidstart, int aidNum, int levmin, int levmax) {
        IntStream.range(0, aidNum).parallel()
                .forEach(i -> {
                    try {
                        infoAndSign(aidstart + i, ThreadLocalRandom.current().nextInt(levmin, levmax));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
        //        for (int i = 0; i < aidNum; i++) {
        //            infoAndSign(aidstart + i, ThreadLocalRandom.current().nextInt(levmin, levmax));
        //        }
    }

    private void infoAndSign(long aid, int lev) {
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        teamClient.upgradeLev(uc, cu, lev);
        MatchListData lists = client.info(uc, cu);
        MatchData md = lists.getMatchListList().stream()
                .filter(m -> m.getId() < EBattleType.多人赛_110.getId() && !m.getIsSign() && m.getStatus() == 1)
                .findAny()
                .orElse(null);
        if (md != null) {
            client.sign(uc, cu, md.getId(), md.getSeq());
        }
    }
}