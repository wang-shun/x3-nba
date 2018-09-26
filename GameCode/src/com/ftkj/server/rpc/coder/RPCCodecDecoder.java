package com.ftkj.server.rpc.coder;

import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.rpc.coder.RPCSerializerFactory.X3Serializer;
import com.ftkj.util.ByteUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCCodecDecoder extends CumulativeProtocolDecoder {
    private static final Logger log = LoggerFactory.getLogger(RPCCodecDecoder.class);
    private static final int MAX_SIZE = 1024 * 1024;
    private final X3Serializer codec;

    public RPCCodecDecoder(X3Serializer codec) {
        this.codec = codec;
    }

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        if (in.markValue() > 0) {
            in.reset();
        } else {
            in.position(0);
        }
        //针对粘包少包处理
        if (in.remaining() > 0) {
            in.mark();//标记当前位置，以便reset
            //			logger.info("有数据进来了数据长度为[{}]",in.remaining());
            if (in.remaining() < 5) {
                in.reset();
                return false;
            }
            int size = in.getInt();
            //			logger.info("请求进入->大小为["+size+"]");
            if (size > MAX_SIZE) {
                log.warn("rpcdec. pack len {} too big, closed client {}", size, session);
                session.closeNow();
                return true;
            }
            if (in.remaining() < size + 1) {
                //				logger.info("还有包没有接收到->大小为["+size+"]");
                in.reset();//字节数组归位
                return false;
            } else {
                //				logger.info("数据接收完毕，准备解包->大小为["+in.remaining()+"]");
                //				byte [] ridBytes = new byte[4];
                //				byte [] codeBytes = new byte[4];
                //				in.get(ridBytes);
                //				int rid = ByteUtil.byteToInt2(ridBytes);
                //				in.get(codeBytes);
                //				int code = ByteUtil.byteToInt2(codeBytes);
                byte zip = in.get();
                decodePacket(session, in, out, size, zip);
            }
        }
        in.free();
        return true;
    }

    //解包
    private void decodePacket(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int size, byte zip) {
        try {
            byte[] sizeBytes = new byte[size];
            in.get(sizeBytes);
            if (zip == 2) {//节点注册消息，不走转发逻辑
                RPCServer source = codec.fromBinary(sizeBytes, RPCServer.class);
                out.write(source);
            } else {
                if (zip == 1) {
                    sizeBytes = ByteUtil.decompress(sizeBytes);
                }
                RPCSource rpc = codec.fromBinary(sizeBytes, RPCSource.class);
                if (rpc.getMethodCode() >= -1 && log.isDebugEnabled()) {
                    log.debug("rpcdec reqid {} code {} size {} sender {}",
                            rpc.getReqId(), rpc.getMethodCode(), size, rpc.getSender());
                }
                out.write(rpc);
            }
            if (in.remaining() > 0) {
                in.mark();
                doDecode(session, in, out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
