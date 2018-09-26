package com.ftkj.x3.client;

import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.x3.client.net.ClientReqMessage;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.util.MessageUtil;
import com.ftkj.xxs.client.Task.Resp;
import com.ftkj.xxs.net.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UninitializedMessageException;

/**
 * @author luch
 */
public interface X3Task {

    default ClientReqMessage createReq(int code, Object... params) {
        return MessageUtil.createReq(code, params);
    }

    default ClientReqMessage createGM(GmCommand gc, Object... params) {
        return MessageUtil.createGM(gc, params);
    }

    default Ret ret(Message msg) {
        return msg != null ? Ret.convert(msg.getRet()) : Ret.nulll();
    }

    default Ret ret(String msg, Object... args) {
        return Ret.clientError(msg, args);
    }

    default Ret ret(DefaultData resp) {
        return resp != null ? Ret.convert(resp.getCode()) : Ret.nulll();
    }

    default <M> Resp<M> succ(M m) {
        return new Resp<>(Ret.success(), m);
    }

    default <M> Resp<M> error(Message msg) {
        return new Resp<>(ret(msg), null);
    }

    default <M> Resp<M> error(Ret ret) {
        return new Resp<>(ret, null);
    }

    default <M> Resp<M> error(DefaultData dd) {
        return new Resp<>(Ret.convert(dd.getCode()), null);
    }

    static <T extends com.google.protobuf.Message> T parseFrom(T parser, Message msg) {
        return parseFrom(parser, msg, msg.getData());
    }

    static <T extends com.google.protobuf.Message> T parseFrom(T parser, Message msg, byte[] data) {
        try {
            T.Builder builder = parser.newBuilderForType();
            @SuppressWarnings("unchecked")
            T result = (T) builder.mergeFrom(data).buildPartial();
            if (!result.isInitialized()) {
                throw new UninitializedMessageException(result).asInvalidProtocolBufferException();
            }
            return result;
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(String.format("code %s mid %s bodylen %s ex msg %s",
                    msg.getCode(), msg.getId(), msg.getBodyLength(), e.getMessage()), e);
        }
    }

}
