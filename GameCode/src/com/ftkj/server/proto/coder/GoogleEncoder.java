package com.ftkj.server.proto.coder;

import com.ftkj.server.GameSource;
import com.ftkj.server.proto.Response;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleEncoder implements ProtocolEncoder {
    private static final Logger log = LoggerFactory.getLogger(GoogleEncoder.class);
    private static int cachesize = 1024;

    public GoogleEncoder() {
    }

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        //synchronized(session){
        IoBuffer buffer;
        if (message instanceof String) {
            byte[] bytes = ((String) message).getBytes("UTF-8");
            buffer = IoBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            //			log.info(">>bytes[{}]",buffer.array());
            out.write(buffer);
            buffer.free();
        } else {
            Response res = (Response) message;
            byte bytes[] = res.getBytes();
            int size = bytes.length;
            buffer = IoBuffer.allocate(size + 13, false);
            buffer.putInt(size);//4
            buffer.putInt(res.getReqId());//4
            buffer.putInt(res.getServiceCode());//4
            buffer.put(res.isIszip() ? (byte) 1 : (byte) 0);
            buffer.put(bytes);
            buffer.flip();
            //			log.info(">>bytes[{}]",buffer.array());
            out.write(buffer);
            int len = buffer.limit();
            buffer.free();

            if (log.isDebugEnabled()) {
                Long teamid = (Long) session.getAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID);
                log.debug("resp >> tid {} code {} reqid {} len {} zip {} ret {} flen {}", teamid != null ? teamid : -1,
                        res.getServiceCode(), res.getReqId(), size, res.isIszip() ? 1 : 0, res.getRet(), len);
            }
        }
        //}
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
