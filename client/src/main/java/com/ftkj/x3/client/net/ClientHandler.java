package com.ftkj.x3.client.net;

import com.ftkj.x3.client.proto.Ret;
import com.ftkj.xxs.client.net.ClientMsgHandler;
import com.ftkj.xxs.client.net.XXsClientHandler;
import com.ftkj.xxs.proto.XxsRet;

/**
 * @author luch
 */
public class ClientHandler extends XXsClientHandler<ClientRespMessage> {
    public ClientHandler(ClientMsgHandler clientMsgHandler) {
        super(clientMsgHandler);
    }

    @Override
    public XxsRet convertRet(int ret) {
        return Ret.convert(ret);
    }
}
