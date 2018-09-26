package com.ftkj.x3.client.task.logic.team;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 模板client.
 *
 * @author luch
 */
@Component
public class VipClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(VipClient.class);

    public static void main(String[] args) {
        new VipClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient uc, ClientUser cu) {
        return succ();
    }

}
