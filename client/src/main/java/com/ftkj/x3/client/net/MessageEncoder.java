package com.ftkj.x3.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 编码
 *
 * @author luch
 */
public class MessageEncoder extends MessageToByteEncoder<ClientReqMessage> {
    private static final Logger log = LoggerFactory.getLogger(MessageEncoder.class);
    private static final boolean isDebug = log.isDebugEnabled();

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientReqMessage msg, ByteBuf out) throws Exception {
        int totalLen = msg.getTotalLength();
        ByteBuf buf = Unpooled.buffer(totalLen);

        buf.writeInt(msg.getBodyLength());

        //        if (isDebug && msg.getBodyLength() != msg.getBodyReadableBytes()) {
        //            log.error("code[{}]mid[{}]bodylen[{}] != data.readableBytes[{}]",
        //                    msg.getCode(), msg.getId(), msg.getBodyLength(), msg.getBodyReadableBytes());
        //            //            throw new IllegalStateException("bodylen != data.readableBytes");
        //        }
        //        if (isDebug) {
        //            if (msg.code() == 302 || msg.code() == 801) {
        //                if (msg.getBodyLength() <= 0) {
        //                    log.error("MessageEncoder code[{}]mid[{}]bodylen[{}] != data.readableBytes[{}]",
        //                            msg.getCode(), msg.getId(), msg.getBodyLength(), msg.getBodyReadableBytes());
        //                    log.error("MessageEncoder trace {}", printlnStackTrace());
        //                    throw new IllegalStateException("bodylen != data.readableBytes");
        //                }
        //            }
        //        }

        buf.writeInt(msg.getId());
        buf.writeInt(msg.getCode());
        buf.writeBytes(msg.getData());

        //        if (isDebug) {
        //            log.debug("send msg. msgId {} code {}. ret {} len {}",
        //                    msg.getId(), msg.code(), msg.getRet(), totalLen);
        //        }

        out.writeBytes(buf);
    }
}
