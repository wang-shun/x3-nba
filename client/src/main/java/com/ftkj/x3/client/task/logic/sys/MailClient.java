package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientEmail;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 邮件.
 *
 * @author luch
 */
@Component
public class MailClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(MailClient.class);

    public static void main(String[] args) {
        new MailClient().run();
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

    /** 一键领取, 领取所有未领有附件的 */
    public void receiveAll(UserClient uc, ClientUser cu) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.Email_Receive_All));
        DefaultData resp = parseFrom(msg);
        int count = 0;
        for (ClientEmail mail : cu.getEmails().values()) {
            if (mail.hasProp()) {
                mail.read();
                count++;
            }
        }
        log.info("tid {} 一键领取邮件 count {}. ret {}", cu.getTid(), count, ret(resp));
    }
}
