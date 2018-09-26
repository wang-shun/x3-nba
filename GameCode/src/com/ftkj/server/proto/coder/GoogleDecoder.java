package com.ftkj.server.proto.coder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.proto.MoPB;
import com.ftkj.server.proto.Request;
import com.ftkj.util.ByteUtil;

public class GoogleDecoder extends CumulativeProtocolDecoder {
    private static final Logger log = LoggerFactory.getLogger(GoogleDecoder.class);
    private static int MAX_SIZE = 1024 * 10;


    public GoogleDecoder() {
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        if (in.markValue() > 0) {
            in.reset();
        } else {
            in.position(0);
        }
        //针对粘包少包处理
        if (in.remaining() > 0) {

            in.mark();//标记当前位置，以便reset
            //			log.info("有数据进来了数据长度为[{}]",in.remaining());
            if (in.remaining() < 12) {
                in.reset();
                return false;
            }
            byte[] sizeBytes = new byte[4];

            in.get(sizeBytes);
            int size = ByteUtil.byteToInt2(sizeBytes);
            //			log.info("请求进入->大小为["+size+"]");
            if (size > MAX_SIZE || size <= 0) {
                session.closeNow();
                return true;
            }
            if (in.remaining() < size + 8) {
                //				log.info("还有包没有接收到->大小为["+size+"]");
                in.reset();//字节数组归位
                return false;
            } else {
                //				log.info("数据接收完毕，准备解包->大小为["+in.remaining()+"]");               
                byte[] ridBytes = new byte[4]; 
                byte[] codeBytes = new byte[4];
                in.get(ridBytes);
                int rid = ByteUtil.byteToInt2(ridBytes);
                in.get(codeBytes);
                int code = ByteUtil.byteToInt2(codeBytes);
                //				log.info("decoder msgid {} code {}", rid, code);
                decodePacket(session, in, out, size, rid, code);
            }
        }
        in.free();
        return true;
    }

    /**
     * @param session 当前会话标识
     * @param in      数据缓存
     * @param out     协议解码输出器 
     * @param size    数据大小
     * @param reqId   请求编码
     * @param code    请求的协议编号
     */
    private void decodePacket(IoSession session, IoBuffer in, ProtocolDecoderOutput out, int size, int reqId, int code) {
        try {
            byte[] sizeBytes = new byte[size];
            in.get(sizeBytes);
            //			String s = "";
            //			for(int i = 0 ; i < sizeBytes.length ; i++){
            //				s+=Integer.toHexString(sizeBytes[i])+",";
            //			}
            //			logger.error("前端发送的字节数据--->"+s);
            //			InputStream   is = new ByteArrayInputStream(sizeBytes);
            //			data = Mo.MoData.parseFrom(new DataInputStream(is));
            MoPB.MoData data = MoPB.MoData.parseFrom(sizeBytes);
            //			logger.trace("前端发送的数据--->"+data.getMsg());
            Request req = new Request(session, data.getMsg(), true, reqId, code, size);
            if (log.isDebugEnabled()) {
                log.debug("decoder << tid {} code {} reqid {} len {} params [{}]",
                        req.getTeamId(), code, reqId, size, data.getMsg());
            }
            out.write(req);

            if (in.remaining() > 0) {
                in.mark();
                doDecode(session, in, out);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
