package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.proto.PlayerBidPB.PlayerBidMainData;
import com.ftkj.proto.PlayerBidPB.PlayerBigGuessMainData;
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
 * 球员竞价 client.
 *
 * @author luch
 */
@Component
public class PlayerBidClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(PlayerBidClient.class);

    public static void main(String[] args) {
        new PlayerBidClient().run();
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

    /** 获取竞价信息列表 */
    public PlayerBidMainData bidinfo(UserClient uc, ClientUser cu) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.LocalPlayerBidManager_showPlayerBidGuessMain));
        PlayerBidMainData resp = parseFrom(PlayerBidMainData.getDefaultInstance(), msg);
        log.info("bidinfo tid {} id {} sec {} prs {}", uc.tid(), resp.getMyId(), resp.getEndSecond(), resp.getDetailListCount());
        if (!isSimpleLog()) {
            log.info("bidinfo tid {} id {} prs {}", uc.tid(), resp.getMyId(), shortDebug(resp.getDetailListList()));
        }
        return resp;
    }

    /** 获取单个球员竞价信息 */
    public PlayerBigGuessMainData bidPlayerInfo(UserClient uc, ClientUser cu, int id) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.LocalPlayerBidManager_showPlayerBidGuessPlayerMain, id));
        PlayerBigGuessMainData resp = parseFrom(PlayerBigGuessMainData.getDefaultInstance(), msg);
        log.info("bidPlayerInfo tid {} id {} sec {} price my {} max {} people {} stats {} teams {}", uc.tid(),
                resp.getId(), resp.getEndSecond(), resp.getMyPrice(), resp.getMaxPrice(), resp.getTotalPeople(),
                resp.getStatus(), resp.getTeamInfoListCount());
        if (!isSimpleLog()) {
            log.info("bidPlayerInfo tid {} id {} teams {}", uc.tid(), resp.getId(), shortDebug(resp.getTeamInfoListList()));
        }
        return resp;
    }
}
