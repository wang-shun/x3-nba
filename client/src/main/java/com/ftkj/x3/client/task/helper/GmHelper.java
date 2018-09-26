package com.ftkj.x3.client.task.helper;

import com.ftkj.cfg.VipBean;
import com.ftkj.console.VipConsole;
import com.ftkj.enums.EPayType;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import org.springframework.stereotype.Component;

/**
 * @author luch
 */
@Component
public class GmHelper extends X3TaskHelper {

    public Ret gmAddVip(UserClient uc, ClientUser cu, int lev) {
        if (cu.getVip().getLevel() >= lev) {
            return Ret.success();
        }
        VipBean vb = VipConsole.getVipLevelBean(lev);
        uc.writeAndGet(uc.gmAddMoney(vb.getMoney(), EPayType.充值));
        return Ret.success();
    }

    public Ret gmAddVip(UserClient uc, ClientUser cu) {
        int maxLev = VipConsole.getMaxLev();
        return gmAddVip(uc, cu, maxLev);
    }
}
