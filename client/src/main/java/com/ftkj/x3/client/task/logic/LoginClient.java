package com.ftkj.x3.client.task.logic;

import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.x3.client.X3Task;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.xxs.net.Message;
import com.google.protobuf.TextFormat;
import org.springframework.stereotype.Component;

/**
 * @author luch
 */
@Component
public class LoginClient extends LogicTask {

    public static void main(String[] args) {
        new LoginClient().run(args);
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        log.info("user {} login success", uc.getUser().getName());
        return success();
    }

    private static void testParsePb() {
        DefaultData srcdd = DefaultData.newBuilder()
                .setCode(1)
                .setMsg("test 测试")
                .build();
        System.out.println(TextFormat.shortDebugString(srcdd));
        Message msg = new ClientRespMessage(1, 1, false, srcdd.toByteArray());
        DefaultData dd = X3Task.parseFrom(DefaultData.getDefaultInstance(), msg);
        System.out.println(TextFormat.shortDebugString(dd));
        //        new TeamClient().run();
    }
}
