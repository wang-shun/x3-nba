package com.ftkj.x3.client.task.single.sys;

import com.ftkj.cfg.AllStarBean;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.sys.AllStarClient;
import com.ftkj.x3.client.task.logic.sys.BattleClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 挑战全明星
 *
 * @author luch
 */
@Component
public class AllStarCodeClient extends SingleCodeTask {
    @Autowired
    private AllStarClient client;
    @Autowired
    private BattleClient bc;
    private EBattleType bt = EBattleType.AllStar;

    public static void main(String[] args) {
        new AllStarCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = User_AccountId;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        AllStarBean asb = client.getEnable();
        if (asb == null) {
            log.warn("no enabled asb");
            return;
        }
        Ret ret = client.info(uc, cu, asb.getId());
        if (ret.isErr()) {
            return;
        }
        bc.matchSlow(uc, cu, () -> client.startMatch(uc, cu, asb.getId()), bt);
        bc.quickOrForceEndMatch(uc, cu, bt);
        sleep(HOUR);
    }
}