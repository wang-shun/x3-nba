package com.ftkj.x3.client.task.helper;

import com.ftkj.console.PropConsole;
import com.ftkj.enums.EPropType;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.proto.PropPB.TeamPropsData;
import com.ftkj.x3.client.console.ClientConsole;
import com.ftkj.x3.client.model.ClientProp;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.ClientPbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 道具辅助类
 *
 * @author luch
 */
@Component
public class PropHelper extends X3TaskHelper {
    private static final Logger log = LoggerFactory.getLogger(PropHelper.class);
    @Autowired
    private ClientConsole console;

    public void gmAddProp(UserClient uc, int propRid, int num) {
        if (propRid > 0) {
            uc.writeAndGet(uc.gmAddProp(propRid, num));
        }
        log.debug("tid {} gm addprop rid {} num {} succ", uc.tid(), propRid, num);
    }

    /** 随机添加道具 */
    public int randomProp(UserClient uc, int num) {
        return randomProp(uc, EPropType.Common, num);
    }

    /** 随机添加道具 */
    public int randomProp(UserClient uc, EPropType type, int num) {
        int size = console.getProp().getSize();
        int index = ThreadLocalRandom.current().nextInt(size);
        List<Integer> allIds = console.getProp().getAllIds();

        int propRid = 0;
        for (int i = index; i < size; i++) {
            propRid = matchProp(allIds, type, i);
            if (propRid > 0) {
                break;
            }
        }
        if (propRid == 0) {
            for (int i = 0; i < index; i++) {
                propRid = matchProp(allIds, type, i);
                if (propRid > 0) {
                    break;
                }
            }
        }

        if (propRid > 0) {
            uc.writeAndGet(uc.gmAddProp(propRid, num));
        }
        log.debug("team {} gm命令添加道具 rid {} num {} 成功", uc.tid(), propRid, num);
        return propRid;
    }

    private int matchProp(List<Integer> allIds, EPropType type, int index) {
        Integer id = allIds.get(index);
        PropBean pb = PropConsole.getProp(id);
        if (pb == null) {
            return 0;
        }
        if (type != null) {
            if (pb.getType() == type) {
                return pb.getPropId();
            }
        } else {
            return pb.getPropId();
        }
        return 0;
    }

    /** 道具发生变化 */
    public static void propChange(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        long tid = uc.tid();
        TeamPropsData tpd = parseFrom(TeamPropsData.getDefaultInstance(), msg);
        if (log.isTraceEnabled()) {
            log.trace("tid {} prop change {}", tid, shortDebug(tpd));
        }
        Map<Integer, ClientProp> newprops = ClientPbUtil.createProp(tid, tpd);
        for (ClientProp newprop : newprops.values()) {
            ClientProp oldprop = cu.getProps().get(newprop.getPropId());
            if (oldprop != null) {
                log.trace("tid {} prop update final num {} -> {}", tid, oldprop.getNum(), newprop.getNum());
                oldprop.setNum(newprop.getNum());
            } else {
                cu.getProps().put(newprop.getPropId(), newprop);
                log.trace("tid {} add new prop id {} num {}", tid, newprop.getPropId(), newprop.getNum());
            }
        }
    }
}
