package com.ftkj.x3.client.net;

/**
 * 响应消息
 *
 * @author luch
 */
public class ClientReqMessage extends ClientMessage {
    /** 头长度. body len 4 + id 4 + code 4*/
    public static final int HEAD_LEN = 4 + 4 + 4;

    public ClientReqMessage(int id, int code) {
        this(id, code, Empty);
    }

    public ClientReqMessage(int id, int code, byte[] data) {
        super(id, code, data);
    }

    @Override
    int getHeadLength() {
        return HEAD_LEN;
    }

}
