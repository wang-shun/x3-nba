package com.ftkj.x3.client.net;

import com.ftkj.xxs.client.net.ClientMsgHandler;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author luch
 */
public class X3ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    //    private static final LoggingHandler loggingHandler = new LoggingHandler();
    private final EventExecutorGroup executor;
    private final ClientMsgHandler clientMsgHandler;

    public X3ClientChannelInitializer(ClientMsgHandler clientMsgHandler) {
        int nThread = Runtime.getRuntime().availableProcessors() * 2;
        EventExecutorGroup executor = new DefaultEventExecutorGroup(nThread, new XxsThreadFactory("client-handler"));
        this.executor = executor;
        this.clientMsgHandler = clientMsgHandler;
    }

    public X3ClientChannelInitializer(EventExecutorGroup executor, ClientMsgHandler clientMsgHandler) {
        this.executor = executor;
        this.clientMsgHandler = clientMsgHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //add codec first
        pipeline.addLast("decoder", new MessageDecoder());
        pipeline.addLast("encoder", new MessageEncoder());
        pipeline.addLast("idleStateHandler", new IdleStateHandler(20 * 60, 0, 0));
        //        pipeline.addLast("loggingHandler", loggingHandler);

        //and the business logic.
        // Please note we create a handler for every new channel
        // because it has stateful properties.
        pipeline.addLast(executor, "handler", new ClientHandler(clientMsgHandler));
    }
}