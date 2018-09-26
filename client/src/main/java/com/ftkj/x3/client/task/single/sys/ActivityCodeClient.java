package com.ftkj.x3.client.task.single.sys;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.ActivityClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class ActivityCodeClient extends SingleCodeTask {

    @Autowired
    private ActivityClient client;

    public static void main(String[] args) {
        new ActivityCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = 1000123717;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        int actid = 30;
        client.show(uc, cu, actid);
    }
}