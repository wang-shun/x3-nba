package com.ftkj.x3.client.task.single.sys;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.TaskClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class TaskCodeClient extends SingleCodeTask {
    @Autowired
    private TaskClient client;

    public static void main(String[] args) {
        new TaskCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        //        long aid = User_AccountId;
        long aid = 1000109078L;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        client.showTaskMain(uc, cu);
    }
}