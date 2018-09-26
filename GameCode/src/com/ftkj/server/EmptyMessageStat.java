package com.ftkj.server;

/**
 * @author luch
 */
public class EmptyMessageStat implements MessageStat {
    @Override
    public void addSendMsgStat(int code, int sendLength) {

    }

    @Override
    public void addReceivedMsgStat(int code, int receivedLength) {

    }

    @Override
    public void addReceivedMsgStat(int code, int receivedLength, int time) {

    }
}
