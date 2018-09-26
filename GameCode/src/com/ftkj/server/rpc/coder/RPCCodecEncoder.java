package com.ftkj.server.rpc.coder;

import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.rpc.coder.RPCSerializerFactory.X3Serializer;
import com.ftkj.util.ByteUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCCodecEncoder implements ProtocolEncoder {
    private static final Logger log = LoggerFactory.getLogger(RPCCodecEncoder.class);
    private final X3Serializer codec;

    public RPCCodecEncoder(X3Serializer codec) {
        this.codec = codec;
    }

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
        if (message instanceof RPCSource) {
            RPCSource res = (RPCSource) message;
            byte[] bytes = codec.toBinary(res);
            byte zip = 0;
            if (bytes.length > 1024) {
                bytes = ByteUtil.compress(bytes);
                zip = 1;
            }
            int size = bytes.length;
            IoBuffer buffer = IoBuffer.allocate(size + 5, false);
            buffer.putInt(size);//4
            buffer.put(zip);//1
            buffer.put(bytes);
            buffer.flip();
            if (res.getMethodCode() >= -1 && log.isDebugEnabled()) {
                log.debug("rpcenc reqid {} code {} size {} receive {}",
                        res.getReqId(), res.getMethodCode(), size, res.getReceive());
            }
            out.write(buffer);
            buffer.free();
        } else if (message instanceof RPCServer) {
            RPCServer res = (RPCServer) message;
            byte bytes[] = codec.toBinary(res);
            byte zip = 2;
            int size = bytes.length;
            IoBuffer buffer = IoBuffer.allocate(size + 5, false);
            buffer.putInt(size);//4
            buffer.put(zip);//1
            buffer.put(bytes);
            buffer.flip();
            if (log.isInfoEnabled()) {
                log.info("rpcenc registe server size {} server {}", size, res.toString());
            }
            //			log.debug(">>ReqId["+res.getReqId()+"],ServiceCode["+res.getMethodCode()+"],ByteSize["+size+"]");
            out.write(buffer);
            buffer.free();
        }
    }

    public void dispose(IoSession session) throws Exception {
    }

	
	/*
	public static byte[] compress(byte[] data){

		byte[] temp = new byte[data.length];
		Deflater compresser = new Deflater();
		compresser.setInput(data);
		compresser.finish();
		int n = compresser.deflate(temp);
		if(!compresser.finished()){
			logger.error("----");
		}
		return Arrays.copyOf(temp, n);		
	}
	*/
}
