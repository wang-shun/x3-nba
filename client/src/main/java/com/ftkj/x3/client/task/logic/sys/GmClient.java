package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.xxs.core.util.StringUtil;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 模板 client.
 *
 * @author luch
 */
@Component
public class GmClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(GmClient.class);

    public static void main(String[] args) {
        new GmClient().run();
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

    public enum ActGmType {
        extend,
        value,
        other,
        finishStatus,
        awardStatus,;

        public static String toStr(List<Integer> ints) {
            return StringUtil.append(ints, StringUtil.COMMA);
        }

        public ActGm with(Object obj) {
            return new ActGm(this, obj.toString());
        }
    }

    public static final class ActGm {
        private final ActGmType at;
        private final String val;

        public ActGm(ActGmType at, String val) {
            this.at = at;
            this.val = val;
        }
    }

    public Ret activitySet(UserClient uc, ClientUser cu, int aid, ActGm... tpls) {
        StringBuilder str = new StringBuilder();
        str.append(aid);
        for (ActGm tpl : tpls) {
            str.append(" ").append(tpl.at.name()).append(" ").append(tpl.val);
        }
        log.info("activity set gm. tid {} args [{}]", cu.tid(), str.toString());
        Message msg = uc.writeAndGet(createGM(GmCommand.Activity_Set, str.toString()));
        return ret(msg);
    }
}
