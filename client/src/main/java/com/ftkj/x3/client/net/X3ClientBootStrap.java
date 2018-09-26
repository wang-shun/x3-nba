package com.ftkj.x3.client.net;

import com.ftkj.xxs.client.net.ClientBootStrap;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.util.Server;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luch
 */
@Component
public class X3ClientBootStrap extends ClientBootStrap<X3ClientNioSocketChannel> {
    private EventExecutorGroup handlerExecutor;
    private X3ClientMsgHandler clientMsgHandler;

    @Autowired
    public void setClientMsgHandler(X3ClientMsgHandler clientMsgHandler) {
        this.clientMsgHandler = clientMsgHandler;
    }

    public X3ClientNioSocketChannel newChannel(boolean wanip, Server server) {
        return newChannel(wanip ? server.getWanIp() : server.getLanIp(), server.getPort());
    }

    @Override
    public void init() {
        this.handlerExecutor = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2, new XxsThreadFactory("client-async"));
        ChannelHandler channelHandler = new X3ClientChannelInitializer(handlerExecutor, clientMsgHandler);
        setChannel(X3ClientNioSocketChannel.class);
        setChannelHandler(channelHandler);
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        handlerExecutor.shutdownGracefully();
        //        try {
        //            handlerExecutor.terminationFuture().sync();
        //        } catch (InterruptedException e) {
        //            log.error(e.getMessage(), e);
        //        }
    }
}
