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

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@link IoHandler} implementation for echo server.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class EchoQueueProtocolHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(EchoQueueProtocolHandler.class);
    private BlockingQueue<Object> allMsg = new LinkedBlockingQueue<>();//客户端请求队列

    @Override
    public void sessionCreated(IoSession session) {
        // We're going to use SSL negotiation notification.
        //        LOGGER.info("Created {}", session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOGGER.info("CLOSED {}", session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOGGER.info("OPENED {}", session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        LOGGER.info("*** IDLE #{} status {} ***", session.getIdleCount(status), status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOGGER.warn("Unexpected exception.", cause);
        session.closeNow();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        //        LOGGER.info("Received : " + message);
        Thread.sleep(ThreadLocalRandom.current().nextInt(1, 10));
        allMsg.offer(message);
    }

    public BlockingQueue<Object> getAllMsg() {
        return allMsg;
    }
}