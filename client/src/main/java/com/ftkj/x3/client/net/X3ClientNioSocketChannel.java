package com.ftkj.x3.client.net;

import io.netty.channel.Channel;

import java.nio.channels.SocketChannel;

/**
 * 客户端使用的channel, 带有登录信息.
 *
 * @author luch
 */
public class X3ClientNioSocketChannel extends X3NioSocketChannel {


    public X3ClientNioSocketChannel() {
    }

    public X3ClientNioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
    }


}
