package com.ftkj.x3.client.util;

import com.ftkj.proto.MoPB.MoData;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.net.ClientReqMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luch
 */
public class MessageUtil {
    private static final Logger log = LoggerFactory.getLogger(MessageUtil.class);

    public static ClientReqMessage createReq(int code, Object... params) {
        String reqstr = createReqStr(params);
        log.debug("create msg code {} req args {}", code, reqstr);
        MoData md = MoData.newBuilder().setMsg(reqstr).build();
        return new ClientReqMessage(0, code, md.toByteArray());
    }

    public final static String DELIMITER = "Ω";

    private static String createReqStr(Object... params) {
        if (params.length == 0) {
            return "";
        }
        if (params.length == 1) {
            return String.valueOf(params[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]).append(DELIMITER);
        }
        return sb.toString();
    }

    public static ClientReqMessage createGM(GmCommand gc, Object... args) {
        String command = gc.getCommand();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                command = command + " " + arg;
            }
        }
        return createReq(ServiceCode.GM_By_Type, command);
    }
}
