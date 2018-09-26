package com.ftkj.x3.client.task.single;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import org.springframework.stereotype.Component;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class _TemplateCodeClient extends SingleCodeTask {

    public static void main(String[] args) {
        new _TemplateCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = User_AccountId;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();
        log.debug("single code test");
    }
}