package com.ftkj.x3.client.net;

import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.xxs.client.net.AsyncListener;
import com.ftkj.xxs.client.net.XxsUserClient;
import com.ftkj.xxs.net.Message;

/**
 * @author luch
 */
public interface X3AsyncListener extends AsyncListener {
    @Override
    default void channelRead(XxsUserClient uc0, Message msg) {
        UserClient uc = (UserClient) uc0;
        channelRead0(uc, (ClientUser) uc.getUser(), (ClientRespMessage) msg);
    }

    void channelRead0(UserClient uc, ClientUser user, ClientRespMessage msg);
}
