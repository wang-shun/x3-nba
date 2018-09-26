package com.ftkj.server;

/**
 * @author luch
 */
public interface MessageStat {
    /** 统计一个发送消息状态 */
    void addSendMsgStat(int code, int sendLength);

    /** 统计一个接收到的消息的状态 */
    void addReceivedMsgStat(int code, int receivedLength);

    /** 统计一个接收到的消息的状态 */
    void addReceivedMsgStat(int code, int receivedLength, int time);
}
