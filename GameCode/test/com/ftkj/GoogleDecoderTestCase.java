package com.ftkj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ftkj.proto.ByteUtil;
import com.ftkj.proto.Request;
/**
 * @author Marc.Wang 2011-9-22 下午04:38:01
 * 功能：
 */
public class GoogleDecoderTestCase extends CumulativeProtocolDecoder {
	private ExcuteReturnData excute;
	//
	public GoogleDecoderTestCase(ExcuteReturnData excute) {
		this.excute = excute;
	}

	protected boolean doDecode(IoSession session, IoBuffer in,
	ProtocolDecoderOutput out) throws Exception {
		if(!session.containsAttribute("sec")){
			session.setAttribute("sec", "sec");
			byte [] xb = new byte[in.remaining()];
			in.get(xb);
			in.free();
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
			in.get(sizeBytes);
			int size = ByteUtil.byteToInt2(sizeBytes);
			in.get(sizeBytes);
			int type = ByteUtil.byteToInt2(sizeBytes);
			in.get(sizeBytes);
			int reqid  = ByteUtil.byteToInt2(sizeBytes);
			byte iszip = in.get();
			if(in.remaining()<size){
				in.reset();//字节数组归位
				return false;
			}else{
				decodePacket(session,in,out,reqid,iszip,size,type);
			}
		}
		in.free();
		return true;
	}
	
	//解包
	private void decodePacket(IoSession session, IoBuffer in,
	ProtocolDecoderOutput out,int reqid,byte iszip, int size,int type) {
		try {
			Request req = new Request(session);
			Object obj = getData(type,in,iszip,size);
			req.setData(obj.toString());
			req.setType(type);
			req.setReqid(reqid);
			out.write(req);
			if(in.remaining()>0){
				in.mark();
				doDecode(session,in,out);
			}
		}catch (Exception e) {
            log.error(e.getMessage(), e);
		}
	}
	
	public Object getData(int type,IoBuffer in,byte iszip, int size)throws IOException{
		byte [] sizeBytes = new byte[size];  
		in.get(sizeBytes);
		InputStream is = new ByteArrayInputStream(sizeBytes);
		return excute.getObject(type,is,iszip==1);
	}
}
