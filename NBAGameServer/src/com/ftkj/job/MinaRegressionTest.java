/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.ftkj.job;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MinaRegressionTest extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MinaRegressionTest.class);
    public static final int MSG_SIZE = 10;
    public static final int CLIENT_COUNT = 10;
    private static final int PORT = 23234;
    private static final int BUFFER_SIZE = 8192;
    private static final int TIMEOUT = 10000;

    public SocketAcceptor acceptor;

    public SocketConnector connector;

    private ExecutorService executor;

    public static AtomicInteger sent = new AtomicInteger(0);

    public MinaRegressionTest() throws IOException {
        int ncpu = Runtime.getRuntime().availableProcessors();
        ExecutorService executor1 = new OrderedThreadPoolExecutor(0, 1000, 60, TimeUnit.SECONDS, threadFactory("server"));
        executor = Executors.newFixedThreadPool(ncpu * 2, threadFactory("server"));

        acceptor = new NioSocketAcceptor(ncpu + 1);
        //        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(BUFFER_SIZE);

        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(executor));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

        connector = new NioSocketConnector(ncpu + 1);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        connector.setConnectTimeoutMillis(TIMEOUT);
        connector.getSessionConfig().setSendBufferSize(BUFFER_SIZE);
        //        connector.getSessionConfig().setReuseAddress(true);
    }

    public void connect() throws Exception {
        final InetSocketAddress socketAddress = new InetSocketAddress("0.0.0.0", PORT);
        final EchoQueueProtocolHandler handler = new EchoQueueProtocolHandler();
        acceptor.setHandler(handler);
        acceptor.bind(socketAddress);

        connector.setHandler(this);
        final IoFutureListener<ConnectFuture> listener = new IoFutureListener<ConnectFuture>() {
            public void operationComplete(ConnectFuture future) {
                try {
                    logger.info("client Write message to session " + future.getSession().getId());
                    final IoSession s = future.getSession();
                    for (int i = 0; i < MSG_SIZE; i++) {
                        s.write("c" + future.getSession().getId() + "_m_" + i);
                    }
                    logger.info("client session {} sent done", future.getSession().getId());
                } catch (Exception e) {
                    logger.error("Can't send message: {}", e.getMessage());
                }
            }
        };

        List<ConnectFuture> futures = new ArrayList<>(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            ConnectFuture future = connector.connect(socketAddress);
            futures.add(future);
        }
        for (ConnectFuture future : futures) {
            future.addListener(listener);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                connector.dispose();
                acceptor.unbind();
                acceptor.dispose();
                executor.shutdownNow();

                logger.info("Sent: " + sent.intValue());
                logger.info("Received: {} {}", handler.getAllMsg().size(), handler.getAllMsg());
                logger.info("FINISH");
            }
        });

    }

    private static ThreadFactory threadFactory(String name) {
        return new ThreadFactory() {
            private AtomicInteger id = new AtomicInteger();

            public Thread newThread(final Runnable r) {
                return new Thread(r, name + id.incrementAndGet());
            }
        };
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        if (!(cause instanceof IOException)) {
            logger.error("Exception: ", cause);
        } else {
            logger.info("I/O error: " + cause.getMessage());
        }
        session.closeNow();
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        sent.incrementAndGet();
    }

    public static void main(String[] args) throws Exception {
        logger.info("START");
        new MinaRegressionTest().connect();
    }
}