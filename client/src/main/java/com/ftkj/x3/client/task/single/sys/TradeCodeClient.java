package com.ftkj.x3.client.task.single.sys;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.TradeClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 交易
 *
 * @author luch
 */
@Component
public class TradeCodeClient extends SingleCodeTask {
    @Autowired
    private TradeClient client;

    public static void main(String[] args) {
        new TradeCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
//        long aid = User_AccountId;
        long aid = 1000112465L;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        client.buyingList(uc,cu,"", "", 0, 0, 0, 0);
    }
}