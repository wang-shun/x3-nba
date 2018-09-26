package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.proto.TradeP2PPB.TradeP2PMainData;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 交易 client.
 *
 * @author luch
 */
@Component
public class TradeClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(TradeClient.class);

    public static void main(String[] args) {
        new TradeClient().run();
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

    public TradeP2PMainData buyingList(UserClient uc, ClientUser cu, String playerName, String grade, int pos, int moneyOrder, int priceOrder, int page) {
        TradeP2PMainData resp = uc.req(uc, cu, ServiceCode.TradeP2PManager_trade_list, TradeP2PMainData.getDefaultInstance(),
            playerName, grade, pos, moneyOrder, priceOrder, page);
        if (!isSimpleLog()) {
            log.debug("trade buying list. tid {} resp {}", uc.tid(), shortDebug(resp));
        } else {
            log.debug("trade buying list. tid {} resp {}", uc.tid(), resp.getTradeListCount());
        }
        return resp;
    }
}
