package com.ftkj.client.coder;

import com.ftkj.client.ClientResponse;
import com.ftkj.util.ByteUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author tim.huang
 * 2016年12月28日
 * 客户端解码
 */
public class ClientGoogleDecoder extends CumulativeProtocolDecoder {
    private static final Logger log = LoggerFactory.getLogger(ClientGoogleDecoder.class);
	private final AttributeKey POLICY = new AttributeKey(getClass(), "policy");
	private final String security = "<policy-file-request/>";
	public static final String gamedate = "<game-data-change/>";
	public static final String gamestat = "<game-data-stat/>";
	public static final String gamegm   = "<gm/>";
	public static final String httpHeader = "GET / HTTP/1.1";
	public static final String clientXml = "<?xml version='1.0'?>";

	protected boolean doDecode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) throws Exception {
		if(isSecurityRequest(session,in,out)){
			//out.write(security);
			//in.free();
			return true;
		}

		if(in.markValue()>0){
			in.reset();
		}else{
			in.position(0);
		}

		//针对粘包少包处理
		if(in.remaining()>0){

			in.mark();//标记当前位置，以便reset
			if(in.remaining()<13){
				in.reset();
				return false;
			}
			byte [] sizeBytes = new byte[4];
//			log.info(">>String[{}]",new String(in.array(),"UTF-8"));
//			log.info(">>bytes[{}]",in.array());
			in.get(sizeBytes);
			int size = ByteUtil.byteToInt2(sizeBytes);
//			int size = in.getInt();
			in.get(sizeBytes);
			int rid = ByteUtil.byteToInt2(sizeBytes);
			in.get(sizeBytes);
			int keySize = ByteUtil.byteToInt2(sizeBytes);
			boolean zip = in.get()==1;
			if(in.remaining()<size){
				in.reset();//字节数组归位
				return false;
			}else{
				decodePacket(session,in,out,size,keySize,rid,zip);
			}
		}
		in.free();
		return true;
	}

	//解包
	private void decodePacket(IoSession session, IoBuffer in,ProtocolDecoderOutput out,int size,int key,int rid,boolean zip) {
		ClientResponse data = null;
		try {
			byte[] bytes = new byte[size];
			in.get(bytes);
			if(zip){
				bytes = ByteUtil.decompress(bytes);
			}
//			System.err.println("---------------------------->"+key);
			data = new ClientResponse(key, bytes,rid);
			out.write(data);
			//
			if(in.remaining()>0){
				in.mark();
				doDecode(session,in,out);
			}
		}catch (Exception e) {
            log.error(e.getMessage(), e);
		}
	}

	private boolean isSecurityRequest(IoSession session, IoBuffer in,ProtocolDecoderOutput out){
		Boolean policy = (Boolean)session.getAttribute(POLICY);
		//logger.info("=="+session.getRemoteAddress().toString()+"++"+policy);
		if(policy != null){
			return false;
		}
		String request = getRequest(in);
		if(request.length()>2){request = request.trim();}
		boolean result = false;
		if(request != null){
			result = request.startsWith(security);
			if(result)out.write(security);

			result = request.startsWith(gamegm);
			if(result)out.write(request);

			result = request.startsWith(gamedate);
			if(result)out.write(gamedate);

			result = request.startsWith(gamestat);
			if(result)out.write(gamestat);

			result = request.startsWith(httpHeader);

			result = request.startsWith(clientXml);
//			if(result)out.write(result);
			in.free();
		}
		session.setAttribute(POLICY,new Boolean(result));
		return result;
	}

	private String getRequest(IoBuffer in){
		byte[] bytes = new byte[in.limit()];
		in.get(bytes);
		String request;
		try {
			request = new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			request = null;
		}
		return request;
	}

}
