package com.ftkj;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ftkj.proto.Response;

/**
 * @author Marc.Wang 2011-9-22 下午06:22:08
 * 功能：
 */
public class GoogleEncoderTestCase implements ProtocolEncoder {
	
	static Logger logger = Logger.getLogger(GoogleEncoderTestCase.class);
		
	private static int cachesize = 1024;
	private final AttributeKey DEFLATER = new AttributeKey(getClass(), "deflater");	
	
	public void encode(IoSession session, Object message,ProtocolEncoderOutput out) throws Exception {		
		synchronized(session){
//			logger.info("发送数据包");
			IoBuffer buffer;
			if(message instanceof String){
				byte[] bytes = ((String)message).getBytes("UTF-8");
				buffer = IoBuffer.allocate(bytes.length);
				buffer.put(bytes);
				//buffer.put((byte)0x0);
				buffer.flip();
				out.write(buffer);
			}else{
				Response res = (Response)message;
				byte bytes[] = res.isIszip() ?compress(session,res.getBytes()):res.getBytes();
				buffer = IoBuffer.allocate(bytes.length+9,false);
				
				buffer.putInt(bytes.length);//4
//				buffer.putInt(res.getType().getService());//4
				buffer.putInt(res.getReqid());//4
				buffer.put(res.isIszip()?(byte)1:(byte)0);//1
				buffer.put(bytes);

				buffer.flip();
				out.write(buffer);
				buffer.free();
			}			
		}
	}
	
	public void dispose(IoSession session) throws Exception {
	
	}
	
	private byte[] compress(IoSession session,byte[] inputs){
		Deflater deflater = (Deflater)session.getAttribute(DEFLATER);
		if(deflater == null){
			deflater = new Deflater();
			session.setAttribute(DEFLATER,deflater);
		}
		deflater.reset();
		deflater.setInput(inputs);
		deflater.finish();
		byte outputs[] = new byte[0]; 
		ByteArrayOutputStream stream = new ByteArrayOutputStream(inputs.length); 
		byte[] bytes = new byte[cachesize];
		int value; 
		while(!deflater.finished()){
			value = deflater.deflate(bytes);
			stream.write(bytes,0, value);			
		}
		outputs = stream.toByteArray();
		try {
			stream.close();
		} catch (IOException e) {
            log.error(e.getMessage(), e);
		}
		return outputs;
	}

}
