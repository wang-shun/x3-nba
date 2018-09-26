package com.ftkj.x3.client.task.single.sys;

import com.ftkj.proto.PlayerBidPB.PlayerBidMainData;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.PlayerBidClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 球员竞价
 *
 * @author luch
 */
@Component
public class PlayerBidCodeClient extends SingleCodeTask {
    @Autowired
    private PlayerBidClient client;

    public static void main(String[] args) {
        new PlayerBidCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = 1000066850L;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        log.debug("single code test");
        PlayerBidMainData bidinfo = client.bidinfo(uc, cu);
        if(bidinfo.getMyId() > 0) {
            client.bidPlayerInfo(uc, cu, bidinfo.getMyId());
        }
    }
}