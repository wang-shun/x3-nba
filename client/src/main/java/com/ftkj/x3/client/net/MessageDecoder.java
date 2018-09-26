package com.ftkj.x3.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 解码.
 *
 * @author luch
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(MessageDecoder.class);
    private static final boolean isDebug = log.isDebugEnabled();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
//        log.debug("receive msg. readable {}", readable);
        if (readable < ClientRespMessage.HEAD_LEN) {
            return;
        }

        in.markReaderIndex();

        int length = in.readInt();
//        log.debug("receive msg. len {}", length);
        if (length < 0) {
            in.resetReaderIndex();
            return;
        }

        int id = in.readInt();
        int code = in.readInt();
        boolean zip = in.readByte() == 1;

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
//        if (isDebug) {
//            log.debug("readable {} length {} code {} id {}", readable, length, code, id);
//        }
        byte[] data;
        if (length == 0) {
            data = ClientRespMessage.Empty;
        } else {
            data = new byte[length];
            in.readBytes(data);
        }

        ClientRespMessage msg = new ClientRespMessage(id, code, zip, data);
        out.add(msg);
    }
}
