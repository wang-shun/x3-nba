package com.ftkj.x3.client.task.single.team;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class TeamCodeClient extends SingleCodeTask {
    @Autowired
    private TeamClient client;

    public static void main(String[] args) {
        new TeamCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
//        long aid = User_AccountId;
        long aid = 1000040833;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
//        client.upgradeLev(uc, cu, 50);
//        long targetTid = 1011000003517L;
//        client.viewTeamInfoAndCaps(uc, cu, targetTid);
    }
}