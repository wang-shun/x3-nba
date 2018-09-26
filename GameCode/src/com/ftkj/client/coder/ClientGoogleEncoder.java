package com.ftkj.client.coder;

import com.ftkj.client.ClientRequest;
import com.ftkj.proto.MoPB;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

/**
 * @author tim.huang
 * 2016年12月28日
 * 客户端编码
 */
public class ClientGoogleEncoder implements ProtocolEncoder {
    private static final Logger log = LoggerFactory.getLogger(ClientGoogleEncoder.class);
	private static int cachesize = 1024;


	public ClientGoogleEncoder(){
		
	}

	public void encode(IoSession session, Object message,ProtocolEncoderOutput out) throws Exception {		
		IoBuffer buffer;
		if(message instanceof ClientRequest){
			ClientRequest cr = (ClientRequest)message;
			MoPB.MoData data = MoPB.MoData.newBuilder().setMsg(cr.getData()).build();
			byte[] bytes = data.toByteArray();
			buffer = IoBuffer.allocate(bytes.length+12,false);
			buffer.putInt(bytes.length);//4
			buffer.putInt(cr.getReqId());//4
			buffer.putInt(cr.getServiceCode());//4
			buffer.put(bytes);//
			buffer.flip();
			out.write(buffer);
			buffer.free();
//			log.info("clietn send message length[{}],msg[{}].",bytes.length,(String)message);
		}else{
			log.debug("clietn send message error msg[{}].",(String)message);
		}
	}

	public void dispose(IoSession session) throws Exception {}

	public static byte[] compress(byte[] inputs){
		Deflater deflater = new Deflater();		
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
		//logger.error("--"+inputs.length+" | "+outputs.length);
		try {
			stream.close();
		} catch (IOException e) {
		}
		return outputs;
	}
}
