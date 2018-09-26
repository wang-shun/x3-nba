package com.ftkj.x3.client.task;

import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.net.ClientMessage;
import com.ftkj.x3.client.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luch
 */
public class GmMessage {

    /** 添加道具 */
    public ClientMessage addProp(long tid, int propId, int num) {
        return MessageUtil.createReq(ServiceCode.GMManager_addProp, tid, propId, num);
    }

    /** 添加货币 */
    public ClientMessage addMoney(long tid, int fk, int type) {
        return MessageUtil.createReq(ServiceCode.GMManager_addMoneyCallback, tid, fk, type);
    }

    /** 添加货币 */
    public ClientMessage addMoney(long tid, int money, int gold, int exp) {
        return MessageUtil.createReq(ServiceCode.GMManager_addMoney, tid, money, gold, exp);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000_000; i++) {
            try {
                String str = args[0] + i;
                list.add(str);
            } catch (Exception e) {
                if (e.getStackTrace().length == 0) {
                    System.out.format("Java ate my stacktrace after iteration #%d %n", i);
                    break;
                }
            }
        }
        System.out.println(list.size());
    }
}
