package com.ftkj.x3.client.net;

import com.ftkj.xxs.client.net.ClientChannelHolder;

/**
 * @author luch
 */
public class X3ClientChannelHolder extends ClientChannelHolder {
    public X3ClientChannelHolder() {
        super(new X3ClientChannelGroup());
    }

    @SuppressWarnings("unchecked")
    @Override
    public X3ClientChannelGroup channelGroup() {
        return (X3ClientChannelGroup) super.channelGroup();
    }
}
