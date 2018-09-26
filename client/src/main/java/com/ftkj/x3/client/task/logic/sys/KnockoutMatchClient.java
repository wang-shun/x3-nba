package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.proto.MatchPB.MatchListData;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 模板 client.
 *
 * @author luch
 */
@Component
public class KnockoutMatchClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(KnockoutMatchClient.class);

    public static void main(String[] args) {
        new KnockoutMatchClient().run();
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

    /** 比赛列表 */
    public MatchListData info(UserClient uc, ClientUser cu) {
        MatchListData resp = uc.req(uc, cu, ServiceCode.Match_List, MatchListData.getDefaultInstance());
        log.info("info tid {} resp {}", cu.tid(), shortDebug(resp));
        return resp;
    }

    /** 报名多人赛 */
    public Ret sign(UserClient uc, ClientUser cu, int battleType, int seqId) {
        return uc.reqCommon(uc, cu, ServiceCode.Match_Sign, 0, battleType, seqId);
    }
}
