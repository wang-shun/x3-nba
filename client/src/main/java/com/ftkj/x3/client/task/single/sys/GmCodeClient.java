package com.ftkj.x3.client.task.single.sys;

import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.EPayType;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.task.logic.sys.GmClient;
import com.ftkj.x3.client.task.logic.sys.GmClient.ActGmType;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.x3.client.task.single.SingleCodeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 单个消息测试模版
 *
 * @author luch
 */
@Component
public class GmCodeClient extends SingleCodeTask {
    @Autowired
    private TeamClient teamClient;
    @Autowired
    private GmClient gmClient;

    public static void main(String[] args) {
        new GmCodeClient().run();
    }

    @Override
    protected void run1(String[] args) {
        long aid = User_AccountId + 1;
        UserClient uc = login(aid, "" + aid);
        //        UserClient uc = loginMainAccount();
        ClientUser cu = uc.getUser();

        //        levAndCurr(uc, cu);
        uc.writeAndGet(createGM(GmCommand.Match_Force_End_All, 0));
        //        actTaskSet(uc, cu);
    }

    /** 彩球活动 */
    private void actTaskSet(UserClient uc, ClientUser cu) {
        int actid = 30; //彩球活动
        int todayAwardStatus = 0;//今天点燃状态 1是已点
        gmClient.activitySet(uc, cu, actid, ActGmType.value.with(todayAwardStatus));
    }

    private void levAndCurr(UserClient uc, ClientUser cu) {
        teamClient.upgradeLev(uc, cu, 50);
        uc.writeAndGet(uc.gmCurrency(EMoneyType.Money, 9999999));
        uc.writeAndGet(uc.gmCurrency(EMoneyType.Bind_Money, 9999999));
        uc.writeAndGet(uc.gmCurrency(EMoneyType.Gold, 9999999));
        uc.writeAndGet(createGM(GmCommand.Pay, EPayType.充值.getType(), 30000));
    }
}