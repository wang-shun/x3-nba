package com.ftkj.x3.client.task.helper;

import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.util.ByteUtil;
import com.ftkj.x3.client.X3Task;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.proto.ClientPbUtil;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.xxs.client.Task;
import com.ftkj.xxs.net.Message;

/**
 * @author luch
 */
public abstract class X3TaskHelper implements Task, X3Task {
    public static String shortDebug(com.google.protobuf.Message msg) {
        return ClientPbUtil.shortDebug(msg);
    }

    public static DefaultData parseFrom(Message msg) {
        return X3Task.parseFrom(DefaultData.getDefaultInstance(), msg);
    }

    public static <T extends com.google.protobuf.Message> T parseFrom(T parser, Message msg) {
        if (msg instanceof ClientRespMessage) {
            ClientRespMessage rmsg = (ClientRespMessage) msg;
            if (rmsg.isZip()) {
                byte[] srcdata = ByteUtil.decompress(msg.getData());
                return X3Task.parseFrom(parser, msg, srcdata);
            }
        }
        return X3Task.parseFrom(parser, msg);
    }

    @Override
    public Ret success() {
        return Ret.success();
    }

}
