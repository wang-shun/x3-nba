package com.ftkj.x3.client.task.logic.team;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.helper.PropHelper;
import com.ftkj.x3.client.task.logic.LogicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 道具测试.
 *
 * @author luch
 */
@Component
public class PropClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(PropClient.class);
    @Autowired
    private PropHelper propHelper;

    public static void main(String[] args) {
        new PropClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient tc, ClientUser ct) {
        return succ();
    }
}
