package com.ftkj;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
/**
 * @author Marc.Wang 2011-9-22 上午10:35:45
 * 功能：
 */
public class GoogleCodecFactoryTestCase implements ProtocolCodecFactory {
	private final GoogleEncoderTestCase encoder;
    private final GoogleDecoderTestCase decoder;
    //
	public GoogleCodecFactoryTestCase(ExcuteReturnData data) {
		 encoder = new GoogleEncoderTestCase();
         decoder = new GoogleDecoderTestCase(data);
	}

	public GoogleDecoderTestCase getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public GoogleEncoderTestCase getEncoder(IoSession session) throws Exception {
		return encoder;
	}
	
	
}
