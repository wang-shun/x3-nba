package com.ftkj.x3.client.net;

import com.ftkj.enums.ErrorCode;
import com.ftkj.xxs.net.Message;

/**
 * 客户端消息. 请求响应不对等.
 *
 * @author luch
 */
public abstract class ClientMessage implements Message {
    /** 正确时的返回码为0 */
    public static final int Ret_Code_Success = ErrorCode.Ret_Success;
    public static final byte[] Empty = new byte[0];
    /** msg id */
    private final int id;
    /** 消息号 */
    private final int code;
    /** data */
    private final byte[] data;

    public ClientMessage(int id, int code) {
        this(id, code, Empty);
    }

    public ClientMessage(int id, int code, byte[] data) {
        this.id = id;
        this.code = code;
        this.data = data != null ? data : Empty;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public int getRet() {
        return Ret_Code_Success;
    }

    @Override
    public boolean isSucess() {
        return true;
    }

    @Override
    public int getBodyLength() {
        return data.length;
    }

    abstract int getHeadLength();

    @Override
    public int getTotalLength() {
        return getHeadLength() + getBodyLength();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
