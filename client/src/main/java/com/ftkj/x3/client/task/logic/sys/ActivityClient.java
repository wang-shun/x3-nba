package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.proto.AtvCommonPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 模板 client.
 *
 * @author luch
 */
@Component
public class ActivityClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(ActivityClient.class);

    public static void main(String[] args) {
        new ActivityClient().run();
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

    public AtvCommonPB.AtvCommonData show(UserClient uc, ClientUser cu, int actid) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.SystemActiveManager_ShowView, actid));
        AtvCommonPB.AtvCommonData resp = parseFrom(AtvCommonPB.AtvCommonData.getDefaultInstance(), msg);
        log.info("act show. tid {} actid {} resp {}", cu.tid(), actid, shortDebug(resp));
        return resp;
    }
}
