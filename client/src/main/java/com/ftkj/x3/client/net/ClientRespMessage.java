package com.ftkj.x3.client.net;

/**
 * 请求消息
 *
 * @author luch
 */
public class ClientRespMessage extends ClientMessage {
    /** 头长度. body len 4 + id 4 + code 4 + zip 1 */
    public static final int HEAD_LEN = 4 + 4 + 4 + 1;
    /** data 是否经过zip压缩 */
    private final boolean zip;

    public ClientRespMessage(int id, int code) {
        this(id, code, false, Empty);
    }

    public ClientRespMessage(int id, int code, boolean zip, byte[] data) {
        super(id, code, data);
        this.zip = zip;
    }

    @Override
    int getHeadLength() {
        return HEAD_LEN;
    }

    public boolean isZip() {
        return zip;
    }

}
